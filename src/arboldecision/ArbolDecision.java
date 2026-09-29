package arboldecision;

import java.util.ArrayList;
import java.util.List;

public class ArbolDecision {

    private NodoDecision raiz;

    public ArbolDecision() {
        this.raiz = null;
    }

    public NodoDecision getRaiz() {
        return raiz;
    }

    public void insertar(String[] ruta, String opcion, NodoDecision nuevo) {
        if (this.raiz == null) {
            this.raiz = nuevo;
        } else {
            insertarRec(this.raiz, ruta, 0, opcion, nuevo);
        }
    }

    private void insertarRec(NodoDecision nodo, String[] ruta, int i, String opcion, NodoDecision nuevo) {
        if (nodo == null) {
            return;
        }
        if (i == ruta.length) {
            nodo.agregarHijo(opcion, nuevo);
            return;
        }
        insertarRec(nodo.obtenerHijo(ruta[i]), ruta, i + 1, opcion, nuevo);
    }

    public NodoDecision buscar(String[] ruta) {
        return buscarRec(this.raiz, ruta, 0);
    }

    private NodoDecision buscarRec(NodoDecision nodo, String[] ruta, int i) {
        if (nodo == null || i == ruta.length) {
            return nodo;
        }
        return buscarRec(nodo.obtenerHijo(ruta[i]), ruta, i + 1);
    }

    public void eliminar(String[] ruta) {
        if (ruta.length == 0) {
            this.raiz = null;
        } else {
            eliminarRec(this.raiz, ruta, 0);
        }
    }

    private void eliminarRec(NodoDecision nodo, String[] ruta, int i) {
        if (nodo == null) {
            return;
        }
        if (i == ruta.length - 1) {
            nodo.eliminarHijo(ruta[i]);
            return;
        }
        eliminarRec(nodo.obtenerHijo(ruta[i]), ruta, i + 1);
    }

    public List<NodoDecision> buscarPorTipo(TipoNodo tipo) {
        List<NodoDecision> resultados = new ArrayList<>();
        buscarPorTipoRec(this.raiz, tipo, resultados);
        return resultados;
    }

    private void buscarPorTipoRec(NodoDecision nodo, TipoNodo tipo, List<NodoDecision> resultados) {
        if (nodo == null) {
            return;
        }
        if (nodo.getTipo() == tipo) {
            resultados.add(nodo);
        }
        for (NodoDecision hijo : nodo.getHijos().values()) {
            buscarPorTipoRec(hijo, tipo, resultados);
        }
    }

    public List<NodoDecision> preorden() {
        List<NodoDecision> orden = new ArrayList<>();
        preordenRec(this.raiz, orden);
        return orden;
    }

    private void preordenRec(NodoDecision nodo, List<NodoDecision> orden) {
        if (nodo == null) {
            return;
        }
        orden.add(nodo);
        for (NodoDecision hijo : nodo.getHijos().values()) {
            preordenRec(hijo, orden);
        }
    }

    public List<NodoDecision> postorden() {
        List<NodoDecision> orden = new ArrayList<>();
        postordenRec(this.raiz, orden);
        return orden;
    }

    private void postordenRec(NodoDecision nodo, List<NodoDecision> orden) {
        if (nodo == null) {
            return;
        }
        for (NodoDecision hijo : nodo.getHijos().values()) {
            postordenRec(hijo, orden);
        }
        orden.add(nodo);
    }

    public void recorrerDFS() {
        dfs(this.raiz, "");
    }

    private void dfs(NodoDecision nodo, String sangria) {
        if (nodo == null) {
            return;
        }
        System.out.println(sangria + "- " + nodo.getTexto() + " [" + nodo.getTipo() + "]");
        for (String opcion : nodo.getHijos().keySet()) {
            System.out.println(sangria + "  Opcion: " + opcion);
            dfs(nodo.obtenerHijo(opcion), sangria + "    ");
        }
    }

    public int peso(NodoDecision nodo) {
        if (nodo == null) {
            return 0;
        }
        int total = 1;
        for (NodoDecision hijo : nodo.getHijos().values()) {
            total += peso(hijo);
        }
        return total;
    }

    public int hojas(NodoDecision nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.getHijos().isEmpty()) {
            return 1;
        }
        int total = 0;
        for (NodoDecision hijo : nodo.getHijos().values()) {
            total += hojas(hijo);
        }
        return total;
    }

    public int altura(NodoDecision nodo) {
        if (nodo == null) {
            return -1;
        }
        int max = -1;
        for (NodoDecision hijo : nodo.getHijos().values()) {
            max = Math.max(max, altura(hijo));
        }
        return max + 1;
    }

    public int grado(NodoDecision nodo) {
        if (nodo == null) {
            return 0;
        }
        int grado = nodo.getHijos().size();
        for (NodoDecision hijo : nodo.getHijos().values()) {
            grado = Math.max(grado, grado(hijo));
        }
        return grado;
    }
}
