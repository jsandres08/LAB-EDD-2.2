package modelo;
import java.util.*;
public class NodoDecision{
 private String texto; private TipoNodo tipo;
 private Map<String,NodoDecision> hijos=new HashMap<>();
 public NodoDecision(String texto, TipoNodo tipo){this.texto=texto;this.tipo=tipo;}
 public String getTexto(){return texto;}
 public TipoNodo getTipo(){return tipo;}
 public Map<String,NodoDecision> getHijos(){return hijos;}
 public void agregarHijo(String op,NodoDecision h){hijos.put(op,h);}
 public NodoDecision obtenerHijo(String op){return hijos.get(op);}
 public void eliminarHijo(String op){hijos.remove(op);}
}