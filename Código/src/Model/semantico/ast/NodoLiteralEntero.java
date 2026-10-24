package Model.semantico.ast;

import Model.lexico.Token;

// <Primitivo> ::= intLiteral
public class NodoLiteralEntero extends NodoLiteral {
    public NodoLiteralEntero(Token literal) {
        super(literal);
    }
}
