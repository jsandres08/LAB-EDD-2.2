package ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodoVisual {

    private final String categoria;
    private final String titulo;
    private final Color color;
    private final Map<String, String> info = new LinkedHashMap<>();
    private final List<NodoVisual> hijos = new ArrayList<>();
    private NodoVisual padre;
    private String etiquetaArista;
    private Object origen;
    int nivel;
    int fila;
    double x;
    double y;

    public NodoVisual(String categoria, String titulo, Color color) {
        this.categoria = categoria;
        this.titulo = titulo;
        this.color = color;
    }

    public NodoVisual agregarHijo(NodoVisual hijo, String etiqueta) {
        hijo.padre = this;
        hijo.etiquetaArista = etiqueta;
        hijos.add(hijo);
        return hijo;
    }

    public NodoVisual conOrigen(Object origen) {
        this.origen = origen;
        return this;
    }

    public Object getOrigen() {
        return origen;
    }

    public NodoVisual dato(String clave, Object valor) {
        info.put(clave, String.valueOf(valor));
        return this;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public Color getColor() {
        return color;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public List<NodoVisual> getHijos() {
        return hijos;
    }

    public NodoVisual getPadre() {
        return padre;
    }

    public String getEtiquetaArista() {
        return etiquetaArista;
    }

    public int getNivel() {
        return nivel;
    }
}
