/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        this.raiz = new Clasificacion("TODAS");
    }

    public Clasificacion getRaiz() { return raiz; }
    public void setRaiz(Clasificacion raiz) { this.raiz = raiz; }

    public void insertarPublicacion(Publicacion pub) {
        String ramaVeracidad = pub.isVeracidad() ? "VERDADERA" : "FALSA";
        Clasificacion nodoVeracidad = raiz.buscarOEsCrearHijo(ramaVeracidad);
        Clasificacion nodoCategoria = nodoVeracidad.buscarOEsCrearHijo(pub.getCategoria());
        nodoCategoria.getPublicaciones().add(pub);
    }

    public boolean eliminarPublicacion(int id) {
        return eliminarRec(this.raiz, id);
    }

    private boolean eliminarRec(Clasificacion nodo, int id) {

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

    public List<Publicacion> buscarPorCategoria(String nombreCategoria) {
        List<Publicacion> resultados = new ArrayList<>();
        buscarRec(this.raiz, nombreCategoria, resultados);
        return resultados;
    }

    private void buscarRec(Clasificacion nodo, String catBuscada, List<Publicacion> resultados) {
        if (nodo.getCategoria().equalsIgnoreCase(catBuscada)) {
            resultados.addAll(nodo.getPublicaciones());
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            buscarRec(hijo, catBuscada, resultados);
        }
    }
    public String verificar(int id) {
        return verificarRec(this.raiz, id, "TODAS");
    }

    private String verificarRec(Clasificacion nodo, int id, String rutaActual) {
        for (Publicacion p : nodo.getPublicaciones()) {
            if (p.getId() == id) {
                return rutaActual;
            }
        }
        for (Clasificacion hijo : nodo.getHijos()) {
            String resultado = verificarRec(hijo, id, rutaActual + " > " + hijo.getCategoria());
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    public void recorrerPreorden() {
        preordenRec(this.raiz, 0);
    }

    private void preordenRec(Clasificacion nodo, int nivel) {
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
}
