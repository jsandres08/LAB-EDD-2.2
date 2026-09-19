/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sarom
 */
public class Main {
    public static void main(String[] args) {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Ana", RolJugador.CIUDADANO));
        jugadores.add(new Jugador("Luis", RolJugador.PERIODISTA));
        jugadores.add(new Jugador("Pedro", RolJugador.INFLUENCER));
        // jugadores.add(new Jugador("María", RolJugador.ALCALDE)); // opcional, hasta 4

        Partida partida = new Partida(jugadores);

        // 2. Instanciar los árboles ya implementados por Persona 1 y Persona 2
        ArbolDecision arbolDecision = new ArbolDecision();
        ArbolClasi arbolClasificacion = new ArbolClasi();

        // 3. Cargar algunas publicaciones de ejemplo en el árbol de clasificación
        arbolClasificacion.insertarPublicacion(
                new Publicacion(1, "Beber cloro elimina virus.", "Anónimo", false, "Rumor"));
        arbolClasificacion.insertarPublicacion(
                new Publicacion(2, "Suspenden clases mañana.", "Vecino", false, "Rumor"));
        arbolClasificacion.insertarPublicacion(
                new Publicacion(3, "Nueva vacuna aprobada.", "Alcaldía", true, "Salud"));

        // 4. Instanciar el motor del juego
        GameEngine engine = new GameEngine(partida, arbolDecision, arbolClasificacion);

        // 5. Flujo de prueba por consola (esto lo reemplaza la GUI de Persona 3)
        System.out.println("=== INICIO DE PARTIDA ===");
        System.out.println("Turno de: " + engine.getJugadorActual());
        System.out.println("Publicación actual: " + engine.getTextoActual());
        System.out.println("Opciones: " + engine.getOpcionesDisponibles());

        engine.procesarDecision("Verificar");
        System.out.println("\nSiguiente pregunta: " + engine.getTextoActual());
        System.out.println("Opciones: " + engine.getOpcionesDisponibles());

        engine.procesarDecision("Sí");

        System.out.println("\n=== ESTADO DE JUGADORES ===");
        for (Jugador j : partida.getJugadores()) {
            System.out.println(j);
        }

        System.out.println("\n=== VERIFICACIÓN EN ÁRBOL DE CLASIFICACIÓN ===");
        System.out.println("Publicación #1 clasificada como: " + engine.verificarPublicacion(1));

        System.out.println("\n=== RECORRIDOS DE AMBOS ÁRBOLES ===");
        arbolDecision.recorrerDFS();
        arbolClasificacion.recorrerPreorden();
    }
}
