package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroDecision {

    private final Jugador jugador;
    private final String publicacion;
    private final List<String> ruta;
    private final String resultado;
    private final Boolean correcta; // true, false, o null si pasó sin decidir

    public RegistroDecision(Jugador jugador, String publicacion, List<String> ruta,
                            String resultado, Boolean correcta) {
        this.jugador = jugador;
        this.publicacion = publicacion;
        this.ruta = new ArrayList<>(ruta);
        this.resultado = resultado;
        this.correcta = correcta;
    }

    public Jugador getJugador() { return jugador; }
    public String getPublicacion() { return publicacion; }
    public List<String> getRuta() { return Collections.unmodifiableList(ruta); }
    public String getResultado() { return resultado; }
    public Boolean getCorrecta() { return correcta; }
}