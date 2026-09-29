package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Clasificacion;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import arboldecision.Consecuencia;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import ui.NodoVisual;
import ui.Tema;

final class ModeloArboles {

    record Metricas(int nodos, int hojas, int altura, int grado) {
    }

    private ModeloArboles() {
    }

    static NodoVisual deDecisiones(ArbolDecision arbol) {
        if (arbol.getRaiz() == null) {
            return null;
        }
        return nodoDecision(arbol, arbol.getRaiz(), new ArrayList<>());
    }

    static Metricas metricasDecisiones(ArbolDecision arbol) {
        NodoDecision r = arbol.getRaiz();
        return new Metricas(arbol.peso(r), arbol.hojas(r), arbol.altura(r), arbol.grado(r));
    }

    private static NodoVisual nodoDecision(ArbolDecision arbol, NodoDecision nodo, List<String> ruta) {
        NodoVisual v = new NodoVisual(categoria(nodo.getTipo()), nodo.getTexto(), colorDecision(nodo)).conOrigen(nodo);
        v.dato("Tipo de nodo", nodo.getTipo());
        if (nodo.getIdPublicacion() >= 0) {
            v.dato("ID de la publicación", nodo.getIdPublicacion());
        }
        v.dato("Ruta desde la raíz", ruta.isEmpty() ? "(es la raíz)" : String.join(" › ", ruta));
        if (nodo.getTipo() == TipoNodo.RESULTADO) {
            v.dato("Decisión", nodo.getCorrecto() == null ? "Sin evaluar" : nodo.getCorrecto() ? "Correcta" : "Incorrecta");
            if (nodo.getConsecuencia() != null) {
                v.dato("Consecuencias (valores base)", describir(nodo.getConsecuencia()));
            }
        }
        v.dato("Opciones disponibles", nodo.getHijos().isEmpty() ? "Ninguna (hoja)" : String.join(", ", nodo.getHijos().keySet()));
        v.dato("Nodos del subárbol", arbol.peso(nodo));
        v.dato("Hojas del subárbol", arbol.hojas(nodo));
        v.dato("Altura del subárbol", arbol.altura(nodo));
        for (Map.Entry<String, NodoDecision> e : nodo.getHijos().entrySet()) {
            ruta.add(e.getKey());
            v.agregarHijo(nodoDecision(arbol, e.getValue(), ruta), e.getKey());
            ruta.remove(ruta.size() - 1);
        }
        return v;
    }

    private static String describir(Consecuencia c) {
        List<String> partes = new ArrayList<>();
        agregar(partes, "Puntos", c.puntos());
        agregar(partes, "Reputación", c.reputacion());
        agregar(partes, "Verificada", c.verificada());
        agregar(partes, "Confianza", c.confianza());
        agregar(partes, "Convivencia", c.convivencia());
        agregar(partes, "Bienestar", c.bienestar());
        agregar(partes, "Desinformación", c.desinformacion());
        agregar(partes, "Conflictos", c.conflictos());
        return partes.isEmpty() ? "Sin cambios" : String.join(", ", partes);
    }

    private static void agregar(List<String> partes, String nombre, int valor) {
        if (valor != 0) {
            partes.add(nombre + " " + (valor > 0 ? "+" : "") + valor);
        }
    }

    private static String categoria(TipoNodo tipo) {
        return switch (tipo) {
            case RAIZ -> "RAÍZ";
            case PUBLICACION -> "PUBLICACIÓN";
            case PREGUNTA -> "PREGUNTA";
            case RESULTADO -> "RESULTADO";
        };
    }

    private static Color colorDecision(NodoDecision nodo) {
        return switch (nodo.getTipo()) {
            case RAIZ -> Tema.VIOLETA;
            case PUBLICACION -> Tema.CIAN;
            case PREGUNTA -> Tema.AMBAR;
            case RESULTADO -> nodo.getCorrecto() == null ? Tema.GRIS : nodo.getCorrecto() ? Tema.VERDE : Tema.MAGENTA;
        };
    }

