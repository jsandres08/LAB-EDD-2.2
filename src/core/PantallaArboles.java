package core;

import arbolclasificacion.Publicacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import ui.BarraSuperior;
import ui.BotonJuego;
import ui.FondoJuego;
import ui.Iconos;
import ui.Indicador;
import ui.Insignia;
import ui.NodoVisual;
import ui.PanelTarjeta;
import ui.TablaJuego;
import ui.Tema;
import ui.VisorArbol;

class PantallaArboles extends FondoJuego implements Pantalla {

    private enum Vista {
        DECISIONES("Decisiones", "Árbol de decisión", Iconos.Tipo.ARBOL,
                "Árbol N-ario que guía cada publicación: opciones, verificación y consecuencias."),
        CLASIFICACION("Clasificación", "Árbol de clasificación", Iconos.Tipo.CAPAS,
                "Publicaciones organizadas por veracidad y categoría. Se consulta al verificar."),
        RECORRIDO("Partida", "Recorrido de la partida", Iconos.Tipo.RELOJ,
                "Las decisiones que han tomado los jugadores hasta ahora.");

        final String pestana;
        final String titulo;
        final Iconos.Tipo icono;
        final String descripcion;

        Vista(String pestana, String titulo, Iconos.Tipo icono, String descripcion) {
            this.pestana = pestana;
            this.titulo = titulo;
            this.icono = icono;
            this.descripcion = descripcion;
        }
    }

    private enum Recorrido { PREORDEN, POSTORDEN }

    private static final String[] PREGUNTAS = {
        "¿Qué problema resuelve?", "¿Por qué un árbol?", "¿Qué variante se usa?",
        "¿Cómo se inserta y elimina?", "¿Cómo se recorre?"
    };

    private final GameEngine engine;
    private final VisorArbol visor = new VisorArbol();
    private final Map<Vista, BotonJuego> pestanas = new EnumMap<>(Vista.class);
    private final Map<Recorrido, BotonJuego> botonesRecorrido = new EnumMap<>(Recorrido.class);
    private final BarraSuperior barra;
    private final PanelTarjeta tarjetaArbol;
    private final JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    private final Indicador indNodos = new Indicador("Nodos", Iconos.Tipo.ARBOL, Tema.CIAN, false);
    private final Indicador indHojas = new Indicador("Hojas", Iconos.Tipo.CAPAS, Tema.VERDE, false);
    private final Indicador indAltura = new Indicador("Altura", Iconos.Tipo.ESTADISTICAS, Tema.AMBAR, false);
    private final Indicador indGrado = new Indicador("Grado", Iconos.Tipo.COMPARTIR, Tema.VIOLETA, false);
    private final JPanel detalle = new PanelDetalle();
    private final JLabel lblMetricas = Tema.etiqueta("", Tema.texto(Font.PLAIN, 12f), Tema.TEXTO_SUAVE);
    private final JTextField txtBuscar = new JTextField(12);
    private final BotonJuego btnBuscar = new BotonJuego("BUSCAR", Iconos.Tipo.BUSCAR, BotonJuego.Estilo.SECUNDARIO);
    private final BotonJuego btnSubarbol = new BotonJuego("VER SUBÁRBOL", Iconos.Tipo.CAPAS, BotonJuego.Estilo.SECUNDARIO);
    private Vista vista = Vista.DECISIONES;
    private Recorrido recorrido;
    private List<NodoVisual> ordenActual = List.of();
    private List<String> resultadosBusqueda;

