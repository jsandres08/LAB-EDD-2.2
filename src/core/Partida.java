/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.util.List;

/**
 *
 * @author sarom
 */
public class Partida {
    private List<Jugador> jugadores;
    private int turnoActual;

    public Partida(List<Jugador> jugadores) {
        if (jugadores.size() < 2 || jugadores.size() > 4) {
            throw new IllegalArgumentException("Debe haber entre 2 y 4 jugadores");
        }
        this.jugadores = jugadores;
        this.turnoActual = 0;
    }

    public Jugador jugadorActual() {
        return jugadores.get(turnoActual);
    }

    public void siguienteTurno() {
        turnoActual = (turnoActual + 1) % jugadores.size();
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }
}
