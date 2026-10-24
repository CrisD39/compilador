package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ExpresionBasica> ::= <OperadorUnario> <Operando> <PostfijoOpcional>. El
// postfijo liga primero al operando (⚠5): -a++ es Unaria(-, Postfija(a, ++)).
public class NodoExpUnaria extends NodoExpCompuesta {
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpUnaria(Token operador, NodoExpresion ladoDer) {
        this.operador = operador;
        this.ladoDer = ladoDer;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        ladoDer.chequear(tabla);
        return null;
    }
}
