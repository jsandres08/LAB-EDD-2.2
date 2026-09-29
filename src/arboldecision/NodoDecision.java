package arboldecision;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodoDecision {

    private String texto;
    private TipoNodo tipo;
    private Boolean correcto;
    private Consecuencia consecuencia;
    private int idPublicacion;
    private Map<String, NodoDecision> hijos;

    public NodoDecision(String texto, TipoNodo tipo) {
        this(texto, tipo, null);
    }

    public NodoDecision(String texto, TipoNodo tipo, Boolean correcto) {
        this(texto, tipo, correcto, null);
    }

    public NodoDecision(String texto, TipoNodo tipo, Boolean correcto, Consecuencia consecuencia) {
        this.texto = texto;
        this.tipo = tipo;
        this.correcto = correcto;
        this.consecuencia = consecuencia;
        this.idPublicacion = -1;
        this.hijos = new LinkedHashMap<>();
    }

    public String getTexto() {
        return texto;
    }

    public TipoNodo getTipo() {
        return tipo;
    }

    public Boolean getCorrecto() {
        return correcto;
    }

    public Consecuencia getConsecuencia() {
        return consecuencia;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public Map<String, NodoDecision> getHijos() {
        return hijos;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setTipo(TipoNodo tipo) {
        this.tipo = tipo;
    }

    public void setCorrecto(Boolean correcto) {
        this.correcto = correcto;
    }

    public void setConsecuencia(Consecuencia consecuencia) {
        this.consecuencia = consecuencia;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public void setHijos(Map<String, NodoDecision> hijos) {
        this.hijos = hijos;
    }

    public void agregarHijo(String opcion, NodoDecision hijo) {
        hijos.put(opcion, hijo);
    }

    public NodoDecision obtenerHijo(String opcion) {
        return hijos.get(opcion);
    }

    public void eliminarHijo(String opcion) {
        hijos.remove(opcion);
    }
}
