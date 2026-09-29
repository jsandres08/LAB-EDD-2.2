package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

public final class Tema {

    public static final Color FONDO = new Color(0xE8F0D8);
    public static final Color FONDO_2 = new Color(0xF8F8F8);
    public static final Color PANEL = new Color(0xECF2F8);
    public static final Color PANEL_CLARO = new Color(0xC8D8F0);
    public static final Color BORDE = new Color(0x303038);
    public static final Color TEXTO = new Color(0x303038);
    public static final Color TEXTO_SUAVE = new Color(0x707080);
    public static final Color CIAN = new Color(0x2890D8);
    public static final Color MAGENTA = new Color(0xE03838);
    public static final Color AMBAR = new Color(0xD89000);
    public static final Color VERDE = new Color(0x30A848);
    public static final Color VIOLETA = new Color(0x8850C8);
    public static final Color AZUL = new Color(0x3860D0);
    public static final Color GRIS = new Color(0x9098A0);
    public static final Color BRONCE = new Color(0xC07030);
    public static final Color SOMBRA = new Color(0xC8C8C0);
    public static final Color LOGO = new Color(0xF8D030);
    public static final Color LOGO_BORDE = new Color(0x284898);

    private static final String FAMILIA = elegir("Consolas", "Lucida Console", "Courier New");
    private static final String FAMILIA_PIXEL = elegir("Lucida Console", "Consolas", "Courier New");
    private static final Font FUENTE_PIXEL = new Font(FAMILIA_PIXEL, Font.BOLD, 9);
    private static final Map<String, BufferedImage> CACHE_PIXEL = new HashMap<>();

    private Tema() {
    }

