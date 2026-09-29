package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Clasificacion;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import arboldecision.Consecuencia;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import core.EstadoCiudad.Indicador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameEngine {

    public static final String OPCION_CONTINUAR = "Continuar";

    public record Efecto(String jugador, int puntos, int reputacion, Map<Indicador, Integer> ciudad, List<String> notas) {
    }

    private static final int PUNTOS_VERIFICAR = 5;
    private static final int BONO_PERIODISTA = 5;
    private static final int BONO_RACHA = 5;
    private static final int REPUTACION_TIEMPO_AGOTADO = -5;
    private static final int DESINFORMACION_TIEMPO_AGOTADO = 3;
    private static final int SEGUNDOS_NORMAL = 10;
    private static final int SEGUNDOS_VIRAL = 6;
    private static final double PROBABILIDAD_VIRAL = 0.3;

    private Partida partida;
    private ArbolDecision arbolDecision;
    private ArbolClasi arbolClasificacion;
    private final Random azar;
    private final EstadoCiudad ciudad = new EstadoCiudad();

    private List<NodoDecision> publicaciones;
    private final Set<NodoDecision> virales = new HashSet<>();
    private final Map<Jugador, Integer> rachas = new HashMap<>();
    private NodoDecision nodoActual;
    private int indicePublicacion;

    private final List<RegistroDecision> historial = new ArrayList<>();
    private final List<String> rutaActual = new ArrayList<>();
    private String publicacionActual = "";
    private boolean esperandoContinuar = false;
    private boolean tiempoAgotado = false;
    private boolean partidaTerminada = false;
    private Efecto ultimoEfecto;

    public GameEngine(Partida partida, ArbolDecision arbolDecision, ArbolClasi arbolClasificacion) {
        this(partida, arbolDecision, arbolClasificacion, new Random());
    }

    public GameEngine(Partida partida, ArbolDecision arbolDecision, ArbolClasi arbolClasificacion, Random azar) {
        this.partida = partida;
        this.arbolDecision = arbolDecision;
        this.arbolClasificacion = arbolClasificacion;
        this.azar = azar;
        reiniciar();
    }

    public Set<String> getOpcionesDisponibles() {
        if (partidaTerminada) {
            return new LinkedHashSet<>();
        }
        if (esperandoContinuar) {
            Set<String> unica = new LinkedHashSet<>();
            unica.add(OPCION_CONTINUAR);
            return unica;
        }
        return new LinkedHashSet<>(nodoActual.getHijos().keySet());
    }

    public String getTextoActual() {
        if (partidaTerminada) {
            StringBuilder sb = new StringBuilder("FIN DE LA PARTIDA\n\nPuntuaciones finales:\n");
            for (Jugador j : partida.getJugadores()) {
                sb.append("  ").append(j).append("\n");
            }
            return sb.toString();
        }
        if (esperandoContinuar && tiempoAgotado) {
            return "Se agotó el tiempo (" + REPUTACION_TIEMPO_AGOTADO + " de reputación)\n\n" + publicacionActual;
        }
        if (esperandoContinuar) {
            Boolean ok = nodoActual.getCorrecto();
            String marca = (ok == null) ? "" : (ok ? "Decisión correcta\n\n" : "Decisión incorrecta\n\n");
            return marca + nodoActual.getTexto();
        }
        return nodoActual.getTexto();
    }

    public Jugador getJugadorActual() {
        return partida.jugadorActual();
    }

    public List<Jugador> getTodosLosJugadores() {
        return partida.getJugadores();
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public List<RegistroDecision> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    public List<RegistroDecision> getHistorialConEnCurso() {
        List<RegistroDecision> todo = new ArrayList<>(historial);
        RegistroDecision enCurso = getRegistroEnCurso();
        if (enCurso != null) {
            todo.add(enCurso);
        }
        return todo;
    }

    public Clasificacion getRaizClasificacion() {
        return arbolClasificacion.getRaiz();
    }

    public ArbolDecision getArbolDecision() {
        return arbolDecision;
    }

    public ArbolClasi getArbolClasificacion() {
        return arbolClasificacion;
    }

    public EstadoCiudad getCiudad() {
        return ciudad;
    }

    public NodoDecision getNodoActual() {
        return nodoActual;
    }

    public boolean isEsperandoContinuar() {
        return esperandoContinuar;
    }

    public boolean isTiempoAgotado() {
        return tiempoAgotado;
    }

    public String getPublicacionActual() {
        return publicacionActual;
    }

    public List<String> getRutaActual() {
        return Collections.unmodifiableList(rutaActual);
    }

    public int getIndicePublicacion() {
        return indicePublicacion;
    }

    public int getTotalPublicaciones() {
        return publicaciones.size();
    }

    public Efecto getUltimoEfecto() {
        return ultimoEfecto;
    }

    public int getRacha(Jugador jugador) {
        return rachas.getOrDefault(jugador, 0);
    }

    public boolean isViral() {
        return !partidaTerminada && virales.contains(publicaciones.get(indicePublicacion));
    }

    public int getSegundosLimite() {
        return isViral() ? SEGUNDOS_VIRAL : SEGUNDOS_NORMAL;
    }

    public Publicacion getPublicacionClasificada() {
        if (partidaTerminada) {
            return null;
        }
        return arbolClasificacion.buscarPorId(publicaciones.get(indicePublicacion).getIdPublicacion());
    }

    public String getResultadoVerificacion() {
        if (partidaTerminada || nodoActual.getTipo() != TipoNodo.PREGUNTA) {
            return null;
        }
        return arbolClasificacion.verificar(publicaciones.get(indicePublicacion).getIdPublicacion());
    }

    public Jugador getCandidato() {
        for (Jugador j : partida.getJugadores()) {
            if (j.getRol() == RolJugador.ALCALDE) {
                return j;
            }
        }
        return null;
    }

    public int getApoyoCandidato(Jugador candidato) {
        double ciudadana = (ciudad.valor(Indicador.CONFIANZA) + ciudad.valor(Indicador.VERIFICADA)
                + ciudad.valor(Indicador.CONVIVENCIA) + (100 - ciudad.valor(Indicador.CONFLICTOS))) / 4.0;
        double reputacion = Math.max(0, Math.min(100, candidato.getReputacion()));
        return (int) Math.round(ciudadana * 0.6 + reputacion * 0.4);
    }

    public RegistroDecision getRegistroEnCurso() {
        if (partidaTerminada || esperandoContinuar) {
            return null;
        }
        if (nodoActual.getTipo() != TipoNodo.PREGUNTA || rutaActual.isEmpty()) {
            return null;
        }
        return new RegistroDecision(partida.jugadorActual(), publicacionActual, rutaActual, null, null);
    }

    public void procesarDecision(String opcion) {
        if (partidaTerminada) {
            return;
        }
        if (esperandoContinuar) {
            continuar();
            return;
        }
        NodoDecision siguiente = nodoActual.obtenerHijo(opcion);
        if (siguiente == null) {
            return;
        }

        Jugador actual = partida.jugadorActual();
        marcarInicioSiEsPublicacion();
        avanzar(siguiente);
        aplicarConsecuencia(actual, opcion, nodoActual);
        rutaActual.add(opcion);

        if (nodoActual.getTipo() == TipoNodo.RESULTADO) {
            historial.add(new RegistroDecision(actual, publicacionActual, rutaActual,
                    nodoActual.getTexto(), nodoActual.getCorrecto()));
            esperandoContinuar = true;
        } else if (nodoActual.getTipo() == TipoNodo.PUBLICACION) {
            historial.add(new RegistroDecision(actual, publicacionActual, rutaActual,
                    "Pasó a la siguiente publicación sin decidir.", null));
            partida.siguienteTurno();
        }
    }

    public void procesarTiempoAgotado() {
        if (partidaTerminada) {
            return;
        }
        if (esperandoContinuar) {
            continuar();
            return;
        }
        Jugador actual = partida.jugadorActual();
        marcarInicioSiEsPublicacion();
        actual.ajustarReputacion(REPUTACION_TIEMPO_AGOTADO);
        rachas.put(actual, 0);
        Map<Indicador, Integer> deltas = new EnumMap<>(Indicador.class);
        int efectivo = ciudad.aplicar(Indicador.DESINFORMACION, DESINFORMACION_TIEMPO_AGOTADO * (isViral() ? 2 : 1));
        if (efectivo != 0) {
            deltas.put(Indicador.DESINFORMACION, efectivo);
        }
        ultimoEfecto = new Efecto(actual.getNombre(), 0, REPUTACION_TIEMPO_AGOTADO, deltas,
                List.of("Nadie actuó a tiempo y la publicación siguió circulando."));
        historial.add(new RegistroDecision(actual, publicacionActual, rutaActual,
                "Se agotó el tiempo.", false));
        tiempoAgotado = true;
        esperandoContinuar = true;
    }

    public final void reiniciar() {
        publicaciones = arbolDecision.buscarPorTipo(TipoNodo.PUBLICACION);
        virales.clear();
        for (int i = 1; i < publicaciones.size(); i++) {
            if (azar.nextDouble() < PROBABILIDAD_VIRAL) {
                virales.add(publicaciones.get(i));
            }
        }
        rachas.clear();
        ciudad.reiniciar();
        indicePublicacion = 0;
        nodoActual = publicaciones.isEmpty() ? arbolDecision.getRaiz() : publicaciones.get(0);
        historial.clear();
        rutaActual.clear();
        publicacionActual = "";
        ultimoEfecto = null;
        esperandoContinuar = false;
        tiempoAgotado = false;
        partidaTerminada = publicaciones.isEmpty();
    }

    private void avanzar(NodoDecision siguiente) {
        nodoActual = siguiente;
        int i = publicaciones.indexOf(siguiente);
        if (i >= 0) {
            indicePublicacion = i;
        }
    }

    private void continuar() {
        esperandoContinuar = false;
        tiempoAgotado = false;
        partida.siguienteTurno();
        if (indicePublicacion + 1 < publicaciones.size()) {
            indicePublicacion++;
            nodoActual = publicaciones.get(indicePublicacion);
        } else {
            partidaTerminada = true;
        }
    }

    private void marcarInicioSiEsPublicacion() {
        if (nodoActual.getTipo() == TipoNodo.PUBLICACION) {
            publicacionActual = nodoActual.getTexto();
            rutaActual.clear();
        }
    }

    private void aplicarConsecuencia(Jugador jugador, String opcion, NodoDecision nodo) {
        int puntos = 0;
        int reputacion = 0;
        Map<Indicador, Integer> deltas = new EnumMap<>(Indicador.class);
        List<String> notas = new ArrayList<>();

        if (CatalogoPublicaciones.VERIFICAR.equals(opcion)) {
            puntos += PUNTOS_VERIFICAR;
            if (jugador.getRol() == RolJugador.PERIODISTA) {
                puntos += BONO_PERIODISTA;
                notas.add("Habilidad de Periodista: +" + BONO_PERIODISTA + " pts extra por investigar.");
            }
        }

        Consecuencia c = nodo.getConsecuencia();
        if (nodo.getTipo() == TipoNodo.RESULTADO && c != null) {
            puntos += c.puntos();
            reputacion += c.reputacion();
            int viral = isViral() ? 2 : 1;
            boolean habilidadUsada = false;
            for (Indicador indicador : Indicador.values()) {
                int base = valorBase(c, indicador);
                if (base == 0) {
                    continue;
                }
                int multiplicador = multiplicador(jugador.getRol(), indicador);
                habilidadUsada |= multiplicador > 1;
                int efectivo = ciudad.aplicar(indicador, base * multiplicador * viral);
                if (efectivo != 0) {
                    deltas.put(indicador, efectivo);
                }
            }
            if (viral > 1) {
                notas.add("Información viral: el impacto en la ciudad se duplicó.");
            }
            if (habilidadUsada) {
                notas.add(descripcionHabilidad(jugador.getRol()));
            }
            if (Boolean.TRUE.equals(nodo.getCorrecto())) {
                int racha = rachas.getOrDefault(jugador, 0) + 1;
                rachas.put(jugador, racha);
                if (racha >= 2) {
                    int bono = BONO_RACHA * (racha - 1);
                    puntos += bono;
                    notas.add("¡Racha de " + racha + " aciertos! +" + bono + " pts de bonificación.");
                }
            } else {
                rachas.put(jugador, 0);
            }
        }

        jugador.sumarPuntos(puntos);
        jugador.ajustarReputacion(reputacion);
        ultimoEfecto = new Efecto(jugador.getNombre(), puntos, reputacion, deltas, notas);
    }

    private static int valorBase(Consecuencia c, Indicador indicador) {
        return switch (indicador) {
            case VERIFICADA -> c.verificada();
            case CONFIANZA -> c.confianza();
            case CONVIVENCIA -> c.convivencia();
            case BIENESTAR -> c.bienestar();
            case DESINFORMACION -> c.desinformacion();
            case CONFLICTOS -> c.conflictos();
        };
    }

    static int multiplicador(RolJugador rol, Indicador indicador) {
        return switch (rol) {
            case INFLUENCER -> 2;
            case CIUDADANO -> indicador == Indicador.CONVIVENCIA || indicador == Indicador.BIENESTAR ? 2 : 1;
            case PERIODISTA -> indicador == Indicador.VERIFICADA ? 2 : 1;
            case ALCALDE -> indicador == Indicador.CONFIANZA ? 2 : 1;
        };
    }

    static String descripcionHabilidad(RolJugador rol) {
        return switch (rol) {
            case INFLUENCER -> "Habilidad de Influencer: sus decisiones tienen el doble de impacto en la ciudad.";
            case CIUDADANO -> "Habilidad de Ciudadano: duplica su efecto en convivencia y bienestar digital.";
            case PERIODISTA -> "Habilidad de Periodista: duplica la información verificada y gana +5 pts al verificar.";
            case ALCALDE -> "Habilidad de Candidato: duplica su efecto en la confianza ciudadana.";
        };
    }

    public void clasificarPublicacion(Publicacion pub) {
        arbolClasificacion.insertarPublicacion(pub);
    }

    public String verificarPublicacion(int idPublicacion) {
        return arbolClasificacion.verificar(idPublicacion);
    }
}
