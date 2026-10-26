package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Tipo;

// <Primitivo> ::= true | false
public class NodoLiteralBooleano extends NodoLiteral {
    public NodoLiteralBooleano(Token literal) {
        super(literal, Tipo.BOOLEAN);
    }

    @Override
    public Object valorConstante() {
        return Boolean.valueOf(literal.getLexema().equals("true"));
    }
}
