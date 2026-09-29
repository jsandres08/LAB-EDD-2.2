package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PanelTarjeta extends JPanel {

    private static final int ALTO_PESTANA = 30;
    private static final int DESPLAZAMIENTO = 15;

    private String titulo;
    private Iconos.Tipo icono;
    private Color acento = Tema.CIAN;

    public PanelTarjeta(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(16, 18, 16, 18));
    }

    public PanelTarjeta conTitulo(String titulo, Iconos.Tipo icono) {
        this.titulo = titulo;
        this.icono = icono;
        setBorder(new EmptyBorder(DESPLAZAMIENTO + ALTO_PESTANA + 8, 18, 16, 18));
        return this;
    }

    public PanelTarjeta conAcento(Color acento) {
        this.acento = acento;
        repaint();
        return this;
    }

    public PanelTarjeta sinEsquinas() {
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        int y0 = titulo == null ? 0 : DESPLAZAMIENTO;
        Tema.ventana(g2, 0, y0, w, h - y0, acento, Tema.FONDO_2);
        if (titulo != null) {
            String texto = titulo.toUpperCase();
            int anchoIcono = icono == null ? 0 : 26;
            int ancho = Tema.anchoPixel(texto, 2) + anchoIcono + 30;
            int x = 16;
            g2.setColor(Tema.BORDE);
            g2.fill(Tema.forma(x, 0, ancho, ALTO_PESTANA, 5));
            g2.setColor(Tema.mezclar(acento, Color.WHITE, 0.55f));
            g2.fill(Tema.forma(x + 3, 3, ancho - 6, ALTO_PESTANA - 6, 3));
            int tx = x + 14;
            if (icono != null) {
                Iconos.dibujar(g2, icono, tx, ALTO_PESTANA / 2.0 - 9, 18, Tema.TEXTO);
                tx += anchoIcono;
            }
            Tema.textoPixel(g2, texto, tx, ALTO_PESTANA / 2 - Tema.altoPixel(2) / 2 + 1, 2, Tema.TEXTO, true);
        }
        g2.dispose();
    }
}