    PantallaArboles(VentanaJuego ventana, GameEngine engine) {
        super(new BorderLayout(0, 14));
        this.engine = engine;
        setBorder(new EmptyBorder(18, 22, 18, 22));

        BotonJuego btnVolver = new BotonJuego("VOLVER", Iconos.Tipo.VOLVER, BotonJuego.Estilo.VOLVER);
        btnVolver.addActionListener(e -> ventana.volver());
        barra = new BarraSuperior(btnVolver, Iconos.Tipo.ARBOL, Tema.VERDE, "Visualizador de árboles", "");
        for (Vista v : Vista.values()) {
            BotonJuego pestana = new BotonJuego(v.pestana.toUpperCase(), v.icono, BotonJuego.Estilo.SECUNDARIO);
            pestana.addActionListener(e -> cargar(v));
            pestanas.put(v, pestana);
            barra.agregar(pestana);
        }
        add(barra, BorderLayout.NORTH);

        tarjetaArbol = new PanelTarjeta(new BorderLayout(0, 10)).conTitulo("Árbol", Iconos.Tipo.ARBOL).conAcento(Tema.VERDE);
        tarjetaArbol.add(visor, BorderLayout.CENTER);
        leyenda.setOpaque(false);
        tarjetaArbol.add(leyenda, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setOpaque(false);
        centro.add(tarjetaArbol, BorderLayout.CENTER);
        centro.add(crearPanelInformacion(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        add(crearHerramientas(), BorderLayout.SOUTH);

        visor.setAlSeleccionar(this::mostrarNodo);
    }

    private JComponent crearPanelInformacion() {
        PanelTarjeta tarjeta = new PanelTarjeta(new BorderLayout(0, 12)).conTitulo("Información", Iconos.Tipo.INFO)
                .conAcento(Tema.VIOLETA);
        tarjeta.setPreferredSize(new Dimension(350, 0));
        JPanel metricas = new JPanel(new GridLayout(2, 2, 10, 10));
        metricas.setOpaque(false);
        metricas.add(indNodos);
        metricas.add(indHojas);
        metricas.add(indAltura);
        metricas.add(indGrado);
        JPanel arriba = new JPanel(new BorderLayout(0, 6));
        arriba.setOpaque(false);
        arriba.add(metricas, BorderLayout.CENTER);
        arriba.add(lblMetricas, BorderLayout.SOUTH);
        tarjeta.add(arriba, BorderLayout.NORTH);

        detalle.setOpaque(false);
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(detalle);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TablaJuego.estilizarScroll(scroll);
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private JComponent crearHerramientas() {
        JPanel filas = new JPanel(new GridLayout(2, 1, 0, 8));
        filas.setOpaque(false);

        PanelTarjeta vistaFila = new PanelTarjeta(new BorderLayout(16, 0)).sinEsquinas();
        vistaFila.setBorder(new EmptyBorder(6, 12, 6, 16));
        JPanel izquierda = fila(FlowLayout.LEFT);
        izquierda.add(etiqueta("VISTA"));
        izquierda.add(herramienta("ZOOM +", Iconos.Tipo.ZOOM_MAS, visor::acercar));
        izquierda.add(herramienta("ZOOM −", Iconos.Tipo.ZOOM_MENOS, visor::alejar));
        izquierda.add(herramienta("CENTRAR", Iconos.Tipo.CENTRAR, visor::encuadrar));
        izquierda.add(herramienta("REINICIAR", Iconos.Tipo.REINICIAR, this::reiniciarVista));
        btnSubarbol.conAcento(Tema.VERDE);
        btnSubarbol.addActionListener(e -> alternarSubarbol());
        izquierda.add(btnSubarbol);
        vistaFila.add(izquierda, BorderLayout.WEST);

        PanelTarjeta operaciones = new PanelTarjeta(new BorderLayout(16, 0)).sinEsquinas().conAcento(Tema.AMBAR);
        operaciones.setBorder(new EmptyBorder(6, 12, 6, 16));
        JPanel recorridos = fila(FlowLayout.LEFT);
        recorridos.add(etiqueta("RECORRIDOS"));
        recorridos.add(botonRecorrido(Recorrido.PREORDEN, "PREORDEN"));
        recorridos.add(botonRecorrido(Recorrido.POSTORDEN, "POSTORDEN"));
        operaciones.add(recorridos, BorderLayout.WEST);
        JPanel busqueda = fila(FlowLayout.RIGHT);
        busqueda.add(etiqueta("BUSCAR POR CATEGORÍA"));
        txtBuscar.setFont(Tema.texto(Font.PLAIN, 14f));
        txtBuscar.setForeground(Tema.TEXTO);
        txtBuscar.setCaretColor(Tema.CIAN);
        txtBuscar.setBackground(Tema.FONDO_2);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Tema.BORDE, 3),
                new EmptyBorder(8, 10, 8, 10)));
        txtBuscar.setToolTipText("Ejemplo: Rumor, Opinión, Noticia falsa");
        txtBuscar.addActionListener(e -> buscar());
        btnBuscar.conAcento(Tema.AMBAR);
        btnBuscar.addActionListener(e -> buscar());
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        operaciones.add(busqueda, BorderLayout.EAST);

        filas.add(vistaFila);
        filas.add(operaciones);
        return filas;
    }

    private static JPanel fila(int alineacion) {
        JPanel p = new JPanel(new FlowLayout(alineacion, 8, 0));
        p.setOpaque(false);
        return p;
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = Tema.etiqueta(texto, Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.15f), Tema.TEXTO_SUAVE);
        l.setBorder(new EmptyBorder(0, 0, 0, 6));
        return l;
    }

