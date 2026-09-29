package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class EtiquetaPixel extends JComponent {

    private String texto;
    private Color color;
    private Iconos.Tipo icono;
    private final int escala;

    public EtiquetaPixel(String texto, int escala, Color color) {
        this.texto = texto;
        this.escala = escala;
        this.color = color;
        setOpaque(false);
    }

    public void setTexto(String texto) {
        this.texto = texto;
        revalidate();
        repaint();
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    public void setIcono(Iconos.Tipo icono) {
        this.icono = icono;
        revalidate();
        repaint();
    }

    private int tamIcono() {
        return icono == null ? 0 : Tema.altoPixel(escala) - escala * 2;
    }

    @Override
    public Dimension getPreferredSize() {
        int extra = icono == null ? 0 : tamIcono() + escala * 4;
        return new Dimension(Tema.anchoPixel(texto, escala) + extra + escala, Tema.altoPixel(escala) + escala);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        int x = 0;
        if (icono != null) {
            int t = tamIcono();
            Iconos.dibujar(g2, icono, 0, (Tema.altoPixel(escala) - t) / 2.0, t, color);
            x = t + escala * 4;
        }
        Tema.textoPixel(g2, texto, x, 0, escala, color, true);
        g2.dispose();
    }
}
