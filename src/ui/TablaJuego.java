package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public final class TablaJuego {

    private static final String FILA_HOVER = "tablaJuego.filaHover";

    private TablaJuego() {
    }

    public static JScrollPane crear(JTable tabla) {
        estilizar(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        estilizarScroll(scroll);
        return scroll;
    }

    public static void estilizar(JTable tabla) {
        tabla.setOpaque(false);
        tabla.setFillsViewportHeight(true);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setRowHeight(46);
        tabla.setFont(Tema.texto(Font.PLAIN, 14f));
        tabla.setForeground(Tema.TEXTO);
        tabla.setFocusable(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, texto(SwingConstants.LEFT));
        tabla.setDefaultRenderer(Number.class, texto(SwingConstants.CENTER));
        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setDefaultRenderer(new Cabecera());
        cabecera.setReorderingAllowed(false);
        cabecera.setOpaque(false);
        cabecera.setPreferredSize(new Dimension(0, 44));
        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                marcar(tabla, tabla.rowAtPoint(e.getPoint()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                marcar(tabla, -1);
            }
        };
        tabla.addMouseMotionListener(hover);
        tabla.addMouseListener(hover);
    }

    public static void columna(JTable tabla, int indice, int ancho, TableCellRenderer render) {
        TableColumn col = tabla.getColumnModel().getColumn(indice);
        col.setPreferredWidth(ancho);
        if (render != null) {
            col.setCellRenderer(render);
        }
    }

    private static void marcar(JTable tabla, int fila) {
        Object anterior = tabla.getClientProperty(FILA_HOVER);
        if (anterior instanceof Integer a && a == fila) {
            return;
        }
        tabla.putClientProperty(FILA_HOVER, fila);
        tabla.repaint();
    }

    public static void estilizarScroll(JScrollPane scroll) {
        JScrollBar vertical = scroll.getVerticalScrollBar();
        JScrollBar horizontal = scroll.getHorizontalScrollBar();
        vertical.setUI(new BarraUI());
        horizontal.setUI(new BarraUI());
        vertical.setOpaque(false);
        horizontal.setOpaque(false);
        vertical.setPreferredSize(new Dimension(10, 0));
        horizontal.setPreferredSize(new Dimension(0, 10));
        vertical.setUnitIncrement(18);
        horizontal.setUnitIncrement(18);
    }

    public static TableCellRenderer texto(int alineacion) {
        return new CeldaBase(alineacion) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                g2.setFont(tabla.getFont());
                String texto = valor == null ? "" : valor.toString();
                dibujarTexto(g2, texto, w, h, Tema.TEXTO);
            }
        };
    }

    public static TableCellRenderer numero(Color color) {
        return new CeldaBase(SwingConstants.CENTER) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                String texto = valor == null ? "" : valor.toString();
                Tema.textoPixel(g2, texto, (w - Tema.anchoPixel(texto, 2)) / 2, (h - Tema.altoPixel(2)) / 2 + 1, 2, color, true);
            }
        };
    }

    public static TableCellRenderer posicion() {
        return new CeldaBase(SwingConstants.CENTER) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                int pos = valor instanceof Integer i ? i : 0;
                Color color = switch (pos) {
                    case 1 -> Tema.AMBAR;
                    case 2 -> Tema.TEXTO_SUAVE;
                    case 3 -> Tema.BRONCE;
                    default -> null;
                };
                g2.setFont(Tema.titulo(15f));
                if (color == null) {
                    dibujarTexto(g2, String.valueOf(pos), w, h, Tema.TEXTO_SUAVE);
                    return;
                }
                int d = 28;
                int mx = (w - d) / 2;
                int my = (h - d) / 2;
                Tema.ventana(g2, mx, my, d, d, color, Tema.mezclar(color, Color.WHITE, 0.4f));
                String texto = String.valueOf(pos);
                Tema.textoPixel(g2, texto, mx + (d - Tema.anchoPixel(texto, 2)) / 2 + 1, my + (d - Tema.altoPixel(2)) / 2 + 1, 2, Tema.TEXTO, false);
            }
        };
    }

    public static TableCellRenderer avatar(IntFunction<Color> colorPorFila) {
        return new CeldaBase(SwingConstants.LEFT) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                String nombre = valor == null ? "" : valor.toString();
                Iconos.avatar(g2, nombre, 12, (h - 30) / 2.0 - 1, 28, colorPorFila.apply(fila));
                g2.setFont(Tema.texto(Font.BOLD, 15f));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(Tema.TEXTO);
                g2.drawString(Tema.recortar(nombre, fm, w - 58), 52, (h + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
    }

    public static TableCellRenderer insignia(Function<Object, Color> colorDe) {
        return new CeldaBase(SwingConstants.CENTER) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                if (valor == null) {
                    return;
                }
                String texto = valor.toString().toUpperCase();
                Color color = colorDe.apply(valor);
                g2.setFont(Tema.texto(Font.BOLD, 12f));
                FontMetrics fm = g2.getFontMetrics();
                texto = Tema.recortar(texto, fm, w - 34);
                int ancho = fm.stringWidth(texto) + 24;
                int alto = 26;
                g2.setColor(Tema.mezclar(color, Color.BLACK, 0.25f));
                g2.fill(Tema.forma((w - ancho) / 2, (h - alto) / 2, ancho, alto, 4));
                g2.setColor(Tema.mezclar(Tema.FONDO_2, color, 0.18f));
                g2.fill(Tema.forma((w - ancho) / 2 + 2, (h - alto) / 2 + 2, ancho - 4, alto - 4, 3));
                dibujarTexto(g2, texto, w, h, Tema.mezclar(color, Color.BLACK, 0.45f));
            }
        };
    }

    public static TableCellRenderer barra(int maximo, Function<Integer, Color> colorDe) {
        return new CeldaBase(SwingConstants.LEFT) {
            @Override
            protected void pintar(Graphics2D g2, int w, int h) {
                int v = valor instanceof Integer i ? i : 0;
                Color color = colorDe.apply(v);
                double fraccion = Math.max(0, Math.min(1, v / (double) maximo));
                double anchoBarra = Math.max(20, w - 80);
                Tema.barraSegmentos(g2, 14, h / 2.0 - 6, anchoBarra, 12, fraccion, color, 10);
                String texto = String.valueOf(v);
                Tema.textoPixel(g2, texto, (int) (anchoBarra + 24), (h - Tema.altoPixel(2)) / 2 + 1, 2, color, true);
            }
        };
    }

    public abstract static class CeldaBase extends JComponent implements TableCellRenderer {

        protected final int alineacion;
        protected JTable tabla;
        protected Object valor;
        protected boolean seleccionada;
        protected int fila;
        protected int columna;

        protected CeldaBase(int alineacion) {
            this.alineacion = alineacion;
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            this.tabla = tabla;
            this.valor = valor;
            this.seleccionada = seleccionada;
            this.fila = fila;
            this.columna = columna;
            String texto = valor == null ? null : valor.toString();
            setToolTipText(texto != null && texto.length() > 24 ? texto : null);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            int w = getWidth();
            int h = getHeight();
            Object hover = tabla.getClientProperty(FILA_HOVER);
            boolean enHover = hover instanceof Integer i && i == fila;
            Color fondo;
            if (seleccionada) {
                fondo = new Color(0xD0E4FF);
            } else if (enHover) {
                fondo = new Color(0xFFF4C0);
            } else if (fila % 2 == 0) {
                fondo = Tema.FONDO_2;
            } else {
                fondo = Tema.PANEL;
            }
            g2.setColor(fondo);
            g2.fillRect(0, 0, w, h);
            if (columna == 0 && (seleccionada || enHover)) {
                g2.setColor(Tema.CIAN);
                g2.fillRect(0, 0, 5, h);
            }
            g2.setColor(new Color(0xD8DCE4));
            g2.drawLine(0, h - 1, w, h - 1);
            pintar(g2, w, h);
            g2.dispose();
        }

        protected void dibujarTexto(Graphics2D g2, String texto, int w, int h, Color color) {
            FontMetrics fm = g2.getFontMetrics();
            String visible = Tema.recortar(texto, fm, w - 24);
            int ancho = fm.stringWidth(visible);
            int x = switch (alineacion) {
                case SwingConstants.CENTER -> (w - ancho) / 2;
                case SwingConstants.RIGHT -> w - ancho - 12;
                default -> 14;
            };
            g2.setColor(color);
            g2.drawString(visible, x, (h + fm.getAscent() - fm.getDescent()) / 2);
        }

        protected abstract void pintar(Graphics2D g2, int w, int h);
    }

    private static class Cabecera extends JComponent implements TableCellRenderer {

        private String texto = "";
        private int alineacion = SwingConstants.LEFT;
        private boolean ultima;

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            texto = valor == null ? "" : valor.toString().toUpperCase();
            TableCellRenderer render = tabla.getColumnModel().getColumn(columna).getCellRenderer();
            if (render == null) {
                render = tabla.getDefaultRenderer(tabla.getColumnClass(columna));
            }
            alineacion = render instanceof CeldaBase celda ? celda.alineacion : SwingConstants.LEFT;
            ultima = columna == tabla.getColumnCount() - 1;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(Tema.PANEL_CLARO);
            g2.fillRect(0, 0, w, h);
            g2.setColor(Tema.BORDE);
            g2.fillRect(0, h - 3, w, 3);
            if (!ultima) {
                g2.setColor(Tema.FONDO_2);
                g2.fillRect(w - 2, 8, 2, h - 16);
            }
            int ancho = Tema.anchoPixel(texto, 2);
            if (ancho <= w - 16) {
                int x = alineacion == SwingConstants.CENTER ? (w - ancho) / 2 : 14;
                Tema.textoPixel(g2, texto, x, (h - 4 - Tema.altoPixel(2)) / 2 + 1, 2, Tema.TEXTO, true);
            } else {
                g2.setFont(Tema.texto(Font.BOLD, 12f));
                FontMetrics fm = g2.getFontMetrics();
                String visible = Tema.recortar(texto, fm, w - 16);
                int x = alineacion == SwingConstants.CENTER ? (w - fm.stringWidth(visible)) / 2 : 14;
                g2.setColor(Tema.TEXTO);
                g2.drawString(visible, x, (h - 4 + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.dispose();
        }
    }

    private static class BarraUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
        }

        @Override
        protected JButton createDecreaseButton(int orientacion) {
            return sinBoton();
        }

        @Override
        protected JButton createIncreaseButton(int orientacion) {
            return sinBoton();
        }

        private JButton sinBoton() {
            JButton boton = new JButton();
            Dimension cero = new Dimension(0, 0);
            boton.setPreferredSize(cero);
            boton.setMinimumSize(cero);
            boton.setMaximumSize(cero);
            return boton;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = Tema.preparar(g);
            g2.setColor(Tema.PANEL);
            g2.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) {
                return;
            }
            Graphics2D g2 = Tema.preparar(g);
            g2.setColor(isThumbRollover() ? Tema.CIAN : Tema.GRIS);
            g2.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
            g2.dispose();
        }
    }
}
