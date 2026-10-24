package Model.semantico.ast;

import Model.lexico.Token;

// <Primitivo> ::= null
public class NodoLiteralNull extends NodoLiteral {
    public NodoLiteralNull(Token literal) {
        super(literal);
    }
}
