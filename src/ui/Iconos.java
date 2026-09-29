package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

public final class Iconos {

    public enum Tipo {
        INICIO, ARBOL, ESTADISTICAS, VOLVER, SIGUIENTE, INFO, BUSCAR, REINICIAR, ZOOM_MAS, ZOOM_MENOS,
        CENTRAR, CERRAR, JUGAR, CHECK, RELOJ, TROFEO, USUARIO, SALIR, COMPARTIR, IGNORAR, VERIFICAR,
        CAPAS, MAS, MENOS, BANDERA, AYUDA, CIUDAD
    }

    private Iconos() {
    }

    public static Icon icono(Tipo tipo, int tam, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = Tema.preparar(g);
                dibujar(g2, tipo, x, y, tam, color);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return tam;
            }

            @Override
            public int getIconHeight() {
                return tam;
            }
        };
    }

    public static void avatar(Graphics2D g, String nombre, double x, double y, double tam, Color color) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Tema.BORDE);
        g2.fill(Tema.forma(x, y, tam, tam, (int) (tam / 5)));
        g2.setColor(Tema.mezclar(color, Color.WHITE, 0.45f));
        g2.fill(Tema.forma(x + 3, y + 3, tam - 6, tam - 6, (int) (tam / 6)));
        g2.setColor(Tema.mezclar(color, Color.WHITE, 0.15f));
        g2.fillRect((int) (x + 3), (int) (y + tam * 0.7), (int) (tam - 6), (int) (tam * 0.3) - 3);
        String inicial = nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase();
        int escala = Math.max(1, (int) Math.round(tam / 14));
        int ancho = Tema.anchoPixel(inicial, escala);
        int alto = Tema.altoPixel(escala);
        Tema.textoPixel(g2, inicial, (int) (x + (tam - ancho) / 2.0) + 1, (int) (y + (tam - alto) / 2.0), escala, Tema.TEXTO, false);
        g2.dispose();
    }

    public static Image imagenAplicacion(int tam) {
        BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = Tema.preparar(img.getGraphics());
        Tema.ventana(g2, 0, 0, tam - 1, tam - 1, Tema.CIAN, Tema.LOGO);
        dibujar(g2, Tipo.VERIFICAR, tam * 0.18, tam * 0.18, tam * 0.64, Tema.TEXTO);
        g2.dispose();
        return img;
    }

    public static void dibujar(Graphics2D g, Tipo tipo, double x, double y, double tam, Color color) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(tam / 24.0, tam / 24.0);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        switch (tipo) {
            case INICIO -> {
                poli(g2, false, 3, 11, 12, 3, 21, 11);
                poli(g2, false, 5, 9.5, 5, 21, 19, 21, 19, 9.5);
                poli(g2, false, 10, 21, 10, 15, 14, 15, 14, 21);
            }
            case ARBOL -> {
                circulo(g2, 12, 5, 2.5);
                circulo(g2, 6, 19, 2.5);
                circulo(g2, 18, 19, 2.5);
                linea(g2, 12, 7.5, 12, 12);
                linea(g2, 6, 12, 18, 12);
                linea(g2, 6, 12, 6, 16.5);
                linea(g2, 18, 12, 18, 16.5);
            }
            case ESTADISTICAS -> {
                linea(g2, 3, 21, 21, 21);
                g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                linea(g2, 6.5, 17.5, 6.5, 12);
                linea(g2, 12, 17.5, 12, 5);
                linea(g2, 17.5, 17.5, 17.5, 9);
            }
            case VOLVER -> {
                linea(g2, 20, 12, 4, 12);
                poli(g2, false, 11, 5, 4, 12, 11, 19);
            }
            case SIGUIENTE -> {
                linea(g2, 4, 12, 20, 12);
                poli(g2, false, 13, 5, 20, 12, 13, 19);
            }
            case INFO -> {
                circulo(g2, 12, 12, 9);
                linea(g2, 12, 11, 12, 16.5);
                punto(g2, 12, 7.6, 1.3);
            }
            case BUSCAR -> {
                circulo(g2, 10.5, 10.5, 6.5);
                linea(g2, 15.5, 15.5, 21, 21);
            }
            case REINICIAR -> {
                g2.draw(new Arc2D.Double(4, 4, 16, 16, 30, 300, Arc2D.OPEN));
                poli(g2, false, 19.5, 3, 19, 8, 14, 7.5);
            }
            case ZOOM_MAS -> {
                circulo(g2, 10.5, 10.5, 6.5);
                linea(g2, 15.5, 15.5, 21, 21);
                linea(g2, 7.5, 10.5, 13.5, 10.5);
                linea(g2, 10.5, 7.5, 10.5, 13.5);
            }
            case ZOOM_MENOS -> {
                circulo(g2, 10.5, 10.5, 6.5);
                linea(g2, 15.5, 15.5, 21, 21);
                linea(g2, 7.5, 10.5, 13.5, 10.5);
            }
            case CENTRAR -> {
                circulo(g2, 12, 12, 7);
                linea(g2, 12, 2, 12, 6);
                linea(g2, 12, 18, 12, 22);
                linea(g2, 2, 12, 6, 12);
                linea(g2, 18, 12, 22, 12);
                punto(g2, 12, 12, 1.8);
            }
            case CERRAR -> {
                linea(g2, 6, 6, 18, 18);
                linea(g2, 18, 6, 6, 18);
            }
            case JUGAR -> {
                Path2D p = new Path2D.Double();
                p.moveTo(7, 4);
                p.lineTo(20, 12);
                p.lineTo(7, 20);
                p.closePath();
                g2.fill(p);
                g2.draw(p);
            }
            case CHECK -> poli(g2, false, 4, 12.5, 9.5, 18, 20, 6);
            case RELOJ -> {
                circulo(g2, 12, 13, 8.5);
                poli(g2, false, 12, 8.5, 12, 13, 15.5, 15);
                linea(g2, 9.5, 2.5, 14.5, 2.5);
            }
            case TROFEO -> {
                Path2D copa = new Path2D.Double();
                copa.moveTo(7, 4);
                copa.lineTo(17, 4);
                copa.lineTo(17, 10);
                copa.quadTo(17, 15, 12, 15);
                copa.quadTo(7, 15, 7, 10);
                copa.closePath();
                g2.draw(copa);
                Path2D asas = new Path2D.Double();
                asas.moveTo(7, 6);
                asas.lineTo(4, 6);
                asas.lineTo(4, 8);
                asas.quadTo(4, 11, 7, 11.5);
                asas.moveTo(17, 6);
                asas.lineTo(20, 6);
                asas.lineTo(20, 8);
                asas.quadTo(20, 11, 17, 11.5);
                g2.draw(asas);
                linea(g2, 12, 15, 12, 19);
                linea(g2, 8, 20.5, 16, 20.5);
            }
            case USUARIO -> {
                circulo(g2, 12, 8, 4);
                Path2D p = new Path2D.Double();
                p.moveTo(4, 21);
                p.quadTo(4, 13.5, 12, 13.5);
                p.quadTo(20, 13.5, 20, 21);
                g2.draw(p);
            }
            case SALIR -> {
                poli(g2, false, 13, 4, 5, 4, 5, 20, 13, 20);
                linea(g2, 10, 12, 21, 12);
                poli(g2, false, 17, 8, 21, 12, 17, 16);
            }
            case COMPARTIR -> {
                circulo(g2, 18, 5, 2.6);
                circulo(g2, 6, 12, 2.6);
                circulo(g2, 18, 19, 2.6);
                linea(g2, 8.3, 10.7, 15.7, 6.3);
                linea(g2, 8.3, 13.3, 15.7, 17.7);
            }
            case IGNORAR -> {
                Path2D ojo = new Path2D.Double();
                ojo.moveTo(2, 12);
                ojo.quadTo(12, 2.5, 22, 12);
                ojo.quadTo(12, 21.5, 2, 12);
                ojo.closePath();
                g2.draw(ojo);
                circulo(g2, 12, 12, 3);
                linea(g2, 4, 20, 20, 4);
            }
            case VERIFICAR -> {
                Path2D escudo = new Path2D.Double();
                escudo.moveTo(12, 2.5);
                escudo.lineTo(20, 5.5);
                escudo.lineTo(20, 11.5);
                escudo.quadTo(20, 18, 12, 21.5);
                escudo.quadTo(4, 18, 4, 11.5);
                escudo.lineTo(4, 5.5);
                escudo.closePath();
                g2.draw(escudo);
                poli(g2, false, 8.5, 12, 11, 14.5, 15.5, 9.5);
            }
            case CAPAS -> {
                poli(g2, true, 12, 3, 21, 8, 12, 13, 3, 8);
                poli(g2, false, 3, 12.5, 12, 17.5, 21, 12.5);
                poli(g2, false, 3, 16.5, 12, 21.5, 21, 16.5);
            }
            case MAS -> {
                linea(g2, 12, 5, 12, 19);
                linea(g2, 5, 12, 19, 12);
            }
            case MENOS -> linea(g2, 5, 12, 19, 12);
            case BANDERA -> {
                linea(g2, 5, 3, 5, 21);
                poli(g2, true, 5, 4, 18, 4, 15, 8.5, 18, 13, 5, 13);
            }
            case AYUDA -> {
                circulo(g2, 12, 12, 9);
                Path2D p = new Path2D.Double();
                p.moveTo(9.3, 9.3);
                p.quadTo(9.5, 6.5, 12, 6.5);
                p.quadTo(14.8, 6.5, 14.8, 9.2);
                p.quadTo(14.8, 11.2, 12, 12.4);
                p.lineTo(12, 13.8);
                g2.draw(p);
                punto(g2, 12, 17, 1.3);
            }
            case CIUDAD -> {
                linea(g2, 2, 21, 22, 21);
                poli(g2, false, 4, 21, 4, 10, 9, 10, 9, 21);
                poli(g2, false, 9, 21, 9, 4, 15, 4, 15, 21);
                poli(g2, false, 15, 21, 15, 13, 20, 13, 20, 21);
                linea(g2, 11.5, 8, 12.5, 8);
                linea(g2, 11.5, 12, 12.5, 12);
                linea(g2, 11.5, 16, 12.5, 16);
            }
        }
        g2.dispose();
    }

    private static void linea(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void circulo(Graphics2D g, double cx, double cy, double r) {
        g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
    }

    private static void punto(Graphics2D g, double cx, double cy, double r) {
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
    }

    private static void poli(Graphics2D g, boolean cerrar, double... p) {
        Path2D path = new Path2D.Double();
        path.moveTo(p[0], p[1]);
        for (int i = 2; i < p.length; i += 2) {
            path.lineTo(p[i], p[i + 1]);
        }
        if (cerrar) {
            path.closePath();
        }
        g.draw(path);
    }
}
