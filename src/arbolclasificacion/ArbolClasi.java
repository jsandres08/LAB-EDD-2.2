package arbolclasificacion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henrytorres
 */
public class ArbolClasi {

    private Clasificacion raiz;

    public ArbolClasi() {
        this.raiz = null;
    }

    public Clasificacion getRaiz() {
        return raiz;
    }

    public void setRaiz(Clasificacion raiz) {
        this.raiz = raiz;
    }

    public void insertarPublicacion(Publicacion pub) {
        if (this.raiz == null) {
            this.raiz = new Clasificacion("TODAS");
        }
        insertarRec(this.raiz, pub, 0);
    }

    private void insertarRec(Clasificacion nodo, Publicacion pub, int nivel) {
        if (nivel == 2) {
            nodo.getPublicaciones().add(pub);
            return;
        }
        String rama;
        if (nivel == 0) {
            rama = pub.isVeracidad() ? "VERDADERA" : "FALSA";
        } else {
            rama = pub.getCategoria();
        }
        insertarRec(buscarOCrearHijo(nodo, rama), pub, nivel + 1);
    }

    private Clasificacion buscarOCrearHijo(Clasificacion nodo, String categoria) {
        for (Clasificacion hijo : nodo.getHijos()) {
            if (hijo.getCategoria().equalsIgnoreCase(categoria)) {
                return hijo;
            }
        }
        Clasificacion nuevo = new Clasificacion(categoria);
        nodo.getHijos().add(nuevo);
        return nuevo;
    }

    public boolean eliminarPublicacion(int id) {
        return eliminarRec(this.raiz, id);
    }

    private boolean eliminarRec(Clasificacion nodo, int id) {
        if (nodo == null) {
            return false;
        }
        for (int i = 0; i < nodo.getPublicaciones().size(); i++) {
            if (nodo.getPublicaciones().get(i).getId() == id) {
                nodo.getPublicaciones().remove(i);
                return true;
            }
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            if (eliminarRec(hijo, id)) {
                return true;
            }
        }
        return false;
    }

    public List<Publicacion> buscarPorCategoria(String categoria) {
        List<Publicacion> resultados = new ArrayList<>();
        buscarRec(this.raiz, categoria, resultados);
        return resultados;
    }

    private void buscarRec(Clasificacion nodo, String categoria, List<Publicacion> resultados) {
        if (nodo == null) {
            return;
        }
        if (nodo.getCategoria().equalsIgnoreCase(categoria)) {
            resultados.addAll(nodo.getPublicaciones());
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            buscarRec(hijo, categoria, resultados);
        }
    }

    public String verificar(int id) {
        if (this.raiz == null) {
            return null;
        }
        return verificarRec(this.raiz, id, this.raiz.getCategoria());
    }

    private String verificarRec(Clasificacion nodo, int id, String ruta) {
        if (nodo == null) {
            return null;
        }
        for (Publicacion p : nodo.getPublicaciones()) {
            if (p.getId() == id) {
                return ruta;
            }
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            String resultado = verificarRec(hijo, id, ruta + " > " + hijo.getCategoria());
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    public Publicacion buscarPorId(int id) {
        return buscarPorIdRec(this.raiz, id);
    }

    private Publicacion buscarPorIdRec(Clasificacion nodo, int id) {
        if (nodo == null) {
            return null;
        }
        for (Publicacion p : nodo.getPublicaciones()) {
            if (p.getId() == id) {
                return p;
            }
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            Publicacion encontrada = buscarPorIdRec(hijo, id);
            if (encontrada != null) {
                return encontrada;
            }
        }
        return null;
    }

    public List<Clasificacion> preorden() {
        List<Clasificacion> orden = new ArrayList<>();
        preordenLista(this.raiz, orden);
        return orden;
    }

    private void preordenLista(Clasificacion nodo, List<Clasificacion> orden) {
        if (nodo == null) {
            return;
        }
        orden.add(nodo);
        for (Clasificacion hijo : nodo.getHijos()) {
            preordenLista(hijo, orden);
        }
    }

    public List<Clasificacion> postorden() {
        List<Clasificacion> orden = new ArrayList<>();
        postordenLista(this.raiz, orden);
        return orden;
    }

    private void postordenLista(Clasificacion nodo, List<Clasificacion> orden) {
        if (nodo == null) {
            return;
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            postordenLista(hijo, orden);
        }
        orden.add(nodo);
    }

    public void recorrerPreorden() {
        preordenRec(this.raiz, 0);
    }

    private void preordenRec(Clasificacion nodo, int nivel) {
        if (nodo == null) {
            return;
        }
        for (int i = 0; i < nivel; i++) {
            System.out.print("    ");
        }
        System.out.println("- [" + nodo.getCategoria() + "]");
        for (Publicacion p : nodo.getPublicaciones()) {
            for (int i = 0; i <= nivel; i++) {
                System.out.print("    ");
            }
            System.out.println("[Pub #" + p.getId() + ": \"" + p.getTexto() + "\"]");
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            preordenRec(hijo, nivel + 1);
        }
    }

    public int peso(Clasificacion nodo) {
        if (nodo == null) {
            return 0;
        }
        int total = 1;
        for (Clasificacion hijo : nodo.getHijos()) {
            total += peso(hijo);
        }
        return total;
    }

    public int hojas(Clasificacion nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.getHijos().isEmpty()) {
            return 1;
        }
        int total = 0;
        for (Clasificacion hijo : nodo.getHijos()) {
            total += hojas(hijo);
        }
        return total;
    }

    public int altura(Clasificacion nodo) {
        if (nodo == null) {
            return -1;
        }
        int max = -1;
        for (Clasificacion hijo : nodo.getHijos()) {
            max = Math.max(max, altura(hijo));
        }
        return max + 1;
    }

    public int grado(Clasificacion nodo) {
        if (nodo == null) {
            return 0;
        }
        int grado = nodo.getHijos().size();
        for (Clasificacion hijo : nodo.getHijos()) {
            grado = Math.max(grado, grado(hijo));
        }
        return grado;
    }

    public int contarPublicaciones(Clasificacion nodo) {
        if (nodo == null) {
            return 0;
        }
        int total = nodo.getPublicaciones().size();
        for (Clasificacion hijo : nodo.getHijos()) {
            total += contarPublicaciones(hijo);
        }
        return total;
    }
}
