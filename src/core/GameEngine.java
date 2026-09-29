package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador del juego.
 *
 * Flujo de una publicación:
 *   PUBLICACION -> (Verificar -> Sí/No) | Compartir | Ignorar  -> RESULTADO
 * Al llegar a un RESULTADO el juego muestra el resultado y ofrece "Continuar",
 * que salta a la siguiente publicación (o termina la partida si no hay más).
 * El turno pasa al siguiente jugador cuando se termina de resolver la publicación.
 */
public class GameEngine {

    public static final String OPCION_CONTINUAR = "Continuar";

    private Partida partida;
    private ArbolDecision arbolDecision;
    private ArbolClasi arbolClasificacion;

    private final List<RegistroDecision> historial = new ArrayList<>();
    private final List<String> rutaActual = new ArrayList<>();
    private String publicacionActual = "";
    private boolean esperandoContinuar = false;
    private boolean partidaTerminada = false;

    public GameEngine(Partida partida, ArbolDecision arbolDecision, ArbolClasi arbolClasificacion) {
        this.partida = partida;
        this.arbolDecision = arbolDecision;
        this.arbolClasificacion = arbolClasificacion;
    }

    // ------------------------------------------------------------------
    // Estado que consume la GUI
    // ------------------------------------------------------------------

    public Set<String> getOpcionesDisponibles() {
        if (partidaTerminada) {
            return new LinkedHashSet<>();
        }
        if (esperandoContinuar) {
            Set<String> unica = new LinkedHashSet<>();
            unica.add(OPCION_CONTINUAR);
            return unica;
        }
        return arbolDecision.getOpcionesDisponibles();
    }

    public String getTextoActual() {
        if (partidaTerminada) {
            StringBuilder sb = new StringBuilder("FIN DE LA PARTIDA\n\nPuntuaciones finales:\n");
            for (Jugador j : partida.getJugadores()) {
                sb.append("  ").append(j).append("\n");
            }
            return sb.toString();
        }
        NodoDecision nodo = arbolDecision.getNodoActual();
        if (esperandoContinuar) {
            Boolean ok = nodo.getCorrecto();
            String marca = (ok == null) ? "" : (ok ? "✔ Decisión correcta\n\n" : "✘ Decisión incorrecta\n\n");
            return marca + nodo.getTexto();
        }
        return nodo.getTexto();
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

    /** Decisiones ya tomadas (solo lo que el jugador ya vivió). */
    public List<RegistroDecision> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    /**
     * Decisión a medias: por ejemplo el jugador eligió "Verificar" y aún no
     * responde Sí/No. Devuelve null si no hay nada en curso.
     */
    public RegistroDecision getRegistroEnCurso() {
        if (partidaTerminada || esperandoContinuar) {
            return null;
        }
        if (arbolDecision.getNodoActual().getTipo() != TipoNodo.PREGUNTA || rutaActual.isEmpty()) {
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
        if (!arbolDecision.getOpcionesDisponibles().contains(opcion)) {
            return;
        }

        Jugador actual = partida.jugadorActual();
        NodoDecision anterior = arbolDecision.getNodoActual();
        if (anterior.getTipo() == TipoNodo.PUBLICACION) {
            publicacionActual = anterior.getTexto();
            rutaActual.clear();
        }

        arbolDecision.avanzar(opcion);
        NodoDecision nodo = arbolDecision.getNodoActual();
        aplicarConsecuencia(actual, opcion, nodo);
        rutaActual.add(opcion);

        if (nodo.getTipo() == TipoNodo.RESULTADO) {
            historial.add(new RegistroDecision(actual, publicacionActual, rutaActual,
                    nodo.getTexto(), nodo.getCorrecto()));
            esperandoContinuar = true;
            System.out.println(">> Resultado: " + nodo.getTexto());
        } else if (nodo.getTipo() == TipoNodo.PUBLICACION) {
            // "Siguiente publicación": omitió la anterior sin decidir
            historial.add(new RegistroDecision(actual, publicacionActual, rutaActual,
                    "Pasó a la siguiente publicación sin decidir.", null));
            partida.siguienteTurno();
        }
        // Si es PREGUNTA (Verificar), el mismo jugador responde Sí/No: no cambia el turno.
    }

    /** Sale de la pantalla de resultado: pasa el turno y va a la siguiente publicación. */
    private void continuar() {
        esperandoContinuar = false;
        partida.siguienteTurno();
        if (!arbolDecision.siguientePublicacion()) {
            partidaTerminada = true;
        }
    }

    /** Acción automática cuando se acaba el temporizador. */
    public void procesarTiempoAgotado() {
        Set<String> opciones = getOpcionesDisponibles();
        if (opciones.isEmpty()) {
            return;
        }
        if (esperandoContinuar) {
            procesarDecision(OPCION_CONTINUAR);
        } else if (opciones.contains("Ignorar")) {
            procesarDecision("Ignorar");
        } else if (opciones.contains("No")) {
            procesarDecision("No");
        } else {
            procesarDecision(opciones.iterator().next());
        }
    }

    private void aplicarConsecuencia(Jugador jugador, String opcion, NodoDecision nodo) {
        switch (opcion) {
            case "Compartir":
                jugador.ajustarReputacion(-5);
                break;
            case "Verificar":
                jugador.sumarPuntos(5);
                break;
            case "Reportar":
                jugador.sumarPuntos(10);
                jugador.ajustarReputacion(5);
                break;
            case "Sí":
                jugador.sumarPuntos(10);
                break;
            case "No":
                jugador.ajustarReputacion(-10);
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------
    // Árbol de clasificación (uso interno, no se muestra al usuario)
    // ------------------------------------------------------------------

    public void clasificarPublicacion(Publicacion pub) {
        arbolClasificacion.insertarPublicacion(pub);
    }

    public String verificarPublicacion(int idPublicacion) {
        return arbolClasificacion.verificar(idPublicacion);
    }

    public void reiniciarArbolDecision() {
        arbolDecision.reiniciar();
        historial.clear();
        rutaActual.clear();
        esperandoContinuar = false;
        partidaTerminada = false;
    }
}