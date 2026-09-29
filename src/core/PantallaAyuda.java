package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import ui.BarraSuperior;
import ui.BotonJuego;
import ui.FondoJuego;
import ui.Iconos;
import ui.PanelTarjeta;
import ui.TablaJuego;
import ui.Tema;

class PantallaAyuda extends FondoJuego implements Pantalla {

    PantallaAyuda(VentanaJuego ventana) {
        super(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(18, 22, 18, 22));

        BotonJuego btnVolver = new BotonJuego("VOLVER", Iconos.Tipo.VOLVER, BotonJuego.Estilo.VOLVER);
        btnVolver.addActionListener(e -> ventana.volver());
        add(new BarraSuperior(btnVolver, Iconos.Tipo.AYUDA, Tema.VIOLETA, "Ayuda",
                "Todo lo que necesitas saber para proteger Ciudad Nova"), BorderLayout.NORTH);

        JPanel columnas = new JPanel(new GridLayout(1, 3, 14, 0));
        columnas.setOpaque(false);

        JPanel c1 = columna();
        c1.add(tarjeta("Objetivo", Iconos.Tipo.TROFEO, Tema.AMBAR,
                "Ciudad Nova elige alcalde y en la red social Civitas circulan noticias verdaderas, rumores, opiniones y ataques. "
                + "Tomen decisiones responsables para que la ciudad termine con mucha información verificada, confianza, "
                + "convivencia y bienestar, y con poca desinformación y pocos conflictos."));
        c1.add(tarjeta("Reglas", Iconos.Tipo.INFO, Tema.CIAN,
                "• Juegan de 2 a 4 personas por turnos. Cada turno llega una publicación.\n"
                + "• Tienes 10 segundos para decidir (6 si la publicación es viral).\n"
                + "• Si el tiempo se acaba pierdes 5 de reputación y la desinformación sube.\n"
                + "• Verificar da +5 pts y revela dónde está clasificada la publicación antes de decidir.\n"
                + "• Encadenar decisiones responsables activa una racha con puntos extra."));
        columnas.add(c1);

        JPanel c2 = columna();
        c2.add(tarjeta("Botones", Iconos.Tipo.JUGAR, Tema.VERDE,
                "• VERIFICAR (1): investiga la publicación antes de actuar.\n"
                + "• COMPARTIR: la difunde en Civitas. Bien con información verdadera, grave con información falsa.\n"
                + "• REPORTAR: la denuncia. Ideal para noticias falsas, rumores y ataques; es censura si es una opinión.\n"
                + "• IGNORAR: no hace nada. Evita daños, pero la publicación sigue circulando.\n"
                + "• Las teclas 1-9 activan las opciones. ESC vuelve atrás."));
        c2.add(tarjeta("Personajes", Iconos.Tipo.USUARIO, Tema.MAGENTA,
                "• Ciudadano: duplica su efecto en convivencia y bienestar digital.\n"
                + "• Periodista: duplica la información verificada y gana +5 pts extra al verificar.\n"
                + "• Influencer: todas sus decisiones tienen el doble de impacto en la ciudad.\n"
                + "• Candidato: duplica su efecto en la confianza ciudadana y compite en la elección final."));
        columnas.add(c2);

        JPanel c3 = columna();
        c3.add(tarjeta("Indicadores", Iconos.Tipo.CIUDAD, Tema.VIOLETA,
                "• Información verificada, Confianza ciudadana, Convivencia y Bienestar digital: mientras más altos, mejor.\n"
                + "• Desinformación y Conflictos: mientras más bajos, mejor.\n"
                + "• El puntaje de la ciudad es el promedio de los seis (los negativos se invierten)."));
        c3.add(tarjeta("Cómo ganar", Iconos.Tipo.ESTADISTICAS, Tema.AMBAR,
                "• Individual: gana quien tenga más puntos (en empate, más reputación).\n"
                + "• Ciudad: 70 o más es una comunidad digital saludable.\n"
                + "• Elección: el Candidato gana con 50% o más de apoyo, que depende de la confianza, la verificación, "
                + "la convivencia, los conflictos y su reputación."));
        columnas.add(c3);

        JScrollPane scroll = new JScrollPane(columnas);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        TablaJuego.estilizarScroll(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    private static JPanel columna() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private static JPanel tarjeta(String titulo, Iconos.Tipo icono, Color color, String texto) {
        PanelTarjeta t = new PanelTarjeta(new BorderLayout()).conTitulo(titulo, icono).conAcento(color);
        JLabel contenido = Tema.etiqueta(Tema.html(texto, 270), Tema.texto(Font.PLAIN, 14.5f), Tema.TEXTO);
        contenido.setVerticalAlignment(JLabel.TOP);
        t.add(contenido, BorderLayout.CENTER);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.setBorder(new EmptyBorder(0, 0, 14, 0));
        envoltorio.add(t, BorderLayout.CENTER);
        envoltorio.setMaximumSize(new Dimension(Integer.MAX_VALUE, envoltorio.getPreferredSize().height));
        envoltorio.add(Box.createVerticalStrut(0), BorderLayout.SOUTH);
        return envoltorio;
    }

    @Override
    public void alMostrar() {
    }
}
