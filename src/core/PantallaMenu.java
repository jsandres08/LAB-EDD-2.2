package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import ui.BotonJuego;
import ui.EtiquetaPixel;
import ui.FondoJuego;
import ui.Iconos;
import ui.Insignia;
import ui.PanelTarjeta;
import ui.Tema;

class PantallaMenu extends FondoJuego implements Pantalla {

    private final GameEngine engine;
    private final BotonJuego btnJugar;
    private final EtiquetaPixel lblPista;

    PantallaMenu(VentanaJuego ventana, GameEngine engine) {
        super(new BorderLayout());
        this.engine = engine;
        setBorder(new EmptyBorder(28, 36, 22, 36));

        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        TituloJuego titulo = new TituloJuego();
        centrar(titulo);
        columna.add(titulo);
        columna.add(Box.createVerticalStrut(22));

        btnJugar = new BotonJuego("JUGAR", Iconos.Tipo.CIUDAD, BotonJuego.Estilo.LISTA);
        btnJugar.addActionListener(e -> ventana.mostrar(VentanaJuego.JUEGO));
        BotonJuego btnArboles = new BotonJuego("ÁRBOLES", Iconos.Tipo.ARBOL, BotonJuego.Estilo.LISTA).conAcento(Tema.VERDE);
        btnArboles.addActionListener(e -> ventana.mostrar(VentanaJuego.ARBOLES));
        BotonJuego btnEstadisticas = new BotonJuego("ESTADÍSTICAS", Iconos.Tipo.ESTADISTICAS, BotonJuego.Estilo.LISTA)
                .conAcento(Tema.AMBAR);
        btnEstadisticas.addActionListener(e -> ventana.mostrar(VentanaJuego.ESTADISTICAS));
        BotonJuego btnAyuda = new BotonJuego("AYUDA", Iconos.Tipo.AYUDA, BotonJuego.Estilo.LISTA).conAcento(Tema.VIOLETA);
        btnAyuda.addActionListener(e -> ventana.mostrar(VentanaJuego.AYUDA));
        BotonJuego btnSalir = new BotonJuego("SALIR", Iconos.Tipo.SALIR, BotonJuego.Estilo.LISTA).conAcento(Tema.MAGENTA);
        btnSalir.addActionListener(e -> ventana.salir());

        PanelTarjeta menu = new PanelTarjeta(new GridLayout(0, 1, 0, 2)).conAcento(Tema.AZUL);
        menu.setBorder(new EmptyBorder(14, 14, 14, 14));
        menu.add(btnJugar);
        menu.add(btnArboles);
        menu.add(btnEstadisticas);
        menu.add(btnAyuda);
        menu.add(btnSalir);
        menu.setPreferredSize(new Dimension(420, 5 * 54 + 36));
        menu.setMaximumSize(menu.getPreferredSize());
        centrar(menu);
        columna.add(menu);
        columna.add(Box.createVerticalStrut(18));

        lblPista = new EtiquetaPixel("PULSA ENTER PARA JUGAR", 2, Tema.TEXTO);
        PanelTarjeta pista = new PanelTarjeta(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0)).conAcento(Tema.AMBAR);
        pista.setBorder(new EmptyBorder(12, 22, 10, 22));
        pista.add(lblPista);
        pista.setMaximumSize(pista.getPreferredSize());
        centrar(pista);
        columna.add(pista);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(columna);
        add(centro, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(0, 12));
        abajo.setOpaque(false);
        abajo.add(new Dialogo("¡Bienvenidos a Ciudad Nova! Se acercan las elecciones y en Civitas circula de todo. "
                + "Decidan qué hacer con cada publicación y protejan a la ciudad de la desinformación."), BorderLayout.CENTER);
        abajo.add(crearPie(), BorderLayout.SOUTH);
        add(abajo, BorderLayout.SOUTH);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "jugar");
        getActionMap().put("jugar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    btnJugar.doClick();
                }
            }
        });
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JPanel jugadores = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        jugadores.setOpaque(false);
        jugadores.add(Tema.etiqueta("JUGADORES", Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.15f), Tema.TEXTO_SUAVE));
        for (Jugador j : engine.getTodosLosJugadores()) {
            jugadores.add(new Insignia(j.getNombre() + " · " + EstiloJuego.nombreRol(j.getRol()),
                    EstiloJuego.colorRol(j.getRol()), Iconos.Tipo.USUARIO, 11f));
        }
        pie.add(jugadores, BorderLayout.WEST);
        JLabel version = Tema.etiqueta(engine.getTotalPublicaciones() + " PUBLICACIONES · PRIMERA ENTREGA · EDD II",
                Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.15f), Tema.TEXTO_SUAVE);
        version.setHorizontalAlignment(SwingConstants.RIGHT);
        pie.add(version, BorderLayout.EAST);
        return pie;
    }

    private static void centrar(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    public void alMostrar() {
        if (engine.isPartidaTerminada()) {
            btnJugar.setText("VER RESULTADOS");
        } else if (!engine.getHistorial().isEmpty() || !engine.getRutaActual().isEmpty()) {
            btnJugar.setText("CONTINUAR PARTIDA");
        } else {
            btnJugar.setText("JUGAR");
        }
        lblPista.setTexto("PULSA ENTER PARA " + (btnJugar.getText().equals("JUGAR") ? "JUGAR" : "CONTINUAR"));
        btnJugar.setActivo(true);
    }

    private static class TituloJuego extends JComponent {

        private static final int ESCALA = 9;

        TituloJuego() {
            setPreferredSize(new Dimension(1000, Tema.altoPixel(ESCALA) + 84));
            setMaximumSize(getPreferredSize());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            String texto = "ALCALDE DIGITAL";
            int ancho = Tema.anchoPixel(texto, ESCALA);
            int x = (getWidth() - ancho) / 2;
            int y = 10;
            int grosor = ESCALA / 2 + 2;
            for (int dx = -grosor; dx <= grosor; dx += 2) {
                for (int dy = -grosor; dy <= grosor + 4; dy += 2) {
                    Tema.textoPixel(g2, texto, x + dx, y + dy, ESCALA, Tema.LOGO_BORDE, false);
                }
            }
            Tema.textoPixel(g2, texto, x, y + 3, ESCALA, new Color(0xC89010), false);
            Tema.textoPixel(g2, texto, x, y, ESCALA, Tema.LOGO, false);

            String version = "EDICIÓN CIVITAS";
            int escala = 3;
            int av = Tema.anchoPixel(version, escala) + 28;
            int vx = (getWidth() - av) / 2;
            int vy = y + Tema.altoPixel(ESCALA) + 12;
            Tema.ventana(g2, vx, vy, av, Tema.altoPixel(escala) + 16, Tema.MAGENTA, Tema.MAGENTA);
            Tema.textoPixel(g2, version, vx + 14, vy + 8, escala, Tema.FONDO_2, false);
            g2.dispose();
        }
    }

    private static class Dialogo extends JComponent {

        private final String texto;

        Dialogo(String texto) {
            this.texto = texto;
            setPreferredSize(new Dimension(800, 92));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            Tema.ventana(g2, 0, 0, w, h, Tema.AZUL, Tema.FONDO_2);
            g2.setFont(Tema.texto(Font.BOLD, 17f));
            FontMetrics fm = g2.getFontMetrics();
            int y = 32;
            for (String linea : Tema.envolver(texto, fm, w - 70, 2)) {
                g2.setColor(Tema.SOMBRA);
                g2.drawString(linea, 26, y + 2);
                g2.setColor(Tema.TEXTO);
                g2.drawString(linea, 24, y);
                y += fm.getHeight() + 6;
            }
            int tx = w - 36;
            int ty = h - 28;
            g2.setColor(Tema.MAGENTA);
            g2.fillPolygon(new int[]{tx, tx + 14, tx + 7}, new int[]{ty, ty, ty + 9}, 3);
            g2.dispose();
        }
    }
}