    static NodoVisual deClasificacion(ArbolClasi arbol) {
        if (arbol.getRaiz() == null) {
            return null;
        }
        return nodoClasificacion(arbol, arbol.getRaiz(), 0);
    }

    static Metricas metricasClasificacion(ArbolClasi arbol) {
        Clasificacion r = arbol.getRaiz();
        return new Metricas(arbol.peso(r), arbol.hojas(r), arbol.altura(r), arbol.grado(r));
    }

    private static NodoVisual nodoClasificacion(ArbolClasi arbol, Clasificacion c, int nivel) {
        String categoria;
        Color color;
        if (nivel == 0) {
            categoria = "RAÍZ";
            color = Tema.VIOLETA;
        } else if (nivel == 1) {
            categoria = "VERACIDAD";
            color = "VERDADERA".equalsIgnoreCase(c.getCategoria()) ? Tema.VERDE : Tema.MAGENTA;
        } else {
            categoria = "CATEGORÍA";
            color = Tema.AZUL;
        }
        NodoVisual v = new NodoVisual(categoria, c.getCategoria(), color).conOrigen(c);
        v.dato("Subcategorías", c.getHijos().size());
        v.dato("Publicaciones en este nodo", c.getPublicaciones().size());
        v.dato("Publicaciones en el subárbol", arbol.contarPublicaciones(c));
        v.dato("Nodos del subárbol", arbol.peso(c));
        v.dato("Altura del subárbol", arbol.altura(c));
        for (Clasificacion hijo : c.getHijos()) {
            v.agregarHijo(nodoClasificacion(arbol, hijo, nivel + 1), null);
        }
        for (Publicacion p : c.getPublicaciones()) {
            NodoVisual pv = new NodoVisual("PUBLICACIÓN #" + p.getId(), p.getTexto(), Tema.CIAN).conOrigen(p);
            pv.dato("ID", p.getId());
            pv.dato("Autor", p.getAutor());
            pv.dato("Veracidad", p.isVeracidad() ? "Verdadera" : "Falsa");
            pv.dato("Categoría", p.getCategoria());
            pv.dato("Ubicación (verificar)", arbol.verificar(p.getId()));
            pv.dato("Guardada en", "Lista de publicaciones del nodo \"" + c.getCategoria() + "\"");
            v.agregarHijo(pv, null);
        }
        return v;
    }

    static NodoVisual deRecorrido(List<Jugador> jugadores, List<RegistroDecision> registros) {
        NodoVisual raiz = new NodoVisual("PARTIDA", "Inicio de la partida", Tema.VIOLETA);
        raiz.dato("Jugadores", jugadores.size());
        raiz.dato("Jugadas registradas", registros.size());
        Map<Jugador, NodoVisual> nodosJugador = new IdentityHashMap<>();
        for (Jugador j : jugadores) {
            int jugadas = 0;
            int aciertos = 0;
            int fallos = 0;
            for (RegistroDecision r : registros) {
                if (r.getJugador() == j) {
                    jugadas++;
                    if (Boolean.TRUE.equals(r.getCorrecta())) {
                        aciertos++;
                    } else if (Boolean.FALSE.equals(r.getCorrecta())) {
                        fallos++;
                    }
                }
            }
            NodoVisual nodo = new NodoVisual("JUGADOR · " + EstiloJuego.nombreRol(j.getRol()).toUpperCase(),
                    j.getNombre(), EstiloJuego.colorRol(j.getRol()));
            nodo.dato("Rol", EstiloJuego.nombreRol(j.getRol()));
            nodo.dato("Puntos", j.getPuntuacion());
            nodo.dato("Reputación", j.getReputacion());
            nodo.dato("Jugadas", jugadas);
            nodo.dato("Aciertos / fallos", aciertos + " / " + fallos);
            raiz.agregarHijo(nodo, null);
            nodosJugador.put(j, nodo);
        }
        for (int i = 0; i < registros.size(); i++) {
            RegistroDecision r = registros.get(i);
            Jugador j = r.getJugador();
            String rutaTexto = r.getRuta().isEmpty() ? "(ninguna)" : String.join(" › ", r.getRuta());
            NodoVisual publicacion = new NodoVisual("JUGADA " + (i + 1), r.getPublicacion(), Tema.CIAN);
            publicacion.dato("Jugador", j.getNombre());
            publicacion.dato("Turno en la partida", i + 1);
            publicacion.dato("Decisiones tomadas", rutaTexto);
            nodosJugador.get(j).agregarHijo(publicacion, null);

            NodoVisual padre = publicacion;
            for (String paso : r.getRuta()) {
                NodoVisual opcion = new NodoVisual("DECISIÓN", "Eligió: " + paso, Tema.AZUL);
                opcion.dato("Jugador", j.getNombre());
                opcion.dato("Opción elegida", paso);
                padre.agregarHijo(opcion, paso);
                padre = opcion;
            }

            String categoria;
            Color color;
            if (r.getResultado() == null) {
                categoria = "EN CURSO";
                color = Tema.AMBAR;
            } else if (r.getCorrecta() == null) {
                categoria = "SIN DECIDIR";
                color = Tema.GRIS;
            } else if (r.getCorrecta()) {
                categoria = "ACIERTO";
                color = Tema.VERDE;
            } else {
                categoria = "FALLO";
                color = Tema.MAGENTA;
            }
            NodoVisual fin = new NodoVisual(categoria,
                    r.getResultado() == null ? "Pendiente de responder" : r.getResultado(), color);
            fin.dato("Estado", categoria);
            fin.dato("Jugador", j.getNombre());
            fin.dato("Ruta", rutaTexto);
            padre.agregarHijo(fin, null);
        }
        return raiz;
    }

