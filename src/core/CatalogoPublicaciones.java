package core;

import arbolclasificacion.ArbolClasi;
import arbolclasificacion.Publicacion;
import arboldecision.ArbolDecision;
import arboldecision.Consecuencia;
import arboldecision.NodoDecision;
import arboldecision.TipoNodo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

final class CatalogoPublicaciones {

    static final String VERIFICAR = "Verificar";
    static final String COMPARTIR = "Compartir";
    static final String REPORTAR = "Reportar";
    static final String IGNORAR = "Ignorar";

    enum TipoPublicacion {
        FALSA("Noticia falsa", false),
        RUMOR("Rumor", false),
        ATAQUE("Ataque", false),
        VERDADERA("Noticia verdadera", true),
        OPINION("Opinión", true);

        final String categoria;
        final boolean veracidad;

        TipoPublicacion(String categoria, boolean veracidad) {
            this.categoria = categoria;
            this.veracidad = veracidad;
        }
    }

    private record Entrada(int id, String texto, String autor, TipoPublicacion tipo) {
    }

    private record Resultado(String texto, boolean correcto, Consecuencia consecuencia) {
    }

    private static final List<Entrada> PUBLICACIONES = List.of(
            new Entrada(1, "El candidato Juan quiere cerrar el colegio del barrio.", "@VecinoAlerta", TipoPublicacion.RUMOR),
            new Entrada(2, "El alcalde actual está robando dinero de la ciudad.", "@CiudadIndignada", TipoPublicacion.ATAQUE),
            new Entrada(3, "Mañana cerrarán todos los parques de Ciudad Nova.", "@NovaNoticiasYa", TipoPublicacion.FALSA),
            new Entrada(4, "La Registraduría confirma que las votaciones serán el domingo de 8 a. m. a 4 p. m.",
                    "Registraduría de Ciudad Nova", TipoPublicacion.VERDADERA),
            new Entrada(5, "En mi opinión, la candidata Laura tiene las mejores propuestas de movilidad.",
                    "@LectoraCivitas", TipoPublicacion.OPINION),
            new Entrada(6, "Beber agua con cloro protege contra el virus que circula en la ciudad.",
                    "Cadena reenviada", TipoPublicacion.FALSA),
            new Entrada(7, "El candidato Andrés es un mentiroso, ¡no voten por él!", "@AntiAndres2026", TipoPublicacion.ATAQUE),
            new Entrada(8, "La alcaldía abre inscripciones para un taller gratuito de alfabetización digital.",
                    "Alcaldía de Ciudad Nova", TipoPublicacion.VERDADERA),
            new Entrada(9, "Dicen que el pasaje del bus subirá al doble después de las elecciones.",
                    "@RumoresNova", TipoPublicacion.RUMOR),
            new Entrada(10, "Me pareció que el debate de anoche fue muy aburrido.", "@PabloOpina", TipoPublicacion.OPINION));

    private CatalogoPublicaciones() {
    }

