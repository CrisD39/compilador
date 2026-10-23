package Model.semantico;

import java.util.LinkedList;
import java.util.List;

public class NodoBloque extends NodoSentencia{

    //Atributos
    List<NodoSentencia> sentencias;
    TablaSimbolos tablaSimbolos;

    //metodos
    public NodoBloque(TablaSimbolos tablaSimbolos){
        this.tablaSimbolos = tablaSimbolos;
        sentencias = new LinkedList<NodoSentencia>();
    }

    public void agregarSentencia(NodoSentencia sentencia){
        sentencias.add(sentencia);
    }

    public void chequearNodo(){
        abrirScope();

        for(NodoSentencia sentencia: sentencias){
            sentencia.chequearNodo();
        }

        cerrarScope();
    }

    public void abrirScope(){
        tablaSimbolos.abrirScope(); //crea un nuevo scope en tabla de simbolos.
    }

    public void agregarAtributoAScope(Atributo atributo){
        tablaSimbolos.añadirAtributoScope(atributo);
    }

    //Tengo que ver si estoy cerrando bien pero se supone que al ser en pila, si.
    public void cerrarScope(){
        tablaSimbolos.cerrarScope();
    }
}
