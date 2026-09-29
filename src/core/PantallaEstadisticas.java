package core;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import ui.BarraSuperior;
import ui.BotonJuego;
import ui.FondoJuego;
import ui.Iconos;
import ui.Indicador;
import ui.PanelTarjeta;
import ui.TablaJuego;
import ui.Tema;

class PantallaEstadisticas extends FondoJuego implements Pantalla {

    private static final String ACIERTO = "Acierto";
    private static final String FALLO = "Fallo";
    private static final String SIN_DECIDIR = "Sin decidir";

    private final GameEngine engine;
    private final Indicador indDecisiones = new Indicador("Jugadas", Iconos.Tipo.CAPAS, Tema.CIAN, true);
    private final Indicador indAciertos = new Indicador("Aciertos", Iconos.Tipo.CHECK, Tema.VERDE, true);
    private final Indicador indPrecision = new Indicador("Precisión", Iconos.Tipo.CENTRAR, Tema.AMBAR, true);
    private final Indicador indLider = new Indicador("Líder", Iconos.Tipo.TROFEO, Tema.VIOLETA, true);
    private final DefaultTableModel modeloRanking = modelo("#", "Jugador", "Rol", "Puntos", "Reputación", "Aciertos", "Fallos");
    private final DefaultTableModel modeloHistorial = modelo("#", "Jugador", "Publicación", "Ruta elegida", "Resultado");
    private final List<Color> coloresRanking = new ArrayList<>();
    private final List<Color> coloresHistorial = new ArrayList<>();
    private final CardLayout tarjetasHistorial = new CardLayout();
    private final JPanel contenedorHistorial = new JPanel(tarjetasHistorial);
    private final PanelTarjeta tarjetaRanking;