    static void cargar(ArbolDecision decision, ArbolClasi clasificacion, Random azar, int cantidad) {
        List<Entrada> elegidas = new ArrayList<>(PUBLICACIONES);
        Collections.shuffle(elegidas, azar);
        elegidas = elegidas.subList(0, Math.min(cantidad, elegidas.size()));

        String[] raiz = {};
        decision.insertar(raiz, null, new NodoDecision("Ciudad Nova · Civitas", TipoNodo.RAIZ));
        int numero = 1;
        for (Entrada e : elegidas) {
            clasificacion.insertarPublicacion(new Publicacion(e.id(), e.texto(), e.autor(), e.tipo().veracidad, e.tipo().categoria));

            String clave = "Publicación " + numero++;
            NodoDecision publicacion = new NodoDecision(e.texto(), TipoNodo.PUBLICACION);
            publicacion.setIdPublicacion(e.id());
            decision.insertar(raiz, clave, publicacion);

            Map<String, Resultado> tabla = resultados(e.tipo());
            String[] rutaPublicacion = {clave};
            decision.insertar(rutaPublicacion, VERIFICAR, new NodoDecision(
                    "Verificaste la publicación en Civitas. Con lo que descubriste, ¿qué harás ahora?", TipoNodo.PREGUNTA));
            decision.insertar(rutaPublicacion, COMPARTIR, nodo(tabla.get(COMPARTIR)));
            decision.insertar(rutaPublicacion, REPORTAR, nodo(tabla.get(REPORTAR)));
            decision.insertar(rutaPublicacion, IGNORAR, nodo(tabla.get(IGNORAR)));

            String[] rutaVerificar = {clave, VERIFICAR};
            decision.insertar(rutaVerificar, COMPARTIR, nodo(tabla.get(VERIFICAR + COMPARTIR)));
            decision.insertar(rutaVerificar, REPORTAR, nodo(tabla.get(VERIFICAR + REPORTAR)));
            decision.insertar(rutaVerificar, IGNORAR, nodo(tabla.get(VERIFICAR + IGNORAR)));
        }
    }

    private static NodoDecision nodo(Resultado r) {
        return new NodoDecision(r.texto(), TipoNodo.RESULTADO, r.correcto(), r.consecuencia());
    }

    private static Consecuencia c(int puntos, int reputacion, int verificada, int confianza, int convivencia,
            int bienestar, int desinformacion, int conflictos) {
        return new Consecuencia(puntos, reputacion, verificada, confianza, convivencia, bienestar, desinformacion, conflictos);
    }

