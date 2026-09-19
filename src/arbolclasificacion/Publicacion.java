/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package arbolclasificacion;

/**
 *
 * @author henrytorres
 */
public class Publicacion {
    
    private int id;
    private String texto;
    private String autor;
    private boolean veracidad; 
    private String categoria;  

    public Publicacion(int id, String texto, String autor, boolean veracidad, String categoria) {
        this.id = id;
        this.texto = texto;
        this.autor = autor;
        this.veracidad = veracidad;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public boolean isVeracidad() { return veracidad; }
    public void setVeracidad(boolean veracidad) { this.veracidad = veracidad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}