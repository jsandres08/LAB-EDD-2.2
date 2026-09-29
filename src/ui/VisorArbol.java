package ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JComponent;

public class VisorArbol extends JComponent {

    public static final int ANCHO_NODO = 196;
    public static final int ALTO_NODO = 76;
    public static final int SEP_X = 26;
    public static final int SEP_Y = 86;
    private static final int MARGEN = 40;
    private static final int COLUMNA_NIVELES = 120;
    private static final double ESCALA_MIN = 0.08;
    private static final double ESCALA_MAX = 2.5;

    private final Font fuenteCategoria = Tema.texto(Font.BOLD, 11f);
    private final Font fuenteTitulo = Tema.texto(Font.BOLD, 12.5f);
    private final Font fuenteEtiqueta = Tema.texto(Font.BOLD, 11f);
    private final Font fuenteOrden = Tema.titulo(13f);

    private final List<NodoVisual> visibles = new ArrayList<>();
    private final Set<NodoVisual> ruta = new HashSet<>();
    private final Set<NodoVisual> resaltados = new HashSet<>();
    private Map<NodoVisual, Integer> orden;
    private NodoVisual raiz;
    private NodoVisual raizVista;
    private NodoVisual hover;
    private NodoVisual seleccionado;
    private int nivelMaximo = Integer.MAX_VALUE;
    private int profundidad;
    private double escala = 1;
    private double offX;
    private double offY;
    private boolean encuadrePendiente;
    private Point inicioArrastre;
    private boolean arrastrando;
    private double offXInicio;
    private double offYInicio;
    private String mensajeVacio = "No hay datos para mostrar";
    private Consumer<NodoVisual> alSeleccionar = n -> {
    };
    private Runnable alCambiarVista = () -> {
    };

