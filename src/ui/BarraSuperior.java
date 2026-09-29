package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class BarraSuperior extends JPanel {

    private final EtiquetaPixel titulo;
    private final JLabel subtitulo;
    private final JPanel derecha = new JPanel(new GridBagLayout());

    public BarraSuperior(JComponent izquierda, Iconos.Tipo icono, Color acento, String titulo, String subtitulo) {
        super(new BorderLayout(18, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 12, 12, 16));

        JPanel izq = new JPanel(new GridBagLayout());
        izq.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 18);
        if (izquierda != null) {
            izq.add(izquierda, c);
        }
        c.insets = new Insets(0, 0, 0, 14);
        izq.add(new Emblema(icono, acento), c);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        this.titulo = new EtiquetaPixel(titulo.toUpperCase(), 3, Tema.TEXTO);
        this.titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.subtitulo = Tema.etiqueta(subtitulo.isEmpty() ? " " : subtitulo, Tema.texto(Font.PLAIN, 14f), Tema.TEXTO_SUAVE);
        this.subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(this.titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(this.subtitulo);
        c.insets = new Insets(0, 0, 0, 0);
        izq.add(textos, c);

        derecha.setOpaque(false);
        add(izq, BorderLayout.WEST);
        add(derecha, BorderLayout.EAST);
    }

    public void agregar(JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 10, 0, 0);
        derecha.add(componente, c);
    }

    public void setTitulo(String texto) {
        titulo.setTexto(texto.toUpperCase());
    }

    public void setSubtitulo(String texto) {
        subtitulo.setText(texto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        Tema.ventana(g2, 0, 0, getWidth() - 1, getHeight() - 1, Tema.AZUL, Tema.FONDO_2);
        g2.dispose();
    }

    private static class Emblema extends JComponent {

        private final Iconos.Tipo icono;
        private final Color color;

        Emblema(Iconos.Tipo icono, Color color) {
            this.icono = icono;
            this.color = color;
            setPreferredSize(new Dimension(54, 54));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            Tema.ventana(g2, 0, 0, 50, 50, color, Tema.mezclar(color, Color.WHITE, 0.55f));
            Iconos.dibujar(g2, icono, 13, 13, 24, Tema.TEXTO);
            g2.dispose();
        }
    }
}