    private BotonJuego herramienta(String texto, Iconos.Tipo icono, Runnable accion) {
        BotonJuego boton = new BotonJuego(texto, icono, BotonJuego.Estilo.SECUNDARIO).conAcento(Tema.VERDE);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private BotonJuego botonRecorrido(Recorrido r, String texto) {
        BotonJuego boton = new BotonJuego(texto, Iconos.Tipo.SIGUIENTE, BotonJuego.Estilo.SECUNDARIO).conAcento(Tema.AMBAR);
        boton.addActionListener(e -> aplicarRecorrido(recorrido == r ? null : r));
        botonesRecorrido.put(r, boton);
        return boton;
    }

    @Override
    public void alMostrar() {
        cargar(vista);
    }

    private void cargar(Vista nueva) {
        vista = nueva;
        recorrido = null;
        ordenActual = List.of();
        resultadosBusqueda = null;
        pestanas.forEach((v, b) -> b.setActivo(v == nueva));
        botonesRecorrido.forEach((r, b) -> b.setActivo(false));
        barra.setSubtitulo(nueva.descripcion);
        tarjetaArbol.setTitulo(nueva.titulo);
        boolean clasificacion = nueva == Vista.CLASIFICACION;
        txtBuscar.setEnabled(clasificacion);
        btnBuscar.setEnabled(clasificacion);

        NodoVisual raiz;
        ModeloArboles.Metricas metricas;
        String origen;
        switch (nueva) {
            case DECISIONES -> {
                raiz = ModeloArboles.deDecisiones(engine.getArbolDecision());
                metricas = raiz == null ? null : ModeloArboles.metricasDecisiones(engine.getArbolDecision());
                origen = "Calculado con ArbolDecision: peso(), hojas(), altura() y grado(). Los niveles empiezan en 0.";
            }
            case CLASIFICACION -> {
                raiz = ModeloArboles.deClasificacion(engine.getArbolClasificacion());
                metricas = raiz == null ? null : ModeloArboles.metricasClasificacion(engine.getArbolClasificacion());
                origen = "Nodos Clasificacion de ArbolClasi (las publicaciones se guardan dentro de cada nodo). Los niveles empiezan en 0.";
            }
            default -> {
                raiz = ModeloArboles.deRecorrido(engine.getTodosLosJugadores(), engine.getHistorialConEnCurso());
                metricas = ModeloArboles.metricas(raiz);
                origen = "Construido a partir del historial de la partida.";
            }
        }
        visor.setMensajeVacio(clasificacion ? "No hay publicaciones clasificadas" : "El árbol está vacío");
        visor.setRaiz(raiz, 0);
        indNodos.setValor(metricas == null ? "0" : String.valueOf(metricas.nodos()));
        indHojas.setValor(metricas == null ? "0" : String.valueOf(metricas.hojas()));
        indAltura.setValor(metricas == null ? "0" : String.valueOf(metricas.altura()));
        indGrado.setValor(metricas == null ? "0" : String.valueOf(metricas.grado()));
        lblMetricas.setText(Tema.html(origen, 215));
        actualizarLeyenda(nueva);
        actualizarSubarbol();
        mostrarNodo(null);
    }

    private void reiniciarVista() {
        visor.reiniciar();
        recorrido = null;
        ordenActual = List.of();
        resultadosBusqueda = null;
        botonesRecorrido.forEach((r, b) -> b.setActivo(false));
        actualizarSubarbol();
        mostrarNodo(null);
    }

    private void alternarSubarbol() {
        if (visor.isSubarbol()) {
            visor.mostrarTodo();
        } else if (visor.getSeleccionado() != null) {
            visor.mostrarSubarbol(visor.getSeleccionado());
        }
        actualizarSubarbol();
    }

    private void actualizarSubarbol() {
        btnSubarbol.setText(visor.isSubarbol() ? "VER TODO" : "VER SUBÁRBOL");
        btnSubarbol.setEnabled(visor.isSubarbol() || visor.getSeleccionado() != null);
        btnSubarbol.revalidate();
    }

    private void aplicarRecorrido(Recorrido r) {
        recorrido = r;
        botonesRecorrido.forEach((k, b) -> b.setActivo(k == r));
        NodoVisual raiz = visor.getRaiz();
        if (r == null || raiz == null) {
            ordenActual = List.of();
            visor.setOrden(null);
        } else {
            List<?> lista = switch (vista) {
                case DECISIONES -> switch (r) {
                    case PREORDEN -> engine.getArbolDecision().preorden();
                    case POSTORDEN -> engine.getArbolDecision().postorden();
                };
                case CLASIFICACION -> switch (r) {
                    case PREORDEN -> engine.getArbolClasificacion().preorden();
                    case POSTORDEN -> engine.getArbolClasificacion().postorden();
                };
                case RECORRIDO -> switch (r) {
                    case PREORDEN -> ModeloArboles.preorden(raiz);
                    case POSTORDEN -> ModeloArboles.postorden(raiz);
                };
            };
            Map<NodoVisual, Integer> orden = ModeloArboles.numerar(raiz, lista);
            NodoVisual[] secuencia = new NodoVisual[orden.size()];
            orden.forEach((nodo, paso) -> secuencia[paso - 1] = nodo);
            ordenActual = List.of(secuencia);
            visor.setOrden(orden);
        }
        if (visor.getSeleccionado() == null) {
            mostrarNodo(null);
        }
    }

    private void buscar() {
        NodoVisual raiz = visor.getRaiz();
        String texto = txtBuscar.getText().trim();
        if (raiz == null || texto.isEmpty()) {
            resultadosBusqueda = null;
            visor.setResaltados(Collections.emptySet());
            mostrarNodo(visor.getSeleccionado());
            return;
        }
        List<Publicacion> encontradas = engine.getArbolClasificacion().buscarPorCategoria(texto);
        Set<Object> objetivo = Collections.newSetFromMap(new IdentityHashMap<>());
        objetivo.addAll(encontradas);
        Set<NodoVisual> nodos = new HashSet<>();
        marcar(raiz, objetivo, nodos);
        visor.setResaltados(nodos);
        resultadosBusqueda = encontradas.stream().map(p -> "#" + p.getId() + " " + p.getTexto()).toList();
        if (visor.getSeleccionado() == null) {
            mostrarNodo(null);
        }
    }

    private void marcar(NodoVisual nodo, Set<Object> objetivo, Set<NodoVisual> nodos) {
        if (nodo.getOrigen() != null && objetivo.contains(nodo.getOrigen())) {
            nodos.add(nodo);
        }
        for (NodoVisual h : nodo.getHijos()) {
            marcar(h, objetivo, nodos);
        }
    }

    private void actualizarLeyenda(Vista v) {
        leyenda.removeAll();
        switch (v) {
            case DECISIONES -> {
                leyenda.add(new ChipLeyenda("Raíz", Tema.VIOLETA));
                leyenda.add(new ChipLeyenda("Publicación", Tema.CIAN));
                leyenda.add(new ChipLeyenda("Verificación", Tema.AMBAR));
                leyenda.add(new ChipLeyenda("Decisión responsable", Tema.VERDE));
                leyenda.add(new ChipLeyenda("Decisión irresponsable", Tema.MAGENTA));
            }
            case CLASIFICACION -> {
                leyenda.add(new ChipLeyenda("Raíz", Tema.VIOLETA));
                leyenda.add(new ChipLeyenda("Verdadera", Tema.VERDE));
                leyenda.add(new ChipLeyenda("Falsa", Tema.MAGENTA));
                leyenda.add(new ChipLeyenda("Categoría", Tema.AZUL));
                leyenda.add(new ChipLeyenda("Publicación", Tema.CIAN));
            }
            default -> {
                for (Jugador j : engine.getTodosLosJugadores()) {
                    leyenda.add(new ChipLeyenda(j.getNombre(), EstiloJuego.colorRol(j.getRol())));
                }
                leyenda.add(new ChipLeyenda("Decisión", Tema.AZUL));
                leyenda.add(new ChipLeyenda("Acierto", Tema.VERDE));
                leyenda.add(new ChipLeyenda("Fallo", Tema.MAGENTA));
                leyenda.add(new ChipLeyenda("En curso", Tema.AMBAR));
            }
        }
        leyenda.revalidate();
        leyenda.repaint();
    }

    private void mostrarNodo(NodoVisual nodo) {
        actualizarSubarbol();
        detalle.removeAll();
        if (nodo == null) {
            mostrarResumen();
        } else {
            Insignia categoria = new Insignia(nodo.getCategoria(), nodo.getColor(), null, 11f);
            categoria.setAlignmentX(Component.LEFT_ALIGNMENT);
            detalle.add(categoria);
            detalle.add(Box.createVerticalStrut(8));
            detalle.add(texto(nodo.getTitulo(), Tema.TEXTO, Font.BOLD, 17f));
            detalle.add(Box.createVerticalStrut(12));
            dato("Nivel", String.valueOf(nodo.getNivel()));
            if (recorrido != null && ordenActual.contains(nodo)) {
                dato("Paso en el " + nombre(recorrido), (ordenActual.indexOf(nodo) + 1) + " de " + ordenActual.size());
            }
            if (nodo.getEtiquetaArista() != null) {
                dato("Se llega eligiendo", nodo.getEtiquetaArista());
            }
            for (Map.Entry<String, String> e : nodo.getInfo().entrySet()) {
                dato(e.getKey(), e.getValue());
            }
            seccion("Padre");
            if (nodo.getPadre() == null) {
                detalle.add(texto("Ninguno (es la raíz)", Tema.TEXTO_SUAVE, Font.PLAIN, 13f));
            } else {
                detalle.add(enlace(nodo.getPadre(), Iconos.Tipo.VOLVER));
            }
            seccion("Hijos (" + nodo.getHijos().size() + ")");
            if (nodo.getHijos().isEmpty()) {
                detalle.add(texto("Ninguno (es una hoja)", Tema.TEXTO_SUAVE, Font.PLAIN, 13f));
            }
            for (NodoVisual hijo : nodo.getHijos()) {
                detalle.add(enlace(hijo, Iconos.Tipo.SIGUIENTE));
                detalle.add(Box.createVerticalStrut(6));
            }
        }
        detalle.revalidate();
        detalle.repaint();
    }

    private void mostrarResumen() {
        if (resultadosBusqueda != null) {
            seccion("Resultado de buscarPorCategoria (" + resultadosBusqueda.size() + ")");
            if (resultadosBusqueda.isEmpty()) {
                detalle.add(texto("No hay publicaciones en la categoría \"" + txtBuscar.getText().trim() + "\".",
                        Tema.TEXTO_SUAVE, Font.PLAIN, 13f));
            }
            for (String r : resultadosBusqueda) {
                detalle.add(texto("• " + r, Tema.TEXTO, Font.PLAIN, 13f));
            }
        }
        if (recorrido != null) {
            seccion("Orden del " + nombre(recorrido) + " (" + ordenActual.size() + " nodos)");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ordenActual.size(); i++) {
                String titulo = ordenActual.get(i).getTitulo();
                sb.append(i + 1).append(". ").append(titulo.length() > 34 ? titulo.substring(0, 33) + "…" : titulo).append("\n");
            }
            detalle.add(texto(sb.toString().trim(), Tema.TEXTO, Font.PLAIN, 13f));
        }
        seccion("¿Por qué este árbol?");
        String[] respuestas = justificacion(vista);
        for (int i = 0; i < PREGUNTAS.length; i++) {
            dato(PREGUNTAS[i], respuestas[i]);
        }
        detalle.add(texto("Selecciona un nodo para ver su información.", Tema.TEXTO_SUAVE, Font.ITALIC, 13f));
    }