    public VisorArbol() {
        setOpaque(false);
        setFocusable(true);
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                inicioArrastre = e.getPoint();
                offXInicio = offX;
                offYInicio = offY;
                arrastrando = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (inicioArrastre == null) {
                    return;
                }
                double dx = e.getX() - inicioArrastre.x;
                double dy = e.getY() - inicioArrastre.y;
                if (!arrastrando && Math.hypot(dx, dy) > 4) {
                    arrastrando = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                if (arrastrando) {
                    offX = offXInicio + dx;
                    offY = offYInicio + dy;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!arrastrando && e.getButton() == MouseEvent.BUTTON1) {
                    NodoVisual nodo = nodoEn(e.getPoint());
                    seleccionar(nodo);
                    if (nodo != null && e.getClickCount() == 2) {
                        enfocar(nodo);
                    }
                }
                arrastrando = false;
                inicioArrastre = null;
                actualizarCursor(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                NodoVisual nodo = nodoEn(e.getPoint());
                if (nodo != hover) {
                    hover = nodo;
                    repaint();
                }
                actualizarCursor(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hover != null) {
                    hover = null;
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown()) {
                    irA(escala, offX - e.getPreciseWheelRotation() * 90, offY);
                } else {
                    zoom(Math.pow(1.15, -e.getPreciseWheelRotation()), e.getPoint());
                }
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
        addMouseWheelListener(raton);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (encuadrePendiente && getWidth() > 0) {
                    encuadrePendiente = false;
                    encuadrar();
                }
            }
        });
    }

    public void setAlSeleccionar(Consumer<NodoVisual> accion) {
        this.alSeleccionar = accion;
    }

    public void setAlCambiarVista(Runnable accion) {
        this.alCambiarVista = accion;
    }

    public void setMensajeVacio(String mensaje) {
        this.mensajeVacio = mensaje;
        repaint();
    }

    public void setRaiz(NodoVisual nuevaRaiz, int nivelesIniciales) {
        raiz = nuevaRaiz;
        raizVista = nuevaRaiz;
        hover = null;
        seleccionado = null;
        ruta.clear();
        resaltados.clear();
        orden = null;
        if (raiz != null) {
            asignarNiveles(raiz, 0);
        }
        nivelMaximo = nivelesIniciales <= 0 ? Integer.MAX_VALUE : nivelesIniciales;
        reorganizar();
        alSeleccionar.accept(null);
        if (getWidth() > 0) {
            encuadrar();
        } else {
            encuadrePendiente = true;
        }
    }

    public NodoVisual getRaiz() {
        return raiz;
    }

    public NodoVisual getSeleccionado() {
        return seleccionado;
    }

    public boolean isSubarbol() {
        return raizVista != raiz;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public int getNivelesVisibles() {
        return Math.min(nivelMaximo, profundidad);
    }

    public void setNivelesVisibles(int niveles) {
        if (raizVista == null) {
            return;
        }
        nivelMaximo = Math.max(1, Math.min(profundidad, niveles));
        if (seleccionado != null && !visible(seleccionado)) {
            seleccionar(null);
        }
        hover = null;
        reorganizar();
        encuadrar();
    }

    public void setOrden(Map<NodoVisual, Integer> orden) {
        this.orden = orden;
        repaint();
    }

    public void setResaltados(Set<NodoVisual> nodos) {
        resaltados.clear();
        resaltados.addAll(nodos);
        repaint();
    }

    public void mostrarSubarbol(NodoVisual nodo) {
        if (nodo == null) {
            return;
        }
        raizVista = nodo;
        nivelMaximo = Integer.MAX_VALUE;
        hover = null;
        reorganizar();
        encuadrar();
    }

    public void mostrarTodo() {
        raizVista = raiz;
        hover = null;
        reorganizar();
        encuadrar();
    }

    public void acercar() {
        zoom(1.25, new Point(getWidth() / 2, getHeight() / 2));
    }

    public void alejar() {
        zoom(1 / 1.25, new Point(getWidth() / 2, getHeight() / 2));
    }

    public void encuadrar() {
        if (raizVista == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = 0;
        for (NodoVisual n : visibles) {
            minX = Math.min(minX, n.x - ANCHO_NODO / 2.0);
            maxX = Math.max(maxX, n.x + ANCHO_NODO / 2.0);
            maxY = Math.max(maxY, n.y + ALTO_NODO);
        }
        double anchoArbol = maxX - minX;
        double anchoUtil = getWidth() - 2.0 * MARGEN - COLUMNA_NIVELES;
        double ajusteAncho = anchoUtil / anchoArbol;
        double ajusteAlto = (getHeight() - 2.0 * MARGEN) / maxY;
        double s;
        double referencia;
        if (ajusteAncho >= 0.3) {
            s = Math.min(1.1, Math.min(ajusteAncho, ajusteAlto));
            referencia = (minX + maxX) / 2;
        } else {
            s = Math.max(ESCALA_MIN, Math.min(0.8, ajusteAlto));
            referencia = raizVista.x;
        }
        double centro = COLUMNA_NIVELES + (getWidth() - COLUMNA_NIVELES) / 2.0;
        irA(s, centro - referencia * s, getHeight() / 2.0 - maxY / 2 * s);
    }

    public void reiniciar() {
        if (raiz == null) {
            return;
        }
        raizVista = raiz;
        nivelMaximo = Integer.MAX_VALUE;
        hover = null;
        orden = null;
        resaltados.clear();
        seleccionar(null);
        reorganizar();
        encuadrar();
    }

    public void enfocar(NodoVisual nodo) {
        double s = Math.max(escala, 0.9);
        irA(s, getWidth() / 2.0 - nodo.x * s, getHeight() / 2.0 - (nodo.y + ALTO_NODO / 2.0) * s);
    }

    public void seleccionar(NodoVisual nodo) {
        seleccionado = nodo;
        ruta.clear();
        for (NodoVisual n = nodo; n != null; n = n.getPadre()) {
            ruta.add(n);
        }
        alSeleccionar.accept(nodo);
        repaint();
    }

    public void seleccionarYEnfocar(NodoVisual nodo) {
        if (!visible(nodo)) {
            raizVista = raiz;
            nivelMaximo = Math.max(nivelMaximo == Integer.MAX_VALUE ? 1 : nivelMaximo, nodo.nivel + 1);
            reorganizar();
        }
        seleccionar(nodo);
        enfocar(nodo);
    }

    private boolean visible(NodoVisual nodo) {
        return visibles.contains(nodo);
    }

    private void asignarNiveles(NodoVisual n, int nivel) {
        n.nivel = nivel;
        for (NodoVisual h : n.getHijos()) {
            asignarNiveles(h, nivel + 1);
        }
    }

    private int calcularProfundidad(NodoVisual n, int nivel) {
        int max = nivel;
        for (NodoVisual h : n.getHijos()) {
            max = Math.max(max, calcularProfundidad(h, nivel + 1));
        }
        return max;
    }

    private void reorganizar() {
        visibles.clear();
        if (raizVista != null) {
            profundidad = calcularProfundidad(raizVista, 1);
            posicionar(raizVista, 0, new int[]{0});
        } else {
            profundidad = 0;
        }
        alCambiarVista.run();
        repaint();
    }

    private void posicionar(NodoVisual n, int fila, int[] columna) {
        n.fila = fila;
        visibles.add(n);
        List<NodoVisual> hijos = hijosVisibles(n);
        if (hijos.isEmpty()) {
            n.x = columna[0]++ * (ANCHO_NODO + SEP_X) + ANCHO_NODO / 2.0;
        } else {
            for (NodoVisual h : hijos) {
                posicionar(h, fila + 1, columna);
            }
            n.x = (hijos.get(0).x + hijos.get(hijos.size() - 1).x) / 2;
        }
        n.y = fila * (ALTO_NODO + SEP_Y);
    }

    private List<NodoVisual> hijosVisibles(NodoVisual n) {
        return n.fila + 1 < nivelMaximo ? n.getHijos() : List.of();
    }

    private int descendientes(NodoVisual n) {
        int total = 0;
        for (NodoVisual h : n.getHijos()) {
            total += 1 + descendientes(h);
        }
        return total;
    }

    private NodoVisual nodoEn(Point p) {
        double wx = (p.x - offX) / escala;
        double wy = (p.y - offY) / escala;
        for (int i = visibles.size() - 1; i >= 0; i--) {
            NodoVisual n = visibles.get(i);
            if (wx >= n.x - ANCHO_NODO / 2.0 && wx <= n.x + ANCHO_NODO / 2.0 && wy >= n.y && wy <= n.y + ALTO_NODO) {
                return n;
            }
        }
        return null;
    }

    private void actualizarCursor(Point p) {
        if (!arrastrando) {
            setCursor(Cursor.getPredefinedCursor(nodoEn(p) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        }
    }

    private void zoom(double factor, Point ancla) {
        double nueva = Math.max(ESCALA_MIN, Math.min(ESCALA_MAX, escala * factor));
        double f = nueva / escala;
        irA(nueva, ancla.x - (ancla.x - offX) * f, ancla.y - (ancla.y - offY) * f);
    }

    private void irA(double s, double ox, double oy) {
        escala = s;
        offX = ox;
        offY = oy;
        alCambiarVista.run();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Tema.preparar(g);
        int w = getWidth();
        int h = getHeight();
        if (raizVista == null) {
            Iconos.dibujar(g2, Iconos.Tipo.ARBOL, w / 2.0 - 28, h / 2.0 - 60, 56, Tema.alfa(Tema.TEXTO_SUAVE, 150));
            g2.setFont(Tema.texto(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.drawString(mensajeVacio, (w - fm.stringWidth(mensajeVacio)) / 2, h / 2 + 24);
            g2.dispose();
            return;
        }

        Graphics2D m = (Graphics2D) g2.create();
        m.translate(offX, offY);
        m.scale(escala, escala);
        dibujarRejilla(m, w, h);
        for (NodoVisual n : visibles) {
            for (NodoVisual hijo : hijosVisibles(n)) {
                dibujarArista(m, n, hijo);
            }
        }
        for (NodoVisual n : visibles) {
            for (NodoVisual hijo : hijosVisibles(n)) {
                if (hijo.getEtiquetaArista() != null) {
                    dibujarEtiqueta(m, n, hijo);
                }
            }
        }
        for (NodoVisual n : visibles) {
            if (n != hover && n != seleccionado) {
                dibujarNodo(m, n);
            }
        }
        if (hover != null && hover != seleccionado) {
            dibujarNodo(m, hover);
        }
        if (seleccionado != null && visible(seleccionado)) {
            dibujarNodo(m, seleccionado);
        }
        m.dispose();
        dibujarNiveles(g2);

        String zoom = "ZOOM " + Math.round(escala * 100) + "%";
        g2.setFont(Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.1f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Tema.alfa(Tema.TEXTO_SUAVE, 200));
        g2.drawString(zoom, w - fm.stringWidth(zoom) - 14, h - 12);
        if (isSubarbol()) {
            String aviso = "VISTA DE SUBÁRBOL";
            g2.setColor(Tema.AMBAR);
            g2.drawString(aviso, 14, h - 12);
        }
        g2.dispose();
    }

    private void dibujarRejilla(Graphics2D g, int w, int h) {
        double paso = 40;
        double x0 = -offX / escala;
        double y0 = -offY / escala;
        double x1 = (w - offX) / escala;
        double y1 = (h - offY) / escala;
        if ((x1 - x0) / paso * ((y1 - y0) / paso) > 5000) {
            return;
        }
        g.setColor(new Color(0xE0E6EE));
        for (double x = Math.floor(x0 / paso) * paso; x < x1; x += paso) {
            for (double y = Math.floor(y0 / paso) * paso; y < y1; y += paso) {
                g.fill(new Rectangle2D.Double(x - 1.5, y - 1.5, 3, 3));
            }
        }
    }

    private boolean aristaActiva(NodoVisual padre, NodoVisual hijo) {
        return seleccionado != null && (padre == seleccionado || (ruta.contains(padre) && ruta.contains(hijo)));
    }

    private void dibujarNiveles(Graphics2D g) {
        int maxFila = 0;
        for (NodoVisual n : visibles) {
            maxFila = Math.max(maxFila, n.fila);
        }
        int base = raizVista.nivel;
        g.setColor(new Color(0xEEF2F6));
        g.fillRect(0, 0, COLUMNA_NIVELES - 8, getHeight());
        g.setColor(Tema.SOMBRA);
        g.fillRect(COLUMNA_NIVELES - 8, 0, 3, getHeight());
        g.setFont(fuenteEtiqueta);
        FontMetrics fm = g.getFontMetrics();
        for (int f = 0; f <= maxFila; f++) {
            String texto = "NIVEL " + (base + f);
            int ancho = fm.stringWidth(texto) + 24;
            int x = 12;
            int y = (int) Math.round(offY + (f * (ALTO_NODO + SEP_Y) + ALTO_NODO / 2.0) * escala - 13);
            if (y < -26 || y > getHeight()) {
                continue;
            }
            Tema.bloque(g, x, y, ancho, 26, 4, Tema.PANEL_CLARO, Tema.BORDE, 0);
            g.setColor(Tema.TEXTO);
            g.drawString(texto, x + 12, y + 13 + (fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    private double medio(NodoVisual padre) {
        return padre.y + ALTO_NODO + SEP_Y * 0.35;
    }

    private Path2D curva(NodoVisual padre, NodoVisual hijo) {
        double x1 = Math.round(padre.x);
        double y1 = padre.y + ALTO_NODO + 4;
        double x2 = Math.round(hijo.x);
        double y2 = hijo.y - 8;
        double ym = Math.round(medio(padre));
        Path2D camino = new Path2D.Double();
        camino.moveTo(x1, y1);
        camino.lineTo(x1, ym);
        camino.lineTo(x2, ym);
        camino.lineTo(x2, y2);
        return camino;
    }

    private void dibujarArista(Graphics2D g, NodoVisual padre, NodoVisual hijo) {
        Path2D camino = curva(padre, hijo);
        Color c;
        float grosor;
        if (aristaActiva(padre, hijo)) {
            c = padre == seleccionado ? hijo.getColor() : seleccionado.getColor();
            grosor = 5f;
        } else {
            boolean cercana = padre == hover || hijo == hover;
            c = cercana ? Tema.TEXTO : Tema.alfa(Tema.GRIS, seleccionado != null ? 110 : 255);
            grosor = cercana ? 4f : 3f;
        }
        g.setStroke(new BasicStroke(grosor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.setColor(c);
        g.draw(camino);
        g.fill(new Rectangle2D.Double(Math.round(hijo.x) - 5, hijo.y - 8, 10, 8));
    }

    private void dibujarEtiqueta(Graphics2D g, NodoVisual padre, NodoVisual hijo) {
        String texto = hijo.getEtiquetaArista().toUpperCase();
        g.setFont(fuenteEtiqueta);
        FontMetrics fm = g.getFontMetrics();
        int ancho = fm.stringWidth(texto) + 16;
        int alto = 20;
        double ym = medio(padre);
        int x = (int) Math.round(hijo.x - ancho / 2.0);
        int y = (int) Math.round((ym + hijo.y - 8) / 2.0 - alto / 2.0);
        boolean activa = aristaActiva(padre, hijo);
        Color color = activa ? (padre == seleccionado ? hijo.getColor() : seleccionado.getColor()) : Tema.BORDE;
        Composite anterior = g.getComposite();
        if (seleccionado != null && !activa) {
            g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        }
        Tema.bloque(g, x, y, ancho, alto, 3, Tema.FONDO_2, activa ? color : Tema.BORDE, 0);
        g.setColor(activa ? Tema.TEXTO : Tema.TEXTO_SUAVE);
        g.drawString(texto, x + 8, y + (alto + fm.getAscent() - fm.getDescent()) / 2);
        g.setComposite(anterior);
    }

    private void dibujarNodo(Graphics2D g, NodoVisual n) {
        boolean esHover = n == hover;
        boolean esSeleccionado = n == seleccionado;
        boolean encontrado = resaltados.contains(n);
        boolean atenuado = (seleccionado != null && !ruta.contains(n) && n.getPadre() != seleccionado)
                || (!resaltados.isEmpty() && !encontrado && !esSeleccionado);
        Composite anterior = g.getComposite();
        if (atenuado) {
            g.setComposite(AlphaComposite.SrcOver.derive(0.45f));
        }
        Color color = n.getColor();
        int x = (int) Math.round(n.x - ANCHO_NODO / 2.0);
        int y = (int) Math.round(n.y);
        if (esSeleccionado || encontrado) {
            g.setColor(encontrado ? new Color(0xF0C020) : color);
            g.fill(Tema.forma(x - 6, y - 6, ANCHO_NODO + 12, ALTO_NODO + 12, 10));
        }
        g.setColor(Tema.SOMBRA);
        g.fill(Tema.forma(x + 4, y + 4, ANCHO_NODO, ALTO_NODO, 8));
        Tema.ventana(g, x, y, ANCHO_NODO, ALTO_NODO, color, esHover ? Tema.mezclar(Tema.FONDO_2, color, 0.1f) : Tema.FONDO_2);
        g.setColor(Tema.mezclar(Tema.FONDO_2, color, esHover || esSeleccionado ? 0.55f : 0.4f));
        g.fillRect(x + 3, y + 3, ANCHO_NODO - 6, 22);
        g.setColor(Tema.BORDE);
        g.fillRect(x + 3, y + 25, ANCHO_NODO - 6, 2);

        g.setFont(fuenteCategoria);
        FontMetrics fc = g.getFontMetrics();
        g.setColor(Tema.mezclar(color, Color.BLACK, 0.5f));
        Integer paso = orden == null ? null : orden.get(n);
        int xCategoria = paso != null ? x + 30 : x + 10;
        g.drawString(Tema.recortar(n.getCategoria(), fc, ANCHO_NODO - (xCategoria - x) - 40), xCategoria, y + 17);
        String nivel = "N" + n.nivel;
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString(nivel, x + ANCHO_NODO - 10 - fc.stringWidth(nivel), y + 17);

        g.setFont(fuenteTitulo);
        FontMetrics fm = g.getFontMetrics();
        int ty = y + 44;
        for (String linea : Tema.envolver(n.getTitulo(), fm, ANCHO_NODO - 22, 2)) {
            g.setColor(Tema.TEXTO);
            g.drawString(linea, x + 10, ty);
            ty += fm.getHeight();
        }

        if (!n.getHijos().isEmpty() && hijosVisibles(n).isEmpty()) {
            String oculto = "+" + descendientes(n);
            g.setFont(fuenteCategoria);
            int ancho = fc.stringWidth(oculto) + 12;
            Tema.bloque(g, x + ANCHO_NODO - ancho - 6, y + ALTO_NODO - 10, ancho, 20, 2, Tema.FONDO_2, Tema.BORDE, 0);
            g.setColor(Tema.TEXTO);
            g.drawString(oculto, x + ANCHO_NODO - ancho, y + ALTO_NODO + 4);
        }

        if (paso != null) {
            String texto = String.valueOf(paso);
            int ancho = Math.max(30, Tema.anchoPixel(texto, 2) + 10);
            Tema.ventana(g, x - 10, y - 10, ancho, 30, Tema.AMBAR, new Color(0xF8D848));
            Tema.textoPixel(g, texto, x - 10 + (ancho - Tema.anchoPixel(texto, 2)) / 2 + 1, y - 10 + (30 - Tema.altoPixel(2)) / 2 + 1,
                    2, Tema.TEXTO, false);
        }
        g.setComposite(anterior);
    }

}
