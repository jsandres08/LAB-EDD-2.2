package core;

import arbolclasificacion.ArbolClasi;
import arboldecision.ArbolDecision;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;
import ui.Tema;

/**
 *
 * @author sarom
 */
public class MainGUI {

    private static final int PUBLICACIONES_POR_PARTIDA = 8;

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "off");
        System.setProperty("swing.aatext", "false");
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Ana", RolJugador.CIUDADANO));
        jugadores.add(new Jugador("Luis", RolJugador.PERIODISTA));
        jugadores.add(new Jugador("Pedro", RolJugador.INFLUENCER));
        jugadores.add(new Jugador("María", RolJugador.ALCALDE));

        Partida partida = new Partida(jugadores);
        Random azar = new Random();

        ArbolDecision arbolDecision = new ArbolDecision();
        ArbolClasi arbolClasificacion = new ArbolClasi();
        CatalogoPublicaciones.cargar(arbolDecision, arbolClasificacion, azar, PUBLICACIONES_POR_PARTIDA);

        GameEngine engine = new GameEngine(partida, arbolDecision, arbolClasificacion, azar);

        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            VentanaJuego ventana = new VentanaJuego(engine);
            ventana.setVisible(true);
        });
    }
}
