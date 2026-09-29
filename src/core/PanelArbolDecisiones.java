package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Dibuja como un grafo de tipo árbol las decisiones que el jugador ya tomó:
 *
 *   Partida
 *    ├─ Publicación 1 ── Eligió: Verificar ── Eligió: Sí ── ✔ Resultado
 *    ├─ Publicación 2 ── Eligió: Ignorar ── ✔ Resultado
 *    └─ ...
 *
 * Solo dibuja lo elegido: no muestra las ramas que el jugador no tomó,
 * para no revelar las respuestas del juego.
 */
public class PanelArbolDecisiones extends JPanel {

    private static final int ANCHO = 170;
    private static final int ALTO = 84;
    private static final int SEP_X = 24;
    private static final int SEP_Y = 46;
    private static final int MARGEN = 24;
    private static final int TOPE = 44; // espacio para la leyenda

    private static final Color C_RAIZ = new Color(70, 80, 100);
    private static final Color C_PUBLICACION = new Color(190, 210, 245);
    private static final Color C_OPCION = new Color(225, 235, 250);
    private static final Color C_CORRECTO = new Color(190, 235, 190);
    private static final Color C_INCORRECTO = new Color(245, 195, 195);
    private static final Color C_NEUTRO = new Color(225, 225, 225);
    private static final Color C_PENDIENTE = new Color(255, 240, 190);

    private static class Nodo {
        String texto;
        Color color;
        boolean textoClaro;
        List<Nodo> hijos = new ArrayList<>();
        double col; // posición horizontal en "columnas"
        int nivel;

        Nodo(String texto, Color color) {
            this.texto = texto;
            this.color = color;
        }
    }

    private final Nodo raiz;
    private int columnas = 0;
    private int maxNivel = 0;

    public PanelArbolDecisiones(List<RegistroDecision> registros) {
        setBackground(Color.WHITE);
        raiz = construir(registros);
        raiz.textoClaro = true;
        posicionar(raiz, 0);

        int ancho = MARGEN * 2 + Math.max(1, columnas) * (ANCHO + SEP_X) - SEP_X;
        int alto = TOPE + MARGEN * 2 + (maxNivel + 1) * ALTO + maxNivel * SEP_Y;
        setPreferredSize(new Dimension(Math.max(ancho, 500), alto));
    }

    // ------------------------------------------------------------------
    // Construcción del árbol a partir del historial
    // ------------------------------------------------------------------
    private Nodo construir(List<RegistroDecision> registros) {
        Nodo raizArbol = new Nodo("INICIO DE LA PARTIDA", C_RAIZ);
        if (registros.isEmpty()) {
            raizArbol.hijos.add(new Nodo("Aún no has tomado ninguna decisión", C_NEUTRO));
            return raizArbol;
        }

        for (RegistroDecision r : registros) {
            Nodo publicacion = new Nodo(
                    r.getPublicacion() + "\n(" + r.getJugador().getNombre() + ")", C_PUBLICACION);
            raizArbol.hijos.add(publicacion);

            Nodo padre = publicacion;
            for (String paso : r.getRuta()) {
                Nodo opcion = new Nodo("Eligió: " + paso, C_OPCION);
                padre.hijos.add(opcion);
                padre = opcion;
            }

            if (r.getResultado() == null) {
                padre.hijos.add(new Nodo("… pendiente de responder", C_PENDIENTE));
            } else if (r.getCorrecta() == null) {
                padre.hijos.add(new Nodo("- Sin decidir\n" + r.getResultado(), C_NEUTRO));
            } else if (r.getCorrecta()) {
                padre.hijos.add(new Nodo("✔ Correcto\n" + r.getResultado(), C_CORRECTO));
            } else {
                padre.hijos.add(new Nodo("✘ Incorrecto\n" + r.getResultado(), C_INCORRECTO));
            }
        }
        return raizArbol;
    }

    /** Las hojas ocupan columnas consecutivas; cada padre se centra sobre sus hijos. */
    private void posicionar(Nodo n, int nivel) {
        n.nivel = nivel;
        maxNivel = Math.max(maxNivel, nivel);
        if (n.hijos.isEmpty()) {
            n.col = columnas++;
        } else {
            for (Nodo h : n.hijos) {
                posicionar(h, nivel + 1);
            }
            n.col = (n.hijos.get(0).col + n.hijos.get(n.hijos.size() - 1).col) / 2.0;
        }
    }

    // ------------------------------------------------------------------
    // Dibujo
    // ------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarLeyenda(g2);
        dibujarAristas(g2, raiz);
        dibujarNodos(g2, raiz);
        g2.dispose();
    }

    private int xIzq(Nodo n) {
        return (int) Math.round(MARGEN + n.col * (ANCHO + SEP_X));
    }

    private int ySup(Nodo n) {
        return TOPE + MARGEN + n.nivel * (ALTO + SEP_Y);
    }

    private void dibujarAristas(Graphics2D g2, Nodo n) {
        g2.setColor(new Color(110, 110, 120));
        g2.setStroke(new BasicStroke(1.8f));
        int px = xIzq(n) + ANCHO / 2;
        int py = ySup(n) + ALTO;
        for (Nodo h : n.hijos) {
            int hx = xIzq(h) + ANCHO / 2;
            int hy = ySup(h);
            int medio = (py + hy) / 2;
            // Conector en "codo": baja, cruza y vuelve a bajar
            g2.drawLine(px, py, px, medio);
            g2.drawLine(px, medio, hx, medio);
            g2.drawLine(hx, medio, hx, hy);
            dibujarAristas(g2, h);
        }
    }

    private void dibujarNodos(Graphics2D g2, Nodo n) {
        int x = xIzq(n);
        int y = ySup(n);

        g2.setColor(n.color);
        g2.fillRoundRect(x, y, ANCHO, ALTO, 16, 16);
        g2.setColor(n.color.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, ANCHO, ALTO, 16, 16);

        g2.setColor(n.textoClaro ? Color.WHITE : new Color(30, 30, 40));
        dibujarTextoCentrado(g2, n.texto, x, y);

        for (Nodo h : n.hijos) {
            dibujarNodos(g2, h);
        }
    }

    private void dibujarTextoCentrado(Graphics2D g2, String texto, int x, int y) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        List<String> lineas = new ArrayList<>();
        int maxAncho = ANCHO - 16;
        for (String parrafo : texto.split("\n")) {
            StringBuilder actual = new StringBuilder();
            for (String palabra : parrafo.split(" ")) {
                String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
                if (fm.stringWidth(prueba) > maxAncho && actual.length() > 0) {
                    lineas.add(actual.toString());
                    actual = new StringBuilder(palabra);
                } else {
                    actual = new StringBuilder(prueba);
                }
            }
            lineas.add(actual.toString());
        }

        int altoLinea = fm.getHeight();
        int maxLineas = Math.max(1, (ALTO - 10) / altoLinea);
        if (lineas.size() > maxLineas) {
            lineas = new ArrayList<>(lineas.subList(0, maxLineas));
            lineas.set(maxLineas - 1, lineas.get(maxLineas - 1) + "…");
        }

        int yTexto = y + (ALTO - lineas.size() * altoLinea) / 2 + fm.getAscent();
        for (String linea : lineas) {
            int xTexto = x + (ANCHO - fm.stringWidth(linea)) / 2;
            g2.drawString(linea, xTexto, yTexto);
            yTexto += altoLinea;
        }
    }

    private void dibujarLeyenda(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int x = MARGEN;
        int y = 12;
        x = leyenda(g2, x, y, C_CORRECTO, "Correcto");
        x = leyenda(g2, x, y, C_INCORRECTO, "Incorrecto");
        x = leyenda(g2, x, y, C_NEUTRO, "Sin decidir");
        leyenda(g2, x, y, C_PENDIENTE, "En curso");
    }

    private int leyenda(Graphics2D g2, int x, int y, Color color, String texto) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, 16, 16, 6, 6);
        g2.setColor(color.darker());
        g2.drawRoundRect(x, y, 16, 16, 6, 6);
        g2.setColor(new Color(30, 30, 40));
        g2.drawString(texto, x + 22, y + 13);
        return x + 22 + g2.getFontMetrics().stringWidth(texto) + 22;
    }
}
