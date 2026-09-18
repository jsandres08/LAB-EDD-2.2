package test;
import modelo.*;
public class PruebaArbolDecision{
 public static void main(String[] args){
  ArbolDecision a=new ArbolDecision();
  System.out.println("Nodo inicial: "+a.getNodoActual().getTexto());
  a.avanzar("Verificar");
  System.out.println("Nodo actual: "+a.getNodoActual().getTexto());
  a.insertar(new String[]{"Verificar"},"Denunciar",
      new NodoDecision("Denunciaste la publicación.",TipoNodo.RESULTADO));
  a.reiniciar(); a.avanzar("Verificar");
  System.out.println("Opciones: "+a.getOpcionesDisponibles());
  a.eliminar(new String[]{"Verificar","No"});
  System.out.println("Tras eliminar: "+a.getOpcionesDisponibles());
  a.reiniciar();
  System.out.println("\nDFS:");
  a.recorrerDFS();
 }
}