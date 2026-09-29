package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class BarraProgreso extends javax.swing.JComponent {

    private int completadas;
    private int actual = -1;
    private int total;

    public BarraProgreso() {
        setOpaque(false);
    }

    public void setValores(int completadas, int actual, int total) {
        this.completadas = completadas;
        this.actual = actual;
        this.total = total;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Math.max(1, total) * 26 + 6, 46);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        g2.setFont(Tema.texto(Font.BOLD, 11f));
        g2.setColor(Tema.TEXTO_SUAVE);
        g2.drawString("PROGRESO " + Math.min(total, completadas) + "/" + total, 2, 12);
        for (int i = 0; i < total; i++) {
            int x = 2 + i * 26;
            java.awt.Color color = i < completadas ? Tema.CIAN : i == actual ? new java.awt.Color(0xF0C020) : Tema.FONDO_2;
            g2.setColor(Tema.BORDE);
            g2.fillRect(x, 20, 20, 18);
            g2.setColor(color);
            g2.fillRect(x + 3, 23, 14, 12);
            if (i < completadas || i == actual) {
                g2.setColor(Tema.mezclar(color, java.awt.Color.WHITE, 0.5f));
                g2.fillRect(x + 3, 23, 14, 3);
            }
        }
        g2.dispose();
    }
}
