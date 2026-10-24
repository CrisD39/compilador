package Model.semantico.ast;

import Model.lexico.Token;

// <Primitivo> ::= charLiteral
public class NodoLiteralCaracter extends NodoLiteral {
    public NodoLiteralCaracter(Token literal) {
        super(literal);
    }
}