    PantallaEstadisticas(VentanaJuego ventana, GameEngine engine) {
        super(new BorderLayout(0, 16));
        this.engine = engine;
        setBorder(new EmptyBorder(20, 24, 20, 24));

        BotonJuego btnVolver = new BotonJuego("VOLVER", Iconos.Tipo.VOLVER, BotonJuego.Estilo.VOLVER);
        btnVolver.addActionListener(e -> ventana.volver());
        BarraSuperior barra = new BarraSuperior(btnVolver, Iconos.Tipo.ESTADISTICAS, Tema.AMBAR, "Estadísticas",
                "Ranking de jugadores y registro de todas las decisiones de la partida");
        add(barra, BorderLayout.NORTH);

        JPanel indicadores = new JPanel(new GridLayout(1, 4, 16, 0));
        indicadores.setOpaque(false);
        indicadores.add(indDecisiones);
        indicadores.add(indAciertos);
        indicadores.add(indPrecision);
        indicadores.add(indLider);

        JTable ranking = new JTable(modeloRanking);
        tarjetaRanking = new PanelTarjeta(new BorderLayout()).conTitulo("Ranking", Iconos.Tipo.TROFEO).conAcento(Tema.AMBAR);
        tarjetaRanking.add(TablaJuego.crear(ranking), BorderLayout.CENTER);
        TablaJuego.columna(ranking, 0, 60, TablaJuego.posicion());
        TablaJuego.columna(ranking, 1, 200, TablaJuego.avatar(coloresRanking::get));
        TablaJuego.columna(ranking, 2, 140, TablaJuego.insignia(EstiloJuego::colorRol));
        TablaJuego.columna(ranking, 3, 100, TablaJuego.numero(Tema.CIAN));
        TablaJuego.columna(ranking, 4, 230, TablaJuego.barra(100, EstiloJuego::colorReputacion));
        TablaJuego.columna(ranking, 5, 100, TablaJuego.numero(Tema.VERDE));
        TablaJuego.columna(ranking, 6, 100, TablaJuego.numero(Tema.MAGENTA));

        JTable historial = new JTable(modeloHistorial);
        PanelTarjeta tarjetaHistorial = new PanelTarjeta(new BorderLayout()).conTitulo("Historial de decisiones", Iconos.Tipo.RELOJ);
        contenedorHistorial.setOpaque(false);
        contenedorHistorial.add(TablaJuego.crear(historial), "tabla");
        JPanel vacio = new JPanel(new GridBagLayout());
        vacio.setOpaque(false);
        JLabel lblVacio = Tema.etiqueta("Aún no hay decisiones registradas. ¡Empieza a jugar!", Tema.texto(Font.BOLD, 15f), Tema.TEXTO_SUAVE);
        lblVacio.setIcon(Iconos.icono(Iconos.Tipo.INFO, 22, Tema.TEXTO_SUAVE));
        lblVacio.setIconTextGap(10);
        vacio.add(lblVacio);
        contenedorHistorial.add(vacio, "vacio");
        tarjetaHistorial.add(contenedorHistorial, BorderLayout.CENTER);
        TablaJuego.columna(historial, 0, 50, TablaJuego.texto(SwingConstants.CENTER));
        TablaJuego.columna(historial, 1, 170, TablaJuego.avatar(coloresHistorial::get));
        TablaJuego.columna(historial, 2, 330, null);
        TablaJuego.columna(historial, 3, 230, null);
        TablaJuego.columna(historial, 4, 150, TablaJuego.insignia(PantallaEstadisticas::colorResultado));

        JPanel tablas = new JPanel(new BorderLayout(0, 16));
        tablas.setOpaque(false);
        tablas.add(tarjetaRanking, BorderLayout.NORTH);
        tablas.add(tarjetaHistorial, BorderLayout.CENTER);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BorderLayout(0, 16));
        centro.add(indicadores, BorderLayout.NORTH);
        centro.add(tablas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static Color colorResultado(Object valor) {
        return switch (String.valueOf(valor)) {
            case ACIERTO -> Tema.VERDE;
            case FALLO -> Tema.MAGENTA;
            default -> Tema.GRIS;
        };
    }

    private static String resultado(RegistroDecision r) {
        if (r.getCorrecta() == null) {
            return SIN_DECIDIR;
        }
        return r.getCorrecta() ? ACIERTO : FALLO;
    }

    @Override
    public void alMostrar() {
        List<RegistroDecision> registros = engine.getHistorial();
        List<Jugador> jugadores = new ArrayList<>(engine.getTodosLosJugadores());
        jugadores.sort(Comparator.comparingInt(Jugador::getPuntuacion).reversed()
                .thenComparing(Comparator.comparingInt(Jugador::getReputacion).reversed()));

        int aciertos = 0;
        int fallos = 0;
        for (RegistroDecision r : registros) {
            if (Boolean.TRUE.equals(r.getCorrecta())) {
                aciertos++;
            } else if (Boolean.FALSE.equals(r.getCorrecta())) {
                fallos++;
            }
        }
        indDecisiones.setValor(String.valueOf(registros.size()), fallos + aciertos + " con resultado evaluado");
        indAciertos.setValor(String.valueOf(aciertos), fallos + " fallos");
        indPrecision.setValor(aciertos + fallos == 0 ? "—" : Math.round(aciertos * 100.0 / (aciertos + fallos)) + "%",
                "Aciertos sobre decisiones evaluadas");
        Jugador lider = jugadores.get(0);
        indLider.setValor(lider.getNombre(), lider.getPuntuacion() + " puntos · reputación " + lider.getReputacion());

        modeloRanking.setRowCount(0);
        coloresRanking.clear();
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            int propios = 0;
            int errores = 0;
            for (RegistroDecision r : registros) {
                if (r.getJugador() == j && Boolean.TRUE.equals(r.getCorrecta())) {
                    propios++;
                } else if (r.getJugador() == j && Boolean.FALSE.equals(r.getCorrecta())) {
                    errores++;
                }
            }
            coloresRanking.add(EstiloJuego.colorRol(j.getRol()));
            modeloRanking.addRow(new Object[]{i + 1, j.getNombre(), EstiloJuego.nombreRol(j.getRol()), j.getPuntuacion(),
                j.getReputacion(), propios, errores});
        }
        int altoRanking = 56 + 16 + 44 + jugadores.size() * 46 + 4;
        tarjetaRanking.setPreferredSize(new Dimension(0, altoRanking));

        modeloHistorial.setRowCount(0);
        coloresHistorial.clear();
        for (int i = 0; i < registros.size(); i++) {
            RegistroDecision r = registros.get(i);
            coloresHistorial.add(EstiloJuego.colorRol(r.getJugador().getRol()));
            modeloHistorial.addRow(new Object[]{i + 1, r.getJugador().getNombre(), r.getPublicacion(),
                r.getRuta().isEmpty() ? "—" : String.join(" › ", r.getRuta()), resultado(r)});
        }
        tarjetasHistorial.show(contenedorHistorial, registros.isEmpty() ? "vacio" : "tabla");
        revalidate();
        repaint();
    }
}
