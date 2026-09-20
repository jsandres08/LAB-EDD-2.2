/*
 * Punto de entrada gráfico. Reemplaza el flujo de prueba por consola
 * de Main.java: en vez de imprimir texto, abre la ventana Swing
 * (InterfazJuego) que consume el mismo GameEngine.
 */
package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author sarom
 */
public class MainGUI {

    public static void main(String[] args) {
        // 1. Jugadores (igual que en Main.java original)
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Ana", RolJugador.CIUDADANO));
        jugadores.add(new Jugador("Luis", RolJugador.PERIODISTA));
        jugadores.add(new Jugador("Pedro", RolJugador.INFLUENCER));
        // jugadores.add(new Jugador("María", RolJugador.ALCALDE)); // opcional, hasta 4

        Partida partida = new Partida(jugadores);

        // 2. Árboles ya implementados por Persona 1 y Persona 2
        ArbolDecision arbolDecision = new ArbolDecision();
        ArbolClasi arbolClasificacion = new ArbolClasi();

        // 3. Publicaciones de ejemplo
        arbolClasificacion.insertarPublicacion(
                new Publicacion(1, "Beber cloro elimina virus.", "Anónimo", false, "Rumor"));
        arbolClasificacion.insertarPublicacion(
                new Publicacion(2, "Suspenden clases mañana.", "Vecino", false, "Rumor"));
        arbolClasificacion.insertarPublicacion(
                new Publicacion(3, "Nueva vacuna aprobada.", "Alcaldía", true, "Salud"));

        // 4. Motor del juego (Persona 4)
        GameEngine engine = new GameEngine(partida, arbolDecision, arbolClasificacion);

        // 5. Lanzar la GUI (Persona 3) en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaJuego ventana = new VentanaJuego(engine);
            ventana.setVisible(true);
        });
    }
}
