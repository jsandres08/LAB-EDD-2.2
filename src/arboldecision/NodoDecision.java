package arboldecision;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodoDecision {

    private String texto;
    private TipoNodo tipo;
    private Boolean correcto; // solo en nodos RESULTADO
    private Map<String, NodoDecision> hijos = new LinkedHashMap<>();

    public NodoDecision(String texto, TipoNodo tipo) {
        this(texto, tipo, null);
    }

    public NodoDecision(String texto, TipoNodo tipo, Boolean correcto) {
        this.texto = texto;
        this.tipo = tipo;
        this.correcto = correcto;
    }

    public String getTexto() { return texto; }
    public TipoNodo getTipo() { return tipo; }
    public Boolean getCorrecto() { return correcto; }
    public Map<String, NodoDecision> getHijos() { return hijos; }
    public void agregarHijo(String op, NodoDecision h) { hijos.put(op, h); }
    public NodoDecision obtenerHijo(String op) { return hijos.get(op); }
    public void eliminarHijo(String op) { hijos.remove(op); }
}