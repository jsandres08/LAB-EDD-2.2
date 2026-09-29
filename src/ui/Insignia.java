package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class Insignia extends JComponent {

    private String texto;
    private Color color;
    private Iconos.Tipo icono;
    private final Font fuente;

    public Insignia(String texto, Color color, Iconos.Tipo icono) {
        this(texto, color, icono, 12f);
    }

    public Insignia(String texto, Color color, Iconos.Tipo icono, float tam) {
        this.texto = texto;
        this.color = color;
        this.icono = icono;
        this.fuente = Tema.texto(Font.BOLD, tam);
        setOpaque(false);
    }

    public void setDatos(String texto, Color color, Iconos.Tipo icono) {
        this.texto = texto;
        this.color = color;
        this.icono = icono;
        revalidate();
        repaint();
    }

    private int tamIcono() {
        return Math.round(fuente.getSize2D() + 3);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(fuente);
        int ancho = fm.stringWidth(texto.toUpperCase()) + 24 + (icono == null ? 0 : tamIcono() + 7);
        return new Dimension(ancho + 3, fm.getHeight() + 13);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        int w = getWidth() - 3;
        int h = getHeight() - 3;
        g2.setColor(Tema.mezclar(color, Color.BLACK, 0.25f));
        g2.fill(Tema.forma(0, 0, w, h, 4));
        g2.setColor(Tema.mezclar(Tema.FONDO_2, color, 0.18f));
        g2.fill(Tema.forma(2, 2, w - 4, h - 4, 3));
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();
        int x = 12;
        if (icono != null) {
            Iconos.dibujar(g2, icono, x, (h - tamIcono()) / 2.0, tamIcono(), color);
            x += tamIcono() + 7;
        }
        g2.setColor(Tema.mezclar(color, Color.BLACK, 0.45f));
        g2.drawString(texto.toUpperCase(), x, (h + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
