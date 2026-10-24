package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Expresion> ::= <ExpresionCompuesta> <RestoAsignacion> con
// <RestoAsignacion> != ϵ. Es una expresión (⚠1, §11.9): puede aparecer en
// return x = 3; o como condición.
public class NodoExpAsignacion extends NodoExpresion {
    private final NodoExpresion ladoIzq;
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpAsignacion(NodoExpresion ladoIzq, Token operador, NodoExpresion ladoDer) {
        this.ladoIzq = ladoIzq;
        this.operador = operador;
        this.ladoDer = ladoDer;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        ladoIzq.chequear(tabla);
        ladoDer.chequear(tabla);
        return null;
    }
}
