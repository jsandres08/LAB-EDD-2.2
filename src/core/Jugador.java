/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

/**
 *
 * @author sarom
 */
public class Jugador {
  private String nombre;
    private RolJugador rol;
    private int puntuacion;
    private int reputacion;

    public Jugador(String nombre, RolJugador rol) {
        this.nombre = nombre;
        this.rol = rol;
        this.puntuacion = 0;
        this.reputacion = 50; 
    }

    public String getNombre() { 
        return nombre; 
    }
    public RolJugador getRol() { 
        return rol; 
    }
    public int getPuntuacion() {
        return puntuacion; 
    }
    public int getReputacion() { 
        return reputacion; 
    }

    public void sumarPuntos(int puntos) {
        this.puntuacion += puntos;
    }

    public void ajustarReputacion(int delta) {
        this.reputacion += delta;
    }

    @Override
    public String toString() {
        return nombre + " [" + rol + "] - Puntos: " + puntuacion + " | Reputación: " + reputacion;
    }  
}
