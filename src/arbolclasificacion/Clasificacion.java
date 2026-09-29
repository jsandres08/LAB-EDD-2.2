package arbolclasificacion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henrytorres
 */
public class Clasificacion {

    private String categoria;
    private List<Publicacion> publicaciones;
    private List<Clasificacion> hijos;

    public Clasificacion(String categoria) {
        this.categoria = categoria;
        this.publicaciones = new ArrayList<>();
        this.hijos = new ArrayList<>();
    }

    public String getCategoria() {
        return categoria;
    }

    public List<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public List<Clasificacion> getHijos() {
        return hijos;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPublicaciones(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public void setHijos(List<Clasificacion> hijos) {
        this.hijos = hijos;
    }
}