    private static String nombre(Recorrido r) {
        return switch (r) {
            case PREORDEN -> "preorden";
            case POSTORDEN -> "postorden";
        };
    }

    private static String[] justificacion(Vista v) {
        return switch (v) {
            case DECISIONES -> new String[]{
                "Representa todas las decisiones posibles ante cada publicación y sus consecuencias. "
                + "El juego baja por el árbol desde la publicación hasta un resultado según lo que elige el jugador.",
                "Cada decisión abre caminos distintos que no se cruzan: es una jerarquía padre → opciones → resultado. "
                + "Avanzar a la siguiente opción es inmediato (un acceso al mapa de hijos).",
                "Árbol N-ario (general). Cada nodo guarda sus hijos en un LinkedHashMap<opción, nodo> que conserva el orden. "
                + "Tipos de nodo: RAIZ, PUBLICACION, PREGUNTA y RESULTADO (con su Consecuencia).",
                "insertar(ruta, opción, nodo) baja recursivamente siguiendo la ruta y agrega el hijo; si el árbol está vacío, "
                + "el nodo se convierte en la raíz. eliminar(ruta) baja hasta el padre y quita la rama completa.",
                "En el juego se desciende por un camino raíz → hoja. Además tiene recorridos en preorden y postorden. "
                + "buscarPorTipo usa preorden para obtener las publicaciones en orden."};
            case CLASIFICACION -> new String[]{
                "Organiza las publicaciones por veracidad y categoría. Cuando un jugador pulsa Verificar, el juego busca la "
                + "publicación en este árbol y le muestra en qué rama está clasificada.",
                "La clasificación es jerárquica (Todas → Verdadera/Falsa → Categoría). Un árbol permite agrupar, contar y "
                + "buscar publicaciones por rama.",
                "Árbol N-ario de categorías: cada nodo Clasificacion tiene una lista de hijos y una lista de publicaciones.",
                "insertarPublicacion crea la raíz si no existe y baja recursivamente dos niveles creando las ramas que falten. "
                + "eliminarPublicacion(id) la busca recursivamente y la quita de la lista de su nodo.",
                "verificar(id) y buscarPorId hacen un DFS en preorden; buscarPorCategoria recorre todo el árbol. "
                + "También tiene postorden."};
            case RECORRIDO -> new String[]{
                "Muestra la historia de la partida: qué publicaciones recibió cada jugador y qué camino eligió en cada una.",
                "Es una jerarquía natural: la partida tiene jugadores, cada jugador tiene sus jugadas y cada jugada sus decisiones.",
                "Árbol N-ario: partida (nivel 0), jugadores (1), jugadas (2), decisiones (3, y 4 si verificó) "
                + "y resultado (4, o 5 si verificó).",
                "Cada jugador es un hijo de la raíz. Cada jugada registrada se inserta como hijo de su jugador. No se eliminan jugadas.",
                "Se dibuja en preorden. Con los botones puedes ver también postorden."};
        };
    }