    private static String elegir(String... nombres) {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String nombre : nombres) {
            if (disponibles.contains(nombre)) {
                return nombre;
            }
        }
        return Font.MONOSPACED;
    }

    public static Font titulo(float tam) {
        return new Font(FAMILIA, Font.BOLD, 12).deriveFont(tam);
    }

    public static Font texto(int estilo, float tam) {
        return new Font(FAMILIA, estilo, 12).deriveFont(tam);
    }

    public static Font espaciada(Font fuente, float espaciado) {
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, espaciado);
        return fuente.deriveFont(atributos);
    }

    public static Color alfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alfa)));
    }

    public static Color mezclar(Color a, Color b, float t) {
        float u = Math.max(0f, Math.min(1f, t));
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * u),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * u),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * u),
                Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * u));
    }

    public static Graphics2D preparar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        return g2;
    }

    public static Polygon forma(double x, double y, double w, double h, int corte) {
        int x0 = (int) Math.round(x);
        int y0 = (int) Math.round(y);
        int x1 = (int) Math.round(x + w);
        int y1 = (int) Math.round(y + h);
        int c = Math.max(0, Math.min(corte, Math.min(x1 - x0, y1 - y0) / 2));
        return new Polygon(
                new int[]{x0 + c, x1 - c, x1, x1, x1 - c, x0 + c, x0, x0},
                new int[]{y0, y0, y0 + c, y1 - c, y1, y1, y1 - c, y0 + c}, 8);
    }

    public static void bloque(Graphics2D g, double x, double y, double w, double h, int corte, Color relleno,
            Color borde, int sombra) {
        if (sombra > 0) {
            g.setColor(SOMBRA);
            g.fill(forma(x + sombra, y + sombra, w, h, corte));
        }
        Polygon p = forma(x, y, w, h, corte);
        g.setColor(relleno);
        g.fill(p);
        if (borde != null) {
            Stroke anterior = g.getStroke();
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g.setColor(borde);
            g.draw(forma(x + 1, y + 1, w - 2, h - 2, Math.max(0, corte - 1)));
            g.setStroke(anterior);
        }
    }

    public static void ventana(Graphics2D g, double x, double y, double w, double h, Color acento, Color relleno) {
        g.setColor(BORDE);
        g.fill(forma(x, y, w, h, 8));
        g.setColor(relleno);
        g.fill(forma(x + 3, y + 3, w - 6, h - 6, 6));
        Stroke anterior = g.getStroke();
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.setColor(mezclar(acento, Color.WHITE, 0.35f));
        g.draw(forma(x + 6, y + 6, w - 13, h - 13, 4));
        g.setStroke(anterior);
    }

    public static Color colorVida(double fraccion) {
        if (fraccion > 0.5) {
            return new Color(0x48C860);
        }
        if (fraccion > 0.2) {
            return new Color(0xF0C020);
        }
        return new Color(0xE84830);
    }

    public static void brillo(Graphics2D g, Shape forma, Color color, int intensidad) {
        Stroke trazo = g.getStroke();
        Color anterior = g.getColor();
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.setColor(alfa(color, Math.min(255, intensidad + 40)));
        g.draw(forma);
        g.setStroke(trazo);
        g.setColor(anterior);
    }

    public static void barraSegmentos(Graphics2D g, double x, double y, double w, double h, double fraccion,
            Color color, int segmentos) {
        double f = Math.max(0, Math.min(1, fraccion));
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);
        int iw = (int) Math.round(w);
        int ih = Math.max(6, (int) Math.round(h));
        g.setColor(BORDE);
        g.fillRect(ix + 2, iy, iw - 4, ih);
        g.fillRect(ix, iy + 2, iw, ih - 4);
        g.setColor(new Color(0x585860));
        g.fillRect(ix + 2, iy + 2, iw - 4, ih - 4);
        int relleno = (int) Math.round((iw - 4) * f);
        if (relleno > 0) {
            g.setColor(color);
            g.fillRect(ix + 2, iy + 2, relleno, ih - 4);
            g.setColor(mezclar(color, Color.WHITE, 0.5f));
            g.fillRect(ix + 2, iy + 2, relleno, Math.max(1, (ih - 4) / 3));
        }
    }

    public static void esquinas(Graphics2D g, double x, double y, double w, double h, double largo, Color color) {
        Stroke trazo = g.getStroke();
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.setColor(color);
        g.draw(new Line2D.Double(x, y, x + largo, y));
        g.draw(new Line2D.Double(x, y, x, y + largo));
        g.draw(new Line2D.Double(x + w, y, x + w - largo, y));
        g.draw(new Line2D.Double(x + w, y, x + w, y + largo));
        g.draw(new Line2D.Double(x, y + h, x + largo, y + h));
        g.draw(new Line2D.Double(x, y + h, x, y + h - largo));
        g.draw(new Line2D.Double(x + w, y + h, x + w - largo, y + h));
        g.draw(new Line2D.Double(x + w, y + h, x + w, y + h - largo));
        g.setStroke(trazo);
    }

    private static BufferedImage imagenPixel(String texto, Color color) {
        String clave = texto + "\u0000" + color.getRGB();
        BufferedImage cache = CACHE_PIXEL.get(clave);
        if (cache != null) {
            return cache;
        }
        BufferedImage medida = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gm = medida.createGraphics();
        FontMetrics fm = gm.getFontMetrics(FUENTE_PIXEL);
        gm.dispose();
        int avance = fm.charWidth('M') + 1;
        int w = Math.max(1, texto.length() * avance);
        int h = fm.getAscent() + fm.getDescent() + 4;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setFont(FUENTE_PIXEL);
        g.setColor(color);
        for (int i = 0; i < texto.length(); i++) {
            String c = texto.substring(i, i + 1);
            g.drawString(c, i * avance + (avance - 1 - fm.stringWidth(c)) / 2, fm.getAscent() + 3);
        }
        g.dispose();
        if (CACHE_PIXEL.size() > 600) {
            CACHE_PIXEL.clear();
        }
        CACHE_PIXEL.put(clave, img);
        return img;
    }

    public static int anchoPixel(String texto, int escala) {
        return imagenPixel(texto, TEXTO).getWidth() * escala;
    }

    public static int altoPixel(int escala) {
        return imagenPixel("A", TEXTO).getHeight() * escala;
    }

    public static void textoPixel(Graphics2D g, String texto, int x, int y, int escala, Color color, boolean sombra) {
        if (texto == null || texto.isEmpty()) {
            return;
        }
        Object interpolacion = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (sombra) {
            BufferedImage s = imagenPixel(texto, SOMBRA);
            g.drawImage(s, x + escala, y + escala, s.getWidth() * escala, s.getHeight() * escala, null);
        }
        BufferedImage img = imagenPixel(texto, color);
        g.drawImage(img, x, y, img.getWidth() * escala, img.getHeight() * escala, null);
        if (interpolacion != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolacion);
        }
    }

    public static List<String> envolver(String texto, FontMetrics fm, int ancho, int maxLineas) {
        List<String> lineas = new ArrayList<>();
        for (String parrafo : texto.split("\n")) {
            StringBuilder actual = new StringBuilder();
            for (String palabra : parrafo.split(" ")) {
                String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
                if (fm.stringWidth(prueba) > ancho && actual.length() > 0) {
                    lineas.add(actual.toString());
                    actual = new StringBuilder(palabra);
                } else {
                    actual = new StringBuilder(prueba);
                }
            }
            lineas.add(actual.toString());
        }
        if (lineas.size() > maxLineas) {
            lineas = new ArrayList<>(lineas.subList(0, maxLineas));
            lineas.set(maxLineas - 1, lineas.get(maxLineas - 1) + "…");
        }
        for (int i = 0; i < lineas.size(); i++) {
            lineas.set(i, recortar(lineas.get(i), fm, ancho));
        }
        return lineas;
    }

    public static String recortar(String texto, FontMetrics fm, int ancho) {
        if (fm.stringWidth(texto) <= ancho) {
            return texto;
        }
        String base = texto.endsWith("…") ? texto.substring(0, texto.length() - 1) : texto;
        while (base.length() > 1 && fm.stringWidth(base + "…") > ancho) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "…";
    }

    public static String html(String texto, int ancho) {
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        return "<html><div style='width:" + ancho + "px'>" + seguro + "</div></html>";
    }

    public static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        return etiqueta;
    }

    public static void instalar() {
        UIManager.put("ToolTip.background", new ColorUIResource(FONDO_2));
        UIManager.put("ToolTip.foreground", new ColorUIResource(TEXTO));
        UIManager.put("ToolTip.font", texto(Font.BOLD, 13f));
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 3), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        ToolTipManager.sharedInstance().setInitialDelay(350);
    }
}