    private static Map<String, Resultado> resultados(TipoPublicacion tipo) {
        Map<String, Resultado> t = new LinkedHashMap<>();
        switch (tipo) {
            case FALSA -> {
                t.put(COMPARTIR, new Resultado("Compartiste una noticia falsa y se propagó por Civitas.", false, c(0, -10, -5, -6, -3, -4, 10, 3)));
                t.put(REPORTAR, new Resultado("Reportaste la noticia falsa. Bien, aunque verificar primero te da más certeza.", true, c(8, 3, 2, 2, 0, 1, -5, 0)));
                t.put(IGNORAR, new Resultado("No la compartiste, pero sigue circulando sin que nadie la frene.", true, c(3, 0, 0, 0, 0, 0, 3, 0)));
                t.put(VERIFICAR + COMPARTIR, new Resultado("Sabías que era falsa y aun así la compartiste.", false, c(0, -15, -6, -8, -4, -4, 12, 4)));
                t.put(VERIFICAR + REPORTAR, new Resultado("Verificaste y reportaste la noticia falsa. ¡Civitas la retiró!", true, c(15, 8, 8, 5, 2, 3, -8, 0)));
                t.put(VERIFICAR + IGNORAR, new Resultado("Verificaste que era falsa y no la difundiste.", true, c(6, 2, 3, 1, 0, 1, -2, 0)));
            }
            case RUMOR -> {
                t.put(COMPARTIR, new Resultado("Compartiste un rumor sin confirmar y la gente empezó a discutir.", false, c(0, -8, -4, -5, -4, -3, 8, 5)));
                t.put(REPORTAR, new Resultado("Reportaste el rumor antes de que creciera.", true, c(8, 3, 2, 2, 1, 1, -4, -2)));
                t.put(IGNORAR, new Resultado("Ignoraste el rumor, pero otros siguen comentándolo.", true, c(3, 0, 0, 0, 0, 0, 3, 1)));
                t.put(VERIFICAR + COMPARTIR, new Resultado("Confirmaste que el rumor era falso y aun así lo difundiste.", false, c(0, -14, -5, -7, -5, -3, 10, 6)));
                t.put(VERIFICAR + REPORTAR, new Resultado("Verificaste que era un rumor falso y lo reportaste. ¡Rumor desmentido!", true, c(15, 8, 7, 5, 3, 2, -7, -3)));
                t.put(VERIFICAR + IGNORAR, new Resultado("Comprobaste que era un rumor y no le diste más alas.", true, c(6, 2, 3, 1, 1, 1, -2, -1)));
            }
            case ATAQUE -> {
                t.put(COMPARTIR, new Resultado("Compartiste un ataque contra un candidato y encendiste la pelea en Civitas.", false, c(0, -12, -2, -6, -8, -5, 5, 10)));
                t.put(REPORTAR, new Resultado("Reportaste el ataque personal y la conversación se calmó.", true, c(10, 5, 1, 3, 5, 3, -2, -6)));
                t.put(IGNORAR, new Resultado("No alimentaste la pelea, aunque el ataque sigue visible.", true, c(4, 1, 0, 0, 1, 1, 0, 2)));
                t.put(VERIFICAR + COMPARTIR, new Resultado("Verificaste que era un ataque sin pruebas y lo compartiste igual.", false, c(0, -15, -3, -8, -9, -5, 6, 12)));
                t.put(VERIFICAR + REPORTAR, new Resultado("Verificaste que no tenía pruebas y lo reportaste. ¡Convivencia protegida!", true, c(15, 8, 4, 5, 6, 3, -4, -8)));
                t.put(VERIFICAR + IGNORAR, new Resultado("Comprobaste que no tenía pruebas y decidiste no difundirlo.", true, c(6, 2, 2, 1, 2, 1, -1, -1)));
            }
            case VERDADERA -> {
                t.put(COMPARTIR, new Resultado("Compartiste información verdadera. ¡Buen aporte a la ciudad!", true, c(10, 5, 5, 4, 1, 2, -3, 0)));
                t.put(REPORTAR, new Resultado("Reportaste una noticia verdadera y los ciudadanos perdieron información útil.", false, c(0, -8, -3, -5, -2, -2, 3, 2)));
                t.put(IGNORAR, new Resultado("Ignoraste una noticia verdadera que habría ayudado a otros ciudadanos.", false, c(0, 0, -1, -1, 0, 0, 1, 0)));
                t.put(VERIFICAR + COMPARTIR, new Resultado("Verificaste la fuente oficial y compartiste la noticia. ¡Información confiable!", true, c(15, 8, 8, 6, 2, 3, -5, 0)));
                t.put(VERIFICAR + REPORTAR, new Resultado("Verificaste que era cierta y aun así la reportaste.", false, c(0, -12, -4, -7, -3, -2, 4, 3)));
                t.put(VERIFICAR + IGNORAR, new Resultado("Confirmaste que era cierta, pero no la compartiste con nadie.", true, c(4, 1, 2, 0, 0, 0, 0, 0)));
            }
            case OPINION -> {
                t.put(COMPARTIR, new Resultado("Compartiste una opinión dejando claro que no es un hecho.", true, c(5, 2, 0, 1, 2, 1, 0, 0)));
                t.put(REPORTAR, new Resultado("Reportaste una opinión legítima: eso es censura y genera conflicto.", false, c(0, -8, 0, -4, -6, -3, 0, 4)));
                t.put(IGNORAR, new Resultado("Respetaste la opinión y seguiste adelante.", true, c(6, 2, 0, 0, 2, 2, 0, 0)));
                t.put(VERIFICAR + COMPARTIR, new Resultado("Verificaste que es una opinión y la compartiste con respeto.", true, c(8, 3, 2, 2, 2, 1, 0, 0)));
                t.put(VERIFICAR + REPORTAR, new Resultado("Aunque verificaste que era solo una opinión, la reportaste.", false, c(0, -10, 0, -5, -7, -3, 0, 5)));
                t.put(VERIFICAR + IGNORAR, new Resultado("Entendiste que era solo una opinión, no un hecho.", true, c(8, 3, 2, 1, 2, 2, 0, 0)));
            }
        }
        return t;
    }
}