    static Map<NodoVisual, Integer> numerar(NodoVisual raiz, List<?> recorrido) {
        Map<Object, NodoVisual> porOrigen = new IdentityHashMap<>();
        indexar(raiz, porOrigen);
        Map<NodoVisual, Integer> orden = new HashMap<>();
        int paso = 1;
        for (Object o : recorrido) {
            NodoVisual v = o instanceof NodoVisual nv ? nv : porOrigen.get(o);
            if (v != null) {
                orden.put(v, paso);
            }
            paso++;
        }
        return orden;
    }

    private static void indexar(NodoVisual v, Map<Object, NodoVisual> porOrigen) {
        if (v.getOrigen() != null) {
            porOrigen.put(v.getOrigen(), v);
        }
        for (NodoVisual h : v.getHijos()) {
            indexar(h, porOrigen);
        }
    }

    static List<NodoVisual> preorden(NodoVisual raiz) {
        List<NodoVisual> orden = new ArrayList<>();
        preordenRec(raiz, orden);
        return orden;
    }

    private static void preordenRec(NodoVisual n, List<NodoVisual> orden) {
        orden.add(n);
        for (NodoVisual h : n.getHijos()) {
            preordenRec(h, orden);
        }
    }

    static List<NodoVisual> postorden(NodoVisual raiz) {
        List<NodoVisual> orden = new ArrayList<>();
        postordenRec(raiz, orden);
        return orden;
    }

    private static void postordenRec(NodoVisual n, List<NodoVisual> orden) {
        for (NodoVisual h : n.getHijos()) {
            postordenRec(h, orden);
        }
        orden.add(n);
    }

    static Metricas metricas(NodoVisual raiz) {
        return new Metricas(nodos(raiz), hojas(raiz), altura(raiz), grado(raiz));
    }

    private static int nodos(NodoVisual n) {
        int total = 1;
        for (NodoVisual h : n.getHijos()) {
            total += nodos(h);
        }
        return total;
    }

    private static int hojas(NodoVisual n) {
        if (n.getHijos().isEmpty()) {
            return 1;
        }
        int total = 0;
        for (NodoVisual h : n.getHijos()) {
            total += hojas(h);
        }
        return total;
    }

    private static int altura(NodoVisual n) {
        int max = -1;
        for (NodoVisual h : n.getHijos()) {
            max = Math.max(max, altura(h));
        }
        return max + 1;
    }

    private static int grado(NodoVisual n) {
        int max = n.getHijos().size();
        for (NodoVisual h : n.getHijos()) {
            max = Math.max(max, grado(h));
        }
        return max;
    }
}
