package modelo;
import java.util.Set;
public class ArbolDecision{
 private NodoDecision raiz,nodoActual;
 public ArbolDecision(){cargar(); nodoActual=raiz;}
 public NodoDecision getNodoActual(){return nodoActual;}
 public void reiniciar(){nodoActual=raiz;}
 public void avanzar(String op){ if(nodoActual.obtenerHijo(op)!=null) nodoActual=nodoActual.obtenerHijo(op);}
 public Set<String> getOpcionesDisponibles(){return nodoActual.getHijos().keySet();}
 public void insertar(String[] ruta,String opcion,NodoDecision nuevo){
   NodoDecision a=raiz;
   for(String r:ruta){ a=a.obtenerHijo(r); if(a==null)return; }
   a.agregarHijo(opcion,nuevo);
 }
 public void eliminar(String[] ruta){
   NodoDecision a=raiz;
   for(int i=0;i<ruta.length-1;i++){ a=a.obtenerHijo(ruta[i]); if(a==null)return; }
   a.eliminarHijo(ruta[ruta.length-1]);
 }
 public void recorrerDFS(){dfs(raiz,"");}
 private void dfs(NodoDecision n,String s){
   if(n==null)return;
   System.out.println(s+"- "+n.getTexto()+" ["+n.getTipo()+"]");
   for(String op:n.getHijos().keySet()){
      System.out.println(s+"  Opcion: "+op);
      dfs(n.obtenerHijo(op),s+"    ");
   }
 }
 private void cargar(){
   raiz=new NodoDecision("PUBLICACION 1: Beber cloro elimina virus.",TipoNodo.PUBLICACION);
   NodoDecision verificar=new NodoDecision("¿Deseas verificar la información?",TipoNodo.PREGUNTA);
   raiz.agregarHijo("Verificar",verificar);
   raiz.agregarHijo("Compartir",new NodoDecision("Compartiste información falsa.",TipoNodo.RESULTADO));
   raiz.agregarHijo("Ignorar",new NodoDecision("Ignoraste la publicación.",TipoNodo.RESULTADO));
   verificar.agregarHijo("Sí",new NodoDecision("Correcto: era falsa.",TipoNodo.RESULTADO));
   verificar.agregarHijo("No",new NodoDecision("Caíste en la desinformación.",TipoNodo.RESULTADO));
   NodoDecision p2=new NodoDecision("PUBLICACION 2: Suspenden clases mañana.",TipoNodo.PUBLICACION);
   raiz.agregarHijo("Siguiente publicación",p2);
   NodoDecision fuente=new NodoDecision("¿Consultar fuente oficial?",TipoNodo.PREGUNTA);
   p2.agregarHijo("Verificar",fuente);
   p2.agregarHijo("Compartir",new NodoDecision("Generaste pánico.",TipoNodo.RESULTADO));
   fuente.agregarHijo("Sí",new NodoDecision("Era un rumor.",TipoNodo.RESULTADO));
   fuente.agregarHijo("No",new NodoDecision("Creíste el rumor.",TipoNodo.RESULTADO));
   NodoDecision p3=new NodoDecision("PUBLICACION 3: Nueva vacuna aprobada.",TipoNodo.PUBLICACION);
   p2.agregarHijo("Siguiente publicación",p3);
   NodoDecision oficial=new NodoDecision("¿Consultar página oficial?",TipoNodo.PREGUNTA);
   p3.agregarHijo("Verificar",oficial);
   p3.agregarHijo("Compartir",new NodoDecision("Compartiste una noticia verdadera.",TipoNodo.RESULTADO));
   oficial.agregarHijo("Sí",new NodoDecision("Confirmaste la noticia.",TipoNodo.RESULTADO));
   oficial.agregarHijo("No",new NodoDecision("No verificaste la fuente.",TipoNodo.RESULTADO));
   NodoDecision p4=new NodoDecision("PUBLICACION 4: Murió un famoso.",TipoNodo.PUBLICACION);
   p3.agregarHijo("Siguiente publicación",p4);
   NodoDecision noticias=new NodoDecision("¿Buscar noticias confiables?",TipoNodo.PREGUNTA);
   p4.agregarHijo("Verificar",noticias);
   p4.agregarHijo("Compartir",new NodoDecision("Era una fake news.",TipoNodo.RESULTADO));
   noticias.agregarHijo("Sí",new NodoDecision("Descubriste el montaje.",TipoNodo.RESULTADO));
   noticias.agregarHijo("No",new NodoDecision("Caíste en la fake news.",TipoNodo.RESULTADO));
 }
}