package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <TernarioOpcional> ::= ? <ExpresionCompuesta> : <ExpresionCompuesta>.
// ladoIzq es la rama antes de ":" y ladoDer la de después. Logro Ternarios:
// todavía no soportado, se reporta sobre "?" (D11).
public class NodoExpTernaria extends NodoExpCompuesta {
    private final NodoExpresion condicion;
    private final Token operador;
    private final NodoExpresion ladoIzq;
    private final NodoExpresion ladoDer;

    public NodoExpTernaria(NodoExpresion condicion, Token operador, NodoExpresion ladoIzq, NodoExpresion ladoDer) {
        this.condicion = condicion;
        this.operador = operador;
        this.ladoIzq = ladoIzq;
        this.ladoDer = ladoDer;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(operador);
    }
}
