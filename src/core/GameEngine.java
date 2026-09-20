/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sarom
 */
public class GameEngine {
   private Partida partida;
    private ArbolDecision arbolDecision;
    private ArbolClasi arbolClasificacion;

    public GameEngine(Partida partida, ArbolDecision arbolDecision, ArbolClasi arbolClasificacion) {
        this.partida = partida;
        this.arbolDecision = arbolDecision;
        this.arbolClasificacion = arbolClasificacion;
    }

    public Set<String> getOpcionesDisponibles() {
        return arbolDecision.getOpcionesDisponibles();
    }

    public String getTextoActual() {
        return arbolDecision.getNodoActual().getTexto();
    }

    public Jugador getJugadorActual() {
        return partida.jugadorActual();
    }
    
    public List<Jugador> getTodosLosJugadores() {
        return partida.getJugadores();
    }

    public void procesarDecision(String opcion) {
        Jugador actual = partida.jugadorActual();

        arbolDecision.avanzar(opcion);
        NodoDecision nodo = arbolDecision.getNodoActual();
        aplicarConsecuencia(actual, opcion, nodo);
        if (nodo.getTipo() == TipoNodo.RESULTADO) {
            System.out.println(">> Resultado: " + nodo.getTexto());
        }

        partida.siguienteTurno();
    }

    private void aplicarConsecuencia(Jugador jugador, String opcion, NodoDecision nodo) {
        switch (opcion) {
            case "Compartir":
                jugador.ajustarReputacion(-5);
                break;
            case "Verificar":
                jugador.sumarPuntos(5);
                break;
            case "Reportar":
                jugador.sumarPuntos(10);
                jugador.ajustarReputacion(5);
                break;
            case "Sí":
                jugador.sumarPuntos(10);
                break;
            case "No":
                jugador.ajustarReputacion(-10);
                break;
            default:
                break;
        }
    }

    public void clasificarPublicacion(Publicacion pub) {
        arbolClasificacion.insertarPublicacion(pub);
    }

    public String verificarPublicacion(int idPublicacion) {
        return arbolClasificacion.verificar(idPublicacion);
    }

    public void reiniciarArbolDecision() {
        arbolDecision.reiniciar();
    } 
      public String obtenerRecorridoArbolDecision() {
        return capturarSalidaConsola(() -> arbolDecision.recorrerDFS());
    }
 
    /**
     * Devuelve como texto el recorrido preorden del árbol de clasificación,
     * tal como lo imprime ArbolClasi.recorrerPreorden().
     */
    public String obtenerRecorridoArbolClasificacion() {
        return capturarSalidaConsola(() -> arbolClasificacion.recorrerPreorden());
    }
 
    /**
     * Redirige temporalmente System.out para capturar lo que imprime un
     * método existente (recorrerDFS, recorrerPreorden, etc.) y devolverlo
     * como String, sin tener que modificar esas clases de otros integrantes.
     */
    private String capturarSalidaConsola(Runnable accion) {
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            accion.run();
        } finally {
            System.setOut(salidaOriginal);
        }
        return buffer.toString();
    }
}
