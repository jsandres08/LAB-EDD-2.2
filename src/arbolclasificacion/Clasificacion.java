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

    public class Clasificacion {
    private String categoria;
        private List<Publicacion> publicaciones;
        private List<Clasificacion> hijos; 

        public Clasificacion(String categoria) {
            this.categoria = categoria;
            this.publicaciones = new ArrayList<>();
            this.hijos = new ArrayList<>();
        }

        public Clasificacion buscarOEsCrearHijo(String nombreCategoria) {
            for (Clasificacion hijo : hijos) {
                if (hijo.getCategoria().equalsIgnoreCase(nombreCategoria)) {
                    return hijo; 
                }
            }

            Clasificacion nuevoHijo = new Clasificacion(nombreCategoria);
            hijos.add(nuevoHijo);
            return nuevoHijo;
        }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public List<Publicacion> getPublicaciones() { return publicaciones; }
        public void setPublicaciones(List<Publicacion> publicaciones) { this.publicaciones = publicaciones; }

        public List<Clasificacion> getHijos() { return hijos; }
        public void setHijos(List<Clasificacion> hijos) { this.hijos = hijos; }
    }
