package Model.semantico.ast;

import Model.semantico.TablaSimbolos;

// <Sentencia> ::= ;
public class NodoSentenciaVacia extends NodoSentencia {
    @Override
    public void chequear(TablaSimbolos tabla) {
    }
}
