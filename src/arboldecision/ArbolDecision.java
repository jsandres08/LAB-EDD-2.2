  package arboldecision;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArbolDecision {

    private NodoDecision raiz, nodoActual;
    private final List<NodoDecision> publicaciones = new ArrayList<>();
    private int indicePublicacion = 0;

    public ArbolDecision() {
        cargar();
        nodoActual = raiz;
    }

    public NodoDecision getNodoActual() {
        return nodoActual;
    }

    public void reiniciar() {
        nodoActual = raiz;
        indicePublicacion = 0;
    }

    public void avanzar(String op) {
        NodoDecision hijo = nodoActual.obtenerHijo(op);
        if (hijo != null) {
            nodoActual = hijo;
            int i = publicaciones.indexOf(hijo);
            if (i >= 0) {
                indicePublicacion = i;
            }
        }
    }

    public Set<String> getOpcionesDisponibles() {
        return nodoActual.getHijos().keySet();
    }

    public boolean hayMasPublicaciones() {
        return indicePublicacion + 1 < publicaciones.size();
    }

    public boolean siguientePublicacion() {
        if (!hayMasPublicaciones()) {
            return false;
        }
        indicePublicacion++;
        nodoActual = publicaciones.get(indicePublicacion);
        return true;
    }

    public void insertar(String[] ruta, String opcion, NodoDecision nuevo) {
        NodoDecision a = raiz;
        for (String r : ruta) {
            a = a.obtenerHijo(r);
            if (a == null) return;
        }
        a.agregarHijo(opcion, nuevo);
    }

    public void eliminar(String[] ruta) {
        NodoDecision a = raiz;
        for (int i = 0; i < ruta.length - 1; i++) {
            a = a.obtenerHijo(ruta[i]);
            if (a == null) return;
        }
        a.eliminarHijo(ruta[ruta.length - 1]);
    }

    public void recorrerDFS() {
        dfs(raiz, "");
    }

    private void dfs(NodoDecision n, String s) {
        if (n == null) return;
        System.out.println(s + "- " + n.getTexto() + " [" + n.getTipo() + "]");
        for (String op : n.getHijos().keySet()) {
            System.out.println(s + "  Opcion: " + op);
            dfs(n.obtenerHijo(op), s + "    ");
        }
    }

    // En los RESULTADO, el tercer parámetro indica si la decisión fue correcta.
    private void cargar() {
        // ---------- PUBLICACIÓN 1 (FALSA) ----------
        raiz = new NodoDecision("PUBLICACION 1: Beber cloro elimina virus.", TipoNodo.PUBLICACION);
        NodoDecision verificar = new NodoDecision("¿Deseas verificar la información?", TipoNodo.PREGUNTA);
        raiz.agregarHijo("Verificar", verificar);
        raiz.agregarHijo("Compartir", new NodoDecision("Compartiste información falsa.", TipoNodo.RESULTADO, false));
        raiz.agregarHijo("Ignorar", new NodoDecision("Ignoraste la publicación.", TipoNodo.RESULTADO, true));
        verificar.agregarHijo("Sí", new NodoDecision("Correcto: era falsa.", TipoNodo.RESULTADO, true));
        verificar.agregarHijo("No", new NodoDecision("Caíste en la desinformación.", TipoNodo.RESULTADO, false));
        publicaciones.add(raiz);

        // ---------- PUBLICACIÓN 2 (RUMOR) ----------
        NodoDecision p2 = new NodoDecision("PUBLICACION 2: Suspenden clases mañana.", TipoNodo.PUBLICACION);
        raiz.agregarHijo("Siguiente publicación", p2);
        NodoDecision fuente = new NodoDecision("¿Consultar fuente oficial?", TipoNodo.PREGUNTA);
        p2.agregarHijo("Verificar", fuente);
        p2.agregarHijo("Compartir", new NodoDecision("Generaste pánico.", TipoNodo.RESULTADO, false));
        p2.agregarHijo("Ignorar", new NodoDecision("Ignoraste un rumor.", TipoNodo.RESULTADO, true));
        fuente.agregarHijo("Sí", new NodoDecision("Era un rumor.", TipoNodo.RESULTADO, true));
        fuente.agregarHijo("No", new NodoDecision("Creíste el rumor.", TipoNodo.RESULTADO, false));
        publicaciones.add(p2);

        // ---------- PUBLICACIÓN 3 (VERDADERA) ----------
        NodoDecision p3 = new NodoDecision("PUBLICACION 3: Nueva vacuna aprobada.", TipoNodo.PUBLICACION);
        p2.agregarHijo("Siguiente publicación", p3);
        NodoDecision oficial = new NodoDecision("¿Consultar página oficial?", TipoNodo.PREGUNTA);
        p3.agregarHijo("Verificar", oficial);
        p3.agregarHijo("Compartir", new NodoDecision("Compartiste una noticia verdadera.", TipoNodo.RESULTADO, true));
        p3.agregarHijo("Ignorar", new NodoDecision("Ignoraste una noticia verdadera.", TipoNodo.RESULTADO, false));
        oficial.agregarHijo("Sí", new NodoDecision("Confirmaste la noticia.", TipoNodo.RESULTADO, true));
        oficial.agregarHijo("No", new NodoDecision("No verificaste la fuente.", TipoNodo.RESULTADO, false));
        publicaciones.add(p3);

        // ---------- PUBLICACIÓN 4 (FAKE NEWS) ----------
        NodoDecision p4 = new NodoDecision("PUBLICACION 4: Murió un famoso.", TipoNodo.PUBLICACION);
        p3.agregarHijo("Siguiente publicación", p4);
        NodoDecision noticias = new NodoDecision("¿Buscar noticias confiables?", TipoNodo.PREGUNTA);
        p4.agregarHijo("Verificar", noticias);
        p4.agregarHijo("Compartir", new NodoDecision("Era una fake news.", TipoNodo.RESULTADO, false));
        p4.agregarHijo("Ignorar", new NodoDecision("Ignoraste una fake news.", TipoNodo.RESULTADO, true));
        noticias.agregarHijo("Sí", new NodoDecision("Descubriste el montaje.", TipoNodo.RESULTADO, true));
        noticias.agregarHijo("No", new NodoDecision("Caíste en la fake news.", TipoNodo.RESULTADO, false));
        publicaciones.add(p4);
    }
}