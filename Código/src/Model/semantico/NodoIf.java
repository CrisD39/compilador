package Model.semantico;

//Podría tener un nodoIfSolo
// Y otra clase que se NodoIfDuo?

import Model.lexico.TokenType;

//por ahi si conviene diferenciar de NodoIfSolo de thenElse
public class NodoIf extends NodoSentencia{
    NodoExpresion condicion; //nodo que sea del tipo bool.
    NodoSentencia si;
    NodoSentencia sino;
    TablaSimbolos tablaSimbolos;

    public NodoIf(TablaSimbolos tablaSimbolos){
       this.tablaSimbolos = tablaSimbolos;
    }

    public void setThen(NodoSentencia si){
        this.si = si;
    }

    public void setSino(NodoSentencia sino){
        this.sino = sino;
    }

    /*
        El chequeo de un nodoIf debe controlar
        que la condición sea boolean,
            Mandar a chequaer el then y el else

         el if abre un scope?
     */
    public void chequearNodo() {
        //veo si la ocndición devuelve un boolean.
        condicion.chequear(); //Acá tendria que ver si es boolean
        //Una expresión retorna un valor?
        if(condicion.getType() == TokenType.PR_BOOLEAN) { //ver si esta bien esto
            //chequear then
            si.chequearNodo();
            //chequaer else
            sino.chequearNodo();
            //tengo que abrir un scope??
        }
    }
}
