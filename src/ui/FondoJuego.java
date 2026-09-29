package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.JPanel;

public class FondoJuego extends JPanel {

    private static final int T = 32;
    private static final int COLUMNAS_MANZANA = 9;
    private static final int FILAS_MANZANA = 7;

    private static final Color CALLE = new Color(0x8C94A4);
    private static final Color LINEA_CALLE = new Color(0xF4ECC8);
    private static final Color ACERA = new Color(0xDCDCD0);
    private static final Color ACERA_JUNTA = new Color(0xC8C8BC);
    private static final Color CESPED = new Color(0xB8E0A0);
    private static final Color CESPED_OSCURO = new Color(0xA0D088);
    private static final Color PARED = new Color(0xF4ECDC);
    private static final Color VENTANA = new Color(0x70B0E8);
    private static final Color PUERTA = new Color(0x906040);
    private static final Color TINTA = new Color(0x303038);
    private static final Color[] TECHOS = {
        new Color(0xE06050), new Color(0x5078D8), new Color(0xE8A048), new Color(0x48A8A0), new Color(0x9068C8)
    };

    public FondoJuego(LayoutManager layout) {
        super(layout);
        setOpaque(true);
        setBackground(ACERA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        Graphics2D g2 = Tema.preparar(g);
        int columnas = w / T + 1;
        int filas = h / T + 1;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                dibujarBaldosa(g2, c, f);
            }
        }
        for (int by = 0; by * FILAS_MANZANA < filas + 2; by++) {
            for (int bx = 0; bx * COLUMNAS_MANZANA < columnas; bx++) {
                dibujarManzana(g2, bx, by);
            }
        }
        g2.setColor(new Color(255, 255, 250, 120));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    private static boolean esCalleHorizontal(int f) {
        return Math.floorMod(f, FILAS_MANZANA) == 3;
    }

    private static boolean esCalleVertical(int c) {
        return Math.floorMod(c, COLUMNAS_MANZANA) == 4;
    }

    private void dibujarBaldosa(Graphics2D g2, int c, int f) {
        int x = c * T;
        int y = f * T;
        boolean horizontal = esCalleHorizontal(f);
        boolean vertical = esCalleVertical(c);
        if (horizontal || vertical) {
            g2.setColor(CALLE);
            g2.fillRect(x, y, T, T);
            g2.setColor(LINEA_CALLE);
            if (horizontal && !vertical && c % 2 == 0) {
                g2.fillRect(x + 6, y + T / 2 - 2, T - 12, 4);
            } else if (vertical && !horizontal && f % 2 == 0) {
                g2.fillRect(x + T / 2 - 2, y + 6, 4, T - 12);
            } else if (horizontal && vertical) {
                for (int i = 4; i < T; i += 8) {
                    g2.fillRect(x + i, y + 2, 4, 5);
                    g2.fillRect(x + i, y + T - 7, 4, 5);
                }
            }
            return;
        }
        g2.setColor(ACERA);
        g2.fillRect(x, y, T, T);
        g2.setColor(ACERA_JUNTA);
        g2.fillRect(x, y + T - 2, T, 2);
        g2.fillRect(x + T - 2, y, 2, T);
    }

    private void dibujarManzana(Graphics2D g2, int bx, int by) {
        int c0 = bx * COLUMNAS_MANZANA + 6;
        int f0 = by * FILAS_MANZANA - 2;
        int ancho = 6;
        int alto = 4;
        int x = c0 * T;
        int y = f0 * T;
        int tipo = Math.floorMod(bx * 7 + by * 13, 5);
        g2.setColor(CESPED);
        g2.fillRect(x, y, ancho * T, alto * T);
        g2.setColor(CESPED_OSCURO);
        for (int i = 0; i < ancho * 2; i++) {
            g2.fillRect(x + i * 16 + 4, y + ((i * 11) % (alto * T - 8)), 4, 4);
        }
        if (tipo == 0) {
            for (int i = 0; i < 4; i++) {
                dibujarArbol(g2, x + 12 + i * 44, y + 14);
                dibujarArbol(g2, x + 34 + i * 44, y + 70);
            }
            return;
        }
        dibujarEdificio(g2, x + 6, y + 6, 3 * T - 14, 3 * T - 4, TECHOS[Math.floorMod(bx + by * 2, TECHOS.length)], tipo % 2 == 0);
        dibujarEdificio(g2, x + 3 * T + 6, y + 6, 3 * T - 14, 3 * T - 4, TECHOS[Math.floorMod(bx * 3 + by + 1, TECHOS.length)], tipo % 2 == 1);
    }

    private void dibujarEdificio(Graphics2D g2, int x, int y, int w, int h, Color techo, boolean alto) {
        int altoTecho = alto ? h * 3 / 5 : h / 2;
        g2.setColor(TINTA);
        g2.fillRect(x - 2, y - 2, w + 4, h + 4);
        g2.setColor(techo);
        g2.fillRect(x, y, w, altoTecho);
        g2.setColor(Tema.mezclar(techo, Color.WHITE, 0.35f));
        for (int ty = y + 4; ty < y + altoTecho - 4; ty += 8) {
            g2.fillRect(x + 4, ty, w - 8, 2);
        }
        g2.setColor(Tema.mezclar(techo, Color.BLACK, 0.3f));
        g2.fillRect(x, y + altoTecho - 4, w, 4);
        g2.setColor(PARED);
        g2.fillRect(x, y + altoTecho, w, h - altoTecho);
        int filasVentanas = alto ? 1 : 2;
        for (int fv = 0; fv < filasVentanas; fv++) {
            for (int vx = x + 6; vx + 10 < x + w - 4; vx += 16) {
                if (fv == filasVentanas - 1 && Math.abs(vx - (x + w / 2 - 6)) < 10) {
                    continue;
                }
                int vy = y + altoTecho + 5 + fv * 14;
                g2.setColor(TINTA);
                g2.fillRect(vx - 1, vy - 1, 12, 10);
                g2.setColor(VENTANA);
                g2.fillRect(vx, vy, 10, 8);
                g2.setColor(Color.WHITE);
                g2.fillRect(vx + 1, vy + 1, 3, 2);
            }
        }
        int puertaX = x + w / 2 - 6;
        g2.setColor(TINTA);
        g2.fillRect(puertaX - 1, y + h - 15, 14, 15);
        g2.setColor(PUERTA);
        g2.fillRect(puertaX, y + h - 14, 12, 14);
    }

    private void dibujarArbol(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(0x805830));
        g2.fillRect(x + 8, y + 18, 6, 8);
        g2.setColor(TINTA);
        g2.fillRect(x + 2, y - 2, 18, 22);
        g2.fillRect(x - 2, y + 2, 26, 14);
        g2.setColor(new Color(0x40A050));
        g2.fillRect(x + 4, y, 14, 18);
        g2.fillRect(x, y + 4, 22, 10);
        g2.setColor(new Color(0x70C868));
        g2.fillRect(x + 4, y + 2, 6, 4);
        g2.fillRect(x + 2, y + 6, 4, 4);
    }
}
