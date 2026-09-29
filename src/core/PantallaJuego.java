package core;

import arbolclasificacion.Publicacion;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import ui.BarraProgreso;
import ui.BarraSuperior;
import ui.BotonJuego;
import ui.EtiquetaPixel;
import ui.FondoJuego;
import ui.Iconos;
import ui.Indicador;
import ui.Insignia;
import ui.PanelTarjeta;
import ui.TablaJuego;
import ui.Tema;

class PantallaJuego extends FondoJuego implements Pantalla {

    private final VentanaJuego ventana;
    private final GameEngine engine;
    private final BarraSuperior barra;
    private final BarraProgreso progreso = new BarraProgreso();
    private final ListaJugadores listaJugadores;
    private final PanelCiudad panelCiudad;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JPanel filaInsignias = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final EtiquetaPixel lblBanner = new EtiquetaPixel(" ", 3, Tema.TEXTO);
    private final JTextArea txtTexto = new JTextArea();
    private final JLabel lblAutor = new JLabel();
    private final JPanel cajaInfo = new JPanel();
    private final JPanel panelRuta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final AnilloTiempo anillo = new AnilloTiempo();
    private final JLabel lblAvisoTiempo = Tema.etiqueta("", Tema.texto(Font.PLAIN, 13f), Tema.TEXTO_SUAVE);
    private final JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
    private final JLabel lblPregunta;
    private final List<BotonJuego> botones = new ArrayList<>();
    private final DefaultTableModel modeloFinal = new DefaultTableModel(new Object[]{"#", "Jugador", "Rol", "Puntos", "Reputación"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final List<Color> coloresFinal = new ArrayList<>();
    private final Indicador indCiudadFinal = new Indicador("Puntaje de la ciudad", Iconos.Tipo.CIUDAD, Tema.CIAN, true);
    private final Indicador indEleccion = new Indicador("Elección del alcalde", Iconos.Tipo.TROFEO, Tema.VIOLETA, true);
    private final JLabel lblVeredicto = Tema.etiqueta("", Tema.texto(Font.BOLD, 16f), Tema.TEXTO);
    private final Timer reloj;
    private long restanteMs;
    private long totalMs = 1;
    private long ultimoTick;
    private boolean iniciado;

    PantallaJuego(VentanaJuego ventana, GameEngine engine) {
        super(new BorderLayout(0, 14));
        this.ventana = ventana;
        this.engine = engine;
        setBorder(new EmptyBorder(18, 22, 18, 22));

        BotonJuego btnMenu = new BotonJuego("MENÚ", Iconos.Tipo.VOLVER, BotonJuego.Estilo.VOLVER);
        btnMenu.addActionListener(e -> ventana.mostrar(VentanaJuego.MENU));
        barra = new BarraSuperior(btnMenu, Iconos.Tipo.CIUDAD, Tema.CIAN, "Ciudad Nova", "");
        barra.agregar(progreso);
        barra.agregar(boton("ÁRBOLES", Iconos.Tipo.ARBOL, Tema.VERDE, VentanaJuego.ARBOLES));
        barra.agregar(boton("ESTADÍSTICAS", Iconos.Tipo.ESTADISTICAS, Tema.AMBAR, VentanaJuego.ESTADISTICAS));
        barra.agregar(boton("AYUDA", Iconos.Tipo.AYUDA, Tema.VIOLETA, VentanaJuego.AYUDA));
        add(barra, BorderLayout.NORTH);

        listaJugadores = new ListaJugadores(engine);
        PanelTarjeta tarjetaJugadores = new PanelTarjeta(new BorderLayout()).conTitulo("Jugadores", Iconos.Tipo.USUARIO);
        tarjetaJugadores.add(listaJugadores, BorderLayout.NORTH);
        tarjetaJugadores.setPreferredSize(new Dimension(280, 0));

        PanelTarjeta tarjeta = new PanelTarjeta(new BorderLayout());
        tarjeta.setBorder(new EmptyBorder(24, 30, 22, 30));
        contenido.setOpaque(false);
        contenido.add(crearVistaJuego(), "juego");
        contenido.add(crearVistaFinal(), "final");
        tarjeta.add(contenido, BorderLayout.CENTER);

        PanelTarjeta tarjetaTiempo = new PanelTarjeta(new BorderLayout(0, 6)).conTitulo("Tiempo", Iconos.Tipo.RELOJ)
                .conAcento(Tema.AMBAR);
        JPanel envoltorioAnillo = new JPanel(new GridBagLayout());
        envoltorioAnillo.setOpaque(false);
        envoltorioAnillo.add(anillo);
        tarjetaTiempo.add(envoltorioAnillo, BorderLayout.CENTER);
        lblAvisoTiempo.setHorizontalAlignment(SwingConstants.CENTER);
        tarjetaTiempo.add(lblAvisoTiempo, BorderLayout.SOUTH);

        panelCiudad = new PanelCiudad(engine);
        PanelTarjeta tarjetaCiudad = new PanelTarjeta(new BorderLayout()).conTitulo("Ciudad", Iconos.Tipo.CIUDAD)
                .conAcento(Tema.VERDE);
        tarjetaCiudad.add(panelCiudad, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new BorderLayout(0, 14));
        derecha.setOpaque(false);
        derecha.setPreferredSize(new Dimension(290, 0));
        derecha.add(tarjetaTiempo, BorderLayout.NORTH);
        derecha.add(tarjetaCiudad, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setOpaque(false);
        centro.add(tarjetaJugadores, BorderLayout.WEST);
        centro.add(tarjeta, BorderLayout.CENTER);
        centro.add(derecha, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        PanelTarjeta tarjetaOpciones = new PanelTarjeta(new BorderLayout(0, 10)).sinEsquinas();
        tarjetaOpciones.setBorder(new EmptyBorder(12, 18, 14, 18));
        lblPregunta = Tema.etiqueta("¿Qué harás?", Tema.texto(Font.BOLD, 13f), Tema.TEXTO_SUAVE);
        lblPregunta.setHorizontalAlignment(SwingConstants.CENTER);
        tarjetaOpciones.add(lblPregunta, BorderLayout.NORTH);
        panelOpciones.setOpaque(false);
        tarjetaOpciones.add(panelOpciones, BorderLayout.CENTER);
        add(tarjetaOpciones, BorderLayout.SOUTH);

        reloj = new Timer(100, e -> tick());
        registrarAtajos();
    }

    private BotonJuego boton(String texto, Iconos.Tipo icono, Color color, String destino) {
        BotonJuego b = new BotonJuego(texto, icono, BotonJuego.Estilo.SECUNDARIO).conAcento(color);
        b.addActionListener(e -> ventana.mostrar(destino));
        return b;
    }

    private JPanel crearVistaJuego() {
        JPanel vista = new JPanel(new BorderLayout(0, 14));
        vista.setOpaque(false);

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        filaInsignias.setOpaque(false);
        filaInsignias.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(filaInsignias);
        cabecera.add(Box.createVerticalStrut(12));
        lblBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(lblBanner);
        vista.add(cabecera, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 14));
        cuerpo.setOpaque(false);
        JPanel textos = new JPanel(new BorderLayout(0, 6));
        textos.setOpaque(false);
        txtTexto.setEditable(false);
        txtTexto.setFocusable(false);
        txtTexto.setOpaque(false);
        txtTexto.setLineWrap(true);
        txtTexto.setWrapStyleWord(true);
        txtTexto.setFont(Tema.texto(Font.BOLD, 23f));
        txtTexto.setForeground(Tema.TEXTO);
        txtTexto.setBorder(null);
        textos.add(txtTexto, BorderLayout.CENTER);
        lblAutor.setFont(Tema.texto(Font.PLAIN, 14f));
        lblAutor.setForeground(Tema.TEXTO_SUAVE);
        lblAutor.setIconTextGap(8);
        textos.add(lblAutor, BorderLayout.SOUTH);
        cuerpo.add(textos, BorderLayout.NORTH);
        cajaInfo.setOpaque(false);
        cajaInfo.setLayout(new BoxLayout(cajaInfo, BoxLayout.Y_AXIS));
        cuerpo.add(cajaInfo, BorderLayout.CENTER);
        vista.add(cuerpo, BorderLayout.CENTER);

        panelRuta.setOpaque(false);
        vista.add(panelRuta, BorderLayout.SOUTH);
        return vista;
    }

    private JPanel crearVistaFinal() {
        JPanel vista = new JPanel(new BorderLayout(0, 14));
        vista.setOpaque(false);

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        EtiquetaPixel titulo = new EtiquetaPixel("RESULTADOS DE LAS ELECCIONES", 3, Tema.AMBAR);
        titulo.setIcono(Iconos.Tipo.TROFEO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(titulo);
        cabecera.add(Box.createVerticalStrut(8));
        lblVeredicto.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(lblVeredicto);
        cabecera.add(Box.createVerticalStrut(12));
        JPanel indicadores = new JPanel(new GridLayout(1, 2, 14, 0));
        indicadores.setOpaque(false);
        indicadores.setAlignmentX(Component.LEFT_ALIGNMENT);
        indicadores.add(indCiudadFinal);
        indicadores.add(indEleccion);
        cabecera.add(indicadores);
        vista.add(cabecera, BorderLayout.NORTH);

        JTable tabla = new JTable(modeloFinal);
        vista.add(TablaJuego.crear(tabla), BorderLayout.CENTER);
        TablaJuego.columna(tabla, 0, 60, TablaJuego.posicion());
        TablaJuego.columna(tabla, 1, 200, TablaJuego.avatar(coloresFinal::get));
        TablaJuego.columna(tabla, 2, 140, TablaJuego.insignia(EstiloJuego::colorRol));
        TablaJuego.columna(tabla, 3, 100, TablaJuego.numero(Tema.CIAN));
        TablaJuego.columna(tabla, 4, 200, TablaJuego.barra(100, EstiloJuego::colorReputacion));
        return vista;
    }

    private void registrarAtajos() {
        for (int i = 1; i <= 9; i++) {
            int indice = i - 1;
            String clave = "opcion" + i;
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), clave);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), clave);
            getActionMap().put(clave, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isShowing() && indice < botones.size()) {
                        botones.get(indice).doClick();
                    }
                }
            });
        }
    }

    @Override
    public void alMostrar() {
        if (!iniciado) {
            iniciado = true;
            actualizar();
        } else {
            repaint();
            reanudar();
        }
    }

    @Override
    public void alOcultar() {
        reloj.stop();
    }

    private void actualizar() {
        int total = engine.getTotalPublicaciones();
        int indice = engine.getIndicePublicacion();
        listaJugadores.repaint();
        panelCiudad.repaint();

        if (engine.isPartidaTerminada()) {
            reloj.stop();
            barra.setTitulo("Fin de la campaña");
            barra.setSubtitulo("Ciudad Nova ha votado");
            progreso.setValores(total, -1, total);
            anillo.setTiempo(0, 1);
            lblAvisoTiempo.setText(Tema.html("La partida terminó.", 200));
            mostrarFinal();
            return;
        }

        boolean resuelta = engine.isEsperandoContinuar();
        Jugador jugador = engine.getJugadorActual();
        barra.setTitulo("Ciudad Nova");
        barra.setSubtitulo("Publicación " + (indice + 1) + " de " + total + "  ·  Turno de " + jugador.getNombre()
                + " (" + EstiloJuego.nombreRol(jugador.getRol()) + ")");
        progreso.setValores(resuelta ? indice + 1 : indice, resuelta ? -1 : indice, total);

        NodoDecision nodo = engine.getNodoActual();
        Publicacion datos = engine.getPublicacionClasificada();
        filaInsignias.removeAll();
        cajaInfo.removeAll();
        lblBanner.setVisible(false);
        lblAutor.setVisible(false);

        if (resuelta) {
            filaInsignias.add(new Insignia("Resultado", Tema.TEXTO_SUAVE, Iconos.Tipo.INFO, 12f));
            if (engine.isTiempoAgotado()) {
                banner("¡TIEMPO AGOTADO!", Tema.AMBAR, Iconos.Tipo.RELOJ);
                txtTexto.setText(engine.getPublicacionActual());
            } else if (Boolean.TRUE.equals(nodo.getCorrecto())) {
                banner("¡DECISIÓN RESPONSABLE!", Tema.VERDE, Iconos.Tipo.CHECK);
                txtTexto.setText(nodo.getTexto());
            } else {
                banner("DECISIÓN IRRESPONSABLE", Tema.MAGENTA, Iconos.Tipo.CERRAR);
                txtTexto.setText(nodo.getTexto());
            }
            if (datos != null) {
                filaInsignias.add(new Insignia(datos.getCategoria(), datos.isVeracidad() ? Tema.VERDE : Tema.MAGENTA,
                        Iconos.Tipo.CAPAS, 12f));
            }
            mostrarConsecuencias(engine.getUltimoEfecto());
        } else {
            if (engine.isViral()) {
                filaInsignias.add(new Insignia("Información viral · impacto ×2", Tema.MAGENTA, Iconos.Tipo.COMPARTIR, 12f));
            }
            if (nodo.getTipo() == TipoNodo.PREGUNTA) {
                filaInsignias.add(new Insignia("Verificación", Tema.AMBAR, Iconos.Tipo.VERIFICAR, 12f), 0);
                txtTexto.setText(nodo.getTexto());
                mostrarVerificacion(datos);
            } else {
                filaInsignias.add(new Insignia("Publicación " + (indice + 1) + " en Civitas", Tema.CIAN, Iconos.Tipo.INFO, 12f), 0);
                txtTexto.setText("«" + nodo.getTexto() + "»");
                if (datos != null) {
                    lblAutor.setText("Publicado por " + datos.getAutor());
                    lblAutor.setIcon(Iconos.icono(Iconos.Tipo.USUARIO, 16, Tema.TEXTO_SUAVE));
                    lblAutor.setVisible(true);
                }
                consejo(jugador);
            }
        }
        filaInsignias.revalidate();
        cajaInfo.revalidate();
        cajaInfo.repaint();

        panelRuta.removeAll();
        boolean hayRuta = resuelta || nodo.getTipo() != TipoNodo.PUBLICACION;
        if (hayRuta && !engine.getRutaActual().isEmpty()) {
            panelRuta.add(Tema.etiqueta("CAMINO EN EL ÁRBOL", Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.15f), Tema.TEXTO_SUAVE));
            for (String paso : engine.getRutaActual()) {
                panelRuta.add(new Insignia(paso, EstiloJuego.colorOpcion(paso), EstiloJuego.iconoOpcion(paso), 11f));
            }
        }
        panelRuta.revalidate();
        panelRuta.repaint();

        tarjetas.show(contenido, "juego");
        lblPregunta.setText(resuelta ? "Pulsa Continuar para pasar el turno" : "¿Qué harás, " + jugador.getNombre()
                + "?  ·  También puedes usar los números del teclado");
        generarOpciones(engine.getOpcionesDisponibles());
        lblAvisoTiempo.setText(Tema.html(engine.isViral() && !resuelta
                ? "¡Se está compartiendo rápido! Tienes menos tiempo." : "Si llega a cero, pierdes 5 de reputación.", 200));
        reiniciarTemporizador(resuelta ? 10 : engine.getSegundosLimite());
    }

    private void banner(String texto, Color color, Iconos.Tipo icono) {
        lblBanner.setTexto(texto);
        lblBanner.setColor(color);
        lblBanner.setIcono(icono);
        lblBanner.setVisible(true);
    }

    private void consejo(Jugador jugador) {
        String texto = switch (jugador.getRol()) {
            case PERIODISTA -> "Como Periodista, verificar te da +5 pts extra y duplica la información verificada.";
            case INFLUENCER -> "Como Influencer, todo lo que hagas tendrá el doble de impacto en la ciudad. ¡Cuidado!";
            case ALCALDE -> "Como Candidato, tus decisiones duplican su efecto sobre la confianza ciudadana.";
            case CIUDADANO -> "Como Ciudadano, tus decisiones duplican su efecto en convivencia y bienestar.";
        };
        cajaInfo.add(Box.createVerticalStrut(6));
        JLabel etiqueta = Tema.etiqueta(Tema.html(texto, 460), Tema.texto(Font.PLAIN, 14f), Tema.TEXTO_SUAVE);
        etiqueta.setIcon(Iconos.icono(Iconos.Tipo.INFO, 18, EstiloJuego.colorRol(jugador.getRol())));
        etiqueta.setIconTextGap(10);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        cajaInfo.add(etiqueta);
    }

    private void mostrarVerificacion(Publicacion datos) {
        cajaInfo.add(titulo("Resultado de la verificación · árbol de clasificación"));
        String ruta = engine.getResultadoVerificacion();
        JPanel fila = filaChips();
        if (ruta == null) {
            fila.add(new Insignia("No se encontró en el árbol", Tema.GRIS, null, 12f));
        } else {
            String[] partes = ruta.split(" > ");
            for (int i = 0; i < partes.length; i++) {
                Color color = i == 1 ? ("VERDADERA".equals(partes[i]) ? Tema.VERDE : Tema.MAGENTA) : i == 0 ? Tema.VIOLETA : Tema.AZUL;
                fila.add(new Insignia(partes[i], color, i == 0 ? Iconos.Tipo.ARBOL : null, 12f));
                if (i < partes.length - 1) {
                    fila.add(new JLabel(Iconos.icono(Iconos.Tipo.SIGUIENTE, 14, Tema.TEXTO_SUAVE)));
                }
            }
        }
        cajaInfo.add(fila);
        if (datos != null) {
            cajaInfo.add(Box.createVerticalStrut(8));
            JLabel autor = Tema.etiqueta("Fuente: " + datos.getAutor() + "  ·  ID #" + datos.getId(), Tema.texto(Font.PLAIN, 14f), Tema.TEXTO_SUAVE);
            autor.setAlignmentX(Component.LEFT_ALIGNMENT);
            cajaInfo.add(autor);
        }
    }

    private void mostrarConsecuencias(GameEngine.Efecto efecto) {
        if (efecto == null) {
            return;
        }
        cajaInfo.add(titulo("Consecuencias para " + efecto.jugador()));
        JPanel fila = filaChips();
        if (efecto.puntos() != 0) {
            fila.add(new Insignia(firmado(efecto.puntos()) + " pts", efecto.puntos() > 0 ? Tema.CIAN : Tema.MAGENTA, Iconos.Tipo.TROFEO, 12f));
        }
        if (efecto.reputacion() != 0) {
            fila.add(new Insignia(firmado(efecto.reputacion()) + " reputación", efecto.reputacion() > 0 ? Tema.VERDE : Tema.MAGENTA,
                    Iconos.Tipo.USUARIO, 12f));
        }
        cajaInfo.add(fila);
        if (!efecto.ciudad().isEmpty()) {
            cajaInfo.add(Box.createVerticalStrut(8));
            JPanel ciudad = null;
            for (Map.Entry<EstadoCiudad.Indicador, Integer> e : efecto.ciudad().entrySet()) {
                if (ciudad == null || ciudad.getComponentCount() == 3) {
                    ciudad = filaChips();
                    cajaInfo.add(ciudad);
                }
                ciudad.add(new Insignia(e.getKey().getNombre() + " " + firmado(e.getValue()),
                        EstiloJuego.colorCambio(e.getKey(), e.getValue()), null, 11f));
            }
        }
        for (String nota : efecto.notas()) {
            cajaInfo.add(Box.createVerticalStrut(8));
            JLabel etiqueta = Tema.etiqueta(Tema.html(nota, 460), Tema.texto(Font.BOLD, 14f), Tema.TEXTO);
            etiqueta.setIcon(Iconos.icono(Iconos.Tipo.INFO, 16, Tema.AMBAR));
            etiqueta.setIconTextGap(8);
            etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
            cajaInfo.add(etiqueta);
        }
    }

    private static String firmado(int valor) {
        return (valor > 0 ? "+" : "") + valor;
    }

    private JLabel titulo(String texto) {
        JLabel etiqueta = Tema.etiqueta(texto.toUpperCase(), Tema.espaciada(Tema.texto(Font.BOLD, 11.5f), 0.14f), Tema.CIAN);
        etiqueta.setBorder(new EmptyBorder(8, 0, 8, 0));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private JPanel filaChips() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        return fila;
    }

    private void generarOpciones(Set<String> opciones) {
        panelOpciones.removeAll();
        botones.clear();
        int n = 1;
        for (String opcion : opciones) {
            BotonJuego boton = new BotonJuego(opcion.toUpperCase(), EstiloJuego.iconoOpcion(opcion), BotonJuego.Estilo.PRINCIPAL)
                    .conAcento(EstiloJuego.colorOpcion(opcion));
            boton.setAtajo(String.valueOf(n++));
            Dimension d = boton.getPreferredSize();
            boton.setPreferredSize(new Dimension(Math.max(190, d.width), d.height));
            boton.addActionListener(e -> procesar(opcion));
            botones.add(boton);
            panelOpciones.add(boton);
        }
        panelOpciones.revalidate();
        panelOpciones.repaint();
    }

    private void mostrarFinal() {
        List<Jugador> ranking = new ArrayList<>(engine.getTodosLosJugadores());
        ranking.sort(Comparator.comparingInt(Jugador::getPuntuacion).reversed()
                .thenComparing(Comparator.comparingInt(Jugador::getReputacion).reversed()));
        modeloFinal.setRowCount(0);
        coloresFinal.clear();
        for (int i = 0; i < ranking.size(); i++) {
            Jugador j = ranking.get(i);
            coloresFinal.add(EstiloJuego.colorRol(j.getRol()));
            modeloFinal.addRow(new Object[]{i + 1, j.getNombre(), EstiloJuego.nombreRol(j.getRol()), j.getPuntuacion(), j.getReputacion()});
        }
        EstadoCiudad ciudad = engine.getCiudad();
        indCiudadFinal.setValor(ciudad.puntaje() + " / 100", ciudad.veredicto());
        lblVeredicto.setText(ciudad.veredicto() + ".");
        Jugador candidato = engine.getCandidato();
        if (candidato == null) {
            indEleccion.setValor("—", "No hay candidatos entre los jugadores");
        } else {
            int apoyo = engine.getApoyoCandidato(candidato);
            indEleccion.setValor(apoyo + "% de apoyo", candidato.getNombre()
                    + (apoyo >= 50 ? " gana la alcaldía de Ciudad Nova" : " pierde la elección"));
        }
        tarjetas.show(contenido, "final");
        lblPregunta.setText("¿Y ahora?");
        panelOpciones.removeAll();
        botones.clear();
        BotonJuego btnEstadisticas = new BotonJuego("VER ESTADÍSTICAS", Iconos.Tipo.ESTADISTICAS, BotonJuego.Estilo.PRINCIPAL);
        btnEstadisticas.addActionListener(e -> ventana.mostrar(VentanaJuego.ESTADISTICAS));
        BotonJuego btnArboles = new BotonJuego("VER ÁRBOLES", Iconos.Tipo.ARBOL, BotonJuego.Estilo.SECUNDARIO);
        btnArboles.addActionListener(e -> ventana.mostrar(VentanaJuego.ARBOLES));
        BotonJuego btnMenu = new BotonJuego("MENÚ PRINCIPAL", Iconos.Tipo.INICIO, BotonJuego.Estilo.SECUNDARIO);
        btnMenu.addActionListener(e -> ventana.mostrar(VentanaJuego.MENU));
        panelOpciones.add(btnEstadisticas);
        panelOpciones.add(btnArboles);
        panelOpciones.add(btnMenu);
        panelOpciones.revalidate();
        panelOpciones.repaint();
    }

    private void procesar(String opcion) {
        reloj.stop();
        engine.procesarDecision(opcion);
        actualizar();
    }

    private void reiniciarTemporizador(int segundos) {
        totalMs = segundos * 1000L;
        restanteMs = totalMs;
        anillo.setTiempo(restanteMs, totalMs);
        ultimoTick = System.currentTimeMillis();
        if (isShowing()) {
            reloj.restart();
        }
    }

    private void reanudar() {
        if (!engine.getOpcionesDisponibles().isEmpty() && restanteMs > 0) {
            ultimoTick = System.currentTimeMillis();
            reloj.start();
        }
    }

    private void tick() {
        long ahora = System.currentTimeMillis();
        restanteMs -= ahora - ultimoTick;
        ultimoTick = ahora;
        anillo.setTiempo(Math.max(0, restanteMs), totalMs);
        if (restanteMs <= 0) {
            reloj.stop();
            engine.procesarTiempoAgotado();
            actualizar();
        }
    }

    private static class AnilloTiempo extends JComponent {

        private static final int BLOQUES = 20;
        private long restante;
        private long total = 1;

        AnilloTiempo() {
            setPreferredSize(new Dimension(128, 128));
        }

        void setTiempo(long restante, long total) {
            this.restante = restante;
            this.total = Math.max(1, total);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            double fraccion = restante / (double) total;
            Color color = fraccion > 0.5 ? Tema.CIAN : fraccion > 0.25 ? Tema.AMBAR : Tema.MAGENTA;
            double cx = getWidth() / 2.0;
            double cy = getHeight() / 2.0;
            double radio = Math.min(getWidth(), getHeight()) / 2.0 - 10;
            int encendidos = (int) Math.ceil(fraccion * BLOQUES);
            for (int i = 0; i < BLOQUES; i++) {
                double angulo = -Math.PI / 2 + i * 2 * Math.PI / BLOQUES;
                int bx = (int) Math.round(cx + Math.cos(angulo) * radio) - 5;
                int by = (int) Math.round(cy + Math.sin(angulo) * radio) - 5;
                g2.setColor(Tema.SOMBRA);
                g2.fillRect(bx + 2, by + 2, 10, 10);
                g2.setColor(i < encendidos ? color : Tema.alfa(Tema.BORDE, 140));
                g2.fillRect(bx, by, 10, 10);
            }
            String numero = restante <= 0 ? "--" : String.valueOf((int) Math.ceil(restante / 1000.0));
            int escala = 4;
            Tema.textoPixel(g2, numero, (getWidth() - Tema.anchoPixel(numero, escala)) / 2,
                    (int) cy - Tema.altoPixel(escala) / 2 - 6, escala, restante <= 0 ? Tema.TEXTO_SUAVE : Tema.TEXTO, true);
            Tema.textoPixel(g2, "SEG", (getWidth() - Tema.anchoPixel("SEG", 2)) / 2, (int) cy + 18, 2, Tema.TEXTO_SUAVE, false);
            g2.dispose();
        }
    }

    private static class PanelCiudad extends JComponent {

        private static final int ALTO_FILA = 42;
        private final GameEngine engine;

        PanelCiudad(GameEngine engine) {
            this.engine = engine;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(240, EstadoCiudad.Indicador.values().length * ALTO_FILA + 30);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            int w = getWidth();
            EstadoCiudad ciudad = engine.getCiudad();
            GameEngine.Efecto efecto = engine.isEsperandoContinuar() ? engine.getUltimoEfecto() : null;
            int y = 0;
            for (EstadoCiudad.Indicador ind : EstadoCiudad.Indicador.values()) {
                int valor = ciudad.valor(ind);
                Color color = ind.isPositivo() ? EstiloJuego.colorReputacion(valor) : EstiloJuego.colorReputacion(100 - valor);
                g2.setFont(Tema.texto(Font.BOLD, 12.5f));
                g2.setColor(Tema.TEXTO);
                g2.drawString(ind.getNombre(), 2, y + 13);
                String texto = String.valueOf(valor);
                g2.setFont(Tema.texto(Font.BOLD, 14f));
                int anchoValor = g2.getFontMetrics().stringWidth(texto);
                g2.setColor(Tema.mezclar(color, java.awt.Color.BLACK, 0.15f));
                g2.drawString(texto, w - 2 - anchoValor, y + 14);
                Integer delta = efecto == null ? null : efecto.ciudad().get(ind);
                if (delta != null) {
                    String d = (delta > 0 ? "+" : "") + delta;
                    g2.setFont(Tema.texto(Font.BOLD, 12f));
                    FontMetrics fd = g2.getFontMetrics();
                    g2.setColor(EstiloJuego.colorCambio(ind, delta));
                    g2.drawString(d, w - 10 - anchoValor - fd.stringWidth(d), y + 13);
                }
                Tema.barraSegmentos(g2, 2, y + 19, w - 4, 11, valor / 100.0,
                        Tema.colorVida(ind.isPositivo() ? valor / 100.0 : 1 - valor / 100.0), 20);
                y += ALTO_FILA;
            }
            g2.setFont(Tema.texto(Font.BOLD, 11.5f));
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.drawString("PUNTAJE CIUDAD", 2, y + 16);
            String p = ciudad.puntaje() + "/100";
            Tema.textoPixel(g2, p, w - 2 - Tema.anchoPixel(p, 2), y, 2, Tema.CIAN, true);
            g2.dispose();
        }
    }

    private static class ListaJugadores extends JComponent {

        private static final int ALTO_FILA = 84;
        private static final int SEPARACION = 10;
        private final GameEngine engine;

        ListaJugadores(GameEngine engine) {
            this.engine = engine;
        }

        @Override
        public Dimension getPreferredSize() {
            int n = engine.getTodosLosJugadores().size();
            return new Dimension(230, n * (ALTO_FILA + SEPARACION));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            int w = getWidth() - 4;
            Jugador actual = engine.isPartidaTerminada() ? null : engine.getJugadorActual();
            int y = 0;
            for (Jugador j : engine.getTodosLosJugadores()) {
                Color color = EstiloJuego.colorRol(j.getRol());
                boolean turno = j == actual;
                Tema.ventana(g2, 0, y, w, ALTO_FILA - 4, turno ? color : Tema.GRIS,
                        turno ? Tema.mezclar(Tema.FONDO_2, color, 0.2f) : Tema.FONDO_2);

                Iconos.avatar(g2, j.getNombre(), 12, y + 12, 36, color);
                g2.setFont(Tema.texto(Font.BOLD, 15f));
                g2.setColor(Tema.TEXTO);
                g2.drawString(j.getNombre(), 58, y + 26);
                g2.setFont(Tema.texto(Font.BOLD, 11f));
                g2.setColor(color);
                g2.drawString(EstiloJuego.nombreRol(j.getRol()).toUpperCase(), 58, y + 42);

                String puntos = String.valueOf(j.getPuntuacion());
                Tema.textoPixel(g2, puntos, w - 14 - Tema.anchoPixel(puntos, 3), y + 6, 3, Tema.CIAN, true);
                g2.setFont(Tema.texto(Font.BOLD, 10f));
                FontMetrics fp = g2.getFontMetrics();
                g2.setColor(Tema.TEXTO_SUAVE);
                g2.drawString("PTS", w - 14 - fp.stringWidth("PTS"), y + 50);

                int rep = j.getReputacion();
                g2.setFont(Tema.texto(Font.BOLD, 10f));
                g2.setColor(Tema.AMBAR);
                g2.drawString("REP", 12, y + 66);
                Tema.barraSegmentos(g2, 38, y + 57, w - 116, 10, rep / 100.0, Tema.colorVida(rep / 100.0), 10);
                g2.setFont(Tema.texto(Font.BOLD, 11f));
                g2.setColor(EstiloJuego.colorReputacion(rep));
                g2.drawString("REP " + rep, w - 72, y + 67);

                int anchoNombre = g2.getFontMetrics(Tema.texto(Font.BOLD, 15f)).stringWidth(j.getNombre());
                String etiqueta = turno ? "TURNO" : engine.getRacha(j) >= 2 ? "RACHA x" + engine.getRacha(j) : null;
                if (etiqueta != null) {
                    g2.setFont(Tema.texto(Font.BOLD, 10.5f));
                    FontMetrics ft = g2.getFontMetrics();
                    int ancho = ft.stringWidth(etiqueta) + 12;
                    Color fondo = turno ? color : Tema.AMBAR;
                    Tema.bloque(g2, 58 + anchoNombre + 8, y + 12, ancho, 18, 2, Tema.mezclar(fondo, Color.WHITE, 0.4f), Tema.BORDE, 0);
                    g2.setColor(Tema.TEXTO);
                    g2.drawString(etiqueta, 58 + anchoNombre + 14, y + 25);
                }
                y += ALTO_FILA + SEPARACION;
            }
            g2.dispose();
        }
    }
}
