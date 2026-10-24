package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ReferenciaResto> ::= [ <Expresion> ] <ReferenciaResto> (⚠2). El "[" es el
// lexema de ERR_INDICE_NO_ENTERO y ERR_NO_ES_ARREGLO.
public class NodoAccesoArregloEncadenado extends NodoEncadenado {
    private final Token corchete;
    private final NodoExpresion indice;

    public NodoAccesoArregloEncadenado(Token corchete, NodoExpresion indice, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.corchete = corchete;
        this.indice = indice;
    }

    @Override
    protected boolean esAsignableSinSiguiente() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        indice.chequear(tabla);
        // Pendiente (spec 0003, ticket 05): recorre a sus hijos y al resto de
        // la cadena, así un error adentro (o un nodo de logro) igual se
        // reporta, y corta en vez de devolver un tipo que todavía no calcula.
        chequearSiguiente(tabla, null);
        throw ExcepcionSemantica.noSoportado(corchete);
    }
}
