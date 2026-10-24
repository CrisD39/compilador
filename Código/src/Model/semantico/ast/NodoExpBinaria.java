package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ExpresionCompuestaResto> ::= <OperadorBinario> <ExpresionBasica> ...
// Sin precedencia (política de Etapa 2): el parser arma una sola cadena
// asociativa a izquierda, y 2+4*b queda como (2+4)*b.
public class NodoExpBinaria extends NodoExpCompuesta {
    private final NodoExpresion ladoIzq;
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpBinaria(NodoExpresion ladoIzq, Token operador, NodoExpresion ladoDer) {
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
