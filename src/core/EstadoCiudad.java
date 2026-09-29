package core;

import java.util.EnumMap;
import java.util.Map;

public class EstadoCiudad {

    public enum Indicador {
        VERIFICADA("Información verificada", true, 50),
        CONFIANZA("Confianza ciudadana", true, 50),
        CONVIVENCIA("Convivencia", true, 60),
        BIENESTAR("Bienestar digital", true, 60),
        DESINFORMACION("Desinformación", false, 30),
        CONFLICTOS("Conflictos", false, 20);

        private final String nombre;
        private final boolean positivo;
        private final int inicial;

        Indicador(String nombre, boolean positivo, int inicial) {
            this.nombre = nombre;
            this.positivo = positivo;
            this.inicial = inicial;
        }

        public String getNombre() {
            return nombre;
        }

        public boolean isPositivo() {
            return positivo;
        }
    }

    private final Map<Indicador, Integer> valores = new EnumMap<>(Indicador.class);

    public EstadoCiudad() {
        reiniciar();
    }

    public final void reiniciar() {
        for (Indicador i : Indicador.values()) {
            valores.put(i, i.inicial);
        }
    }

    public int valor(Indicador indicador) {
        return valores.get(indicador);
    }

    public int aplicar(Indicador indicador, int delta) {
        int anterior = valores.get(indicador);
        int nuevo = Math.max(0, Math.min(100, anterior + delta));
        valores.put(indicador, nuevo);
        return nuevo - anterior;
    }

    public int puntaje() {
        int total = 0;
        for (Indicador i : Indicador.values()) {
            int v = valores.get(i);
            total += i.positivo ? v : 100 - v;
        }
        return Math.round(total / (float) Indicador.values().length);
    }

    public String veredicto() {
        int p = puntaje();
        if (p >= 70) {
            return "Ciudad Nova es una comunidad digital saludable";
        }
        if (p >= 50) {
            return "Ciudad Nova resistió, pero la desinformación dejó huella";
        }
        return "Ciudad Nova quedó dividida por la desinformación";
    }
}