    private BotonJuego enlace(NodoVisual destino, Iconos.Tipo icono) {
        String etiqueta = destino.getEtiquetaArista() != null ? destino.getEtiquetaArista() + ": " : "";
        String texto = etiqueta + destino.getTitulo();
        if (texto.length() > 20) {
            texto = texto.substring(0, 19) + "…";
        }
        BotonJuego boton = new BotonJuego(texto, icono, BotonJuego.Estilo.SECUNDARIO).conAcento(destino.getColor());
        boton.setToolTipText(destino.getTitulo());
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(300, 46));
        boton.addActionListener(e -> visor.seleccionarYEnfocar(destino));
        return boton;
    }

    private void seccion(String texto) {
        detalle.add(Box.createVerticalStrut(10));
        JLabel etiqueta = Tema.etiqueta(Tema.html(texto.toUpperCase(), 215),
                Tema.espaciada(Tema.texto(Font.BOLD, 11f), 0.15f), Tema.CIAN);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        detalle.add(etiqueta);
        detalle.add(Box.createVerticalStrut(6));
    }

    private void dato(String clave, String valor) {
        JLabel etiqueta = Tema.etiqueta(Tema.html(clave.toUpperCase(), 215),
                Tema.espaciada(Tema.texto(Font.BOLD, 10.5f), 0.12f), Tema.TEXTO_SUAVE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        detalle.add(etiqueta);
        detalle.add(Box.createVerticalStrut(2));
        detalle.add(texto(valor, Tema.TEXTO, Font.PLAIN, 14f));
        detalle.add(Box.createVerticalStrut(10));
    }

    private JLabel texto(String contenido, Color color, int estilo, float tam) {
        JLabel etiqueta = Tema.etiqueta(Tema.html(contenido, 215), Tema.texto(estilo, tam), color);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private static class PanelDetalle extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle r, int orientacion, int direccion) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle r, int orientacion, int direccion) {
            return r.height - 30;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static class ChipLeyenda extends JComponent {

        private final String texto;
        private final Color color;
        private final Font fuente = Tema.texto(Font.BOLD, 12f);

        ChipLeyenda(String texto, Color color) {
            this.texto = texto;
            this.color = color;
            setPreferredSize(new Dimension(getFontMetrics(fuente).stringWidth(texto) + 24, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.preparar(g);
            g2.setColor(Tema.SOMBRA);
            g2.fillRect(4, getHeight() / 2 - 4, 12, 12);
            g2.setColor(color);
            g2.fillRect(2, getHeight() / 2 - 6, 12, 12);
            g2.setFont(fuente);
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.drawString(texto, 22, getHeight() / 2 + 5);
            g2.dispose();
        }
    }
}
