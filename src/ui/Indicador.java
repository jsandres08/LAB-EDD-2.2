package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class Indicador extends JComponent {

    private final String etiqueta;
    private final Iconos.Tipo icono;
    private final Color color;
    private final boolean grande;
    private String valor = "-";
    private String detalle = "";

    public Indicador(String etiqueta, Iconos.Tipo icono, Color color, boolean grande) {
        this.etiqueta = etiqueta;
        this.icono = icono;
        this.color = color;
        this.grande = grande;
        setOpaque(false);
    }

    public void setValor(String valor) {
        setValor(valor, "");
    }

    public void setValor(String valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
        setToolTipText(detalle.isEmpty() ? null : detalle);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return grande ? new Dimension(220, 108) : new Dimension(130, 70);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        int w = getWidth() - 4;
        int h = getHeight() - 4;
        Tema.ventana(g2, 0, 0, w, h, color, Tema.FONDO_2);
        g2.setColor(color);
        g2.fillRect(3, 10, 5, h - 20);

        int pad = grande ? 18 : 13;
        int tamIcono = grande ? 22 : 15;
        if (icono != null) {
            Iconos.dibujar(g2, icono, w - pad - tamIcono, pad - 3, tamIcono, color);
        }
        g2.setFont(Tema.texto(Font.BOLD, grande ? 12f : 10.5f));
        g2.setColor(Tema.TEXTO_SUAVE);
        g2.drawString(etiqueta.toUpperCase(), pad + 2, pad + (grande ? 8 : 6));

        int escala = grande ? 3 : 2;
        String texto = valor;
        while (texto.length() > 1 && Tema.anchoPixel(texto, escala) > w - 2 * pad) {
            texto = texto.substring(0, texto.length() - 1);
        }
        int alto = Tema.altoPixel(escala);
        int y = grande ? pad + 16 : h - pad - alto + 6;
        Tema.textoPixel(g2, texto, pad + 2, y, escala, Tema.TEXTO, true);
        if (grande && !detalle.isEmpty()) {
            g2.setFont(Tema.texto(Font.PLAIN, 12f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Tema.mezclar(color, Color.BLACK, 0.2f));
            g2.drawString(Tema.recortar(detalle, fm, w - 2 * pad), pad + 2, h - pad + 2);
        }
        g2.dispose();
    }
}
