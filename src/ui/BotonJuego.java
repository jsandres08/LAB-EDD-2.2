package ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.JButton;

public class BotonJuego extends JButton {

    public enum Estilo { PRINCIPAL, SECUNDARIO, VOLVER, PELIGRO, ICONO, LISTA }

    private static final int SOMBRA = 3;

    private final Estilo estilo;
    private final Iconos.Tipo tipoIcono;
    private Color acento;
    private boolean activo;
    private String atajo;

    public BotonJuego(String texto, Iconos.Tipo icono, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        this.tipoIcono = icono;
        this.acento = estilo == Estilo.PELIGRO ? Tema.MAGENTA : Tema.CIAN;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (estilo == Estilo.ICONO) {
            setToolTipText(texto);
        }
    }

    public BotonJuego conAcento(Color color) {
        this.acento = color;
        repaint();
        return this;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        repaint();
    }

    public boolean isActivo() {
        return activo;
    }

    public void setAtajo(String atajo) {
        this.atajo = atajo;
        revalidate();
        repaint();
    }

    private int escala() {
        return estilo == Estilo.PRINCIPAL || estilo == Estilo.LISTA ? 3 : 2;
    }

    private int tamIcono() {
        return estilo == Estilo.PRINCIPAL || estilo == Estilo.LISTA ? 22 : 18;
    }

    private String textoVisible() {
        return estilo == Estilo.ICONO ? "" : getText();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        int texto = Tema.anchoPixel(textoVisible(), escala());
        int icono = tipoIcono == null ? 0 : tamIcono() + 10;
        int extra = (atajo == null ? 0 : 34) + 30;
        return switch (estilo) {
            case PRINCIPAL -> new Dimension(texto + icono + extra + 36, 64);
            case LISTA -> new Dimension(texto + icono + extra + 20, 54);
            case ICONO -> new Dimension(46, 46);
            case VOLVER -> new Dimension(texto + icono + extra + 10, 46);
            default -> new Dimension(texto + icono + extra + 16, 50);
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        }
        boolean lista = estilo == Estilo.LISTA;
        int sombra = lista ? 0 : SOMBRA;
        int w = getWidth() - sombra - 1;
        int h = getHeight() - sombra - 1;
        boolean presionado = getModel().isArmed() && getModel().isPressed();
        boolean hover = isEnabled() && getModel().isRollover();
        Color base = isEnabled() ? acento : Tema.GRIS;
        int d = presionado ? sombra : 0;

        if (lista) {
            if (hover || activo) {
                g2.setColor(Tema.mezclar(Tema.FONDO_2, base, 0.18f));
                g2.fillRect(4, 4, w - 8, h - 8);
            }
        } else {
            Color relleno;
            if (estilo == Estilo.PRINCIPAL || activo) {
                relleno = Tema.mezclar(base, Color.WHITE, hover ? 0.3f : 0.5f);
            } else {
                relleno = hover ? Tema.mezclar(Tema.FONDO_2, base, 0.18f) : Tema.FONDO_2;
            }
            if (!presionado && sombra > 0) {
                g2.setColor(Tema.SOMBRA);
                g2.fill(Tema.forma(sombra, sombra, w, h, 7));
            }
            g2.setColor(Tema.BORDE);
            g2.fill(Tema.forma(d, d, w, h, 7));
            g2.setColor(relleno);
            g2.fill(Tema.forma(d + 3, d + 3, w - 6, h - 6, 5));
            g2.setColor(Tema.mezclar(relleno, Color.WHITE, 0.6f));
            g2.fillRect(d + 7, d + 5, w - 14, 2);
            if (estilo != Estilo.VOLVER && !(estilo == Estilo.PRINCIPAL || activo)) {
                g2.setColor(base);
                g2.fillRect(d + 5, d + h - 9, w - 10, 3);
            }
            if (isFocusOwner()) {
                g2.setColor(base);
                g2.draw(Tema.forma(d - 3, d - 3, w + 5, h + 5, 9));
            }
        }

        int cy = d + h / 2;
        int x0 = d + 8;
        if (hover || (lista && activo)) {
            int t = lista ? 7 : 5;
            Polygon flecha = new Polygon(new int[]{x0 + 2, x0 + 2 + t, x0 + 2}, new int[]{cy - t, cy, cy + t}, 3);
            g2.setColor(Tema.SOMBRA);
            flecha.translate(2, 2);
            g2.fill(flecha);
            flecha.translate(-2, -2);
            g2.setColor(Tema.TEXTO);
            g2.fill(flecha);
        }
        x0 += lista ? 22 : 12;
        if (atajo != null) {
            g2.setColor(Tema.BORDE);
            g2.fillRect(x0, cy - 12, 24, 24);
            g2.setColor(Tema.FONDO_2);
            g2.fillRect(x0 + 2, cy - 10, 20, 20);
            Tema.textoPixel(g2, atajo, x0 + (24 - Tema.anchoPixel(atajo, 2)) / 2 + 1, cy - Tema.altoPixel(2) / 2, 2, Tema.TEXTO, false);
            x0 += 30;
        }

        String texto = textoVisible();
        int escala = escala();
        int ti = tipoIcono == null ? 0 : tamIcono();
        int sep = ti > 0 && !texto.isEmpty() ? 10 : 0;
        int ancho = ti + sep + Tema.anchoPixel(texto, escala);
        int x = lista ? x0 : x0 + (d + w - 8 - x0 - ancho) / 2;
        if (estilo == Estilo.ICONO) {
            x = d + (w - ti) / 2;
        }
        if (ti > 0) {
            Color colorIcono = estilo == Estilo.PRINCIPAL || activo || estilo == Estilo.VOLVER ? Tema.TEXTO
                    : Tema.mezclar(base, Color.BLACK, 0.15f);
            Iconos.dibujar(g2, tipoIcono, x, cy - ti / 2.0, ti, colorIcono);
        }
        if (!texto.isEmpty()) {
            Color colorTexto = estilo == Estilo.PELIGRO ? Tema.MAGENTA : Tema.TEXTO;
            Tema.textoPixel(g2, texto, x + ti + sep, cy - Tema.altoPixel(escala) / 2 + 1, escala, colorTexto, true);
        }
        g2.dispose();
    }
}
