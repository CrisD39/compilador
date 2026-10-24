package Model.semantico.ast;

import Model.lexico.Token;

// <Primitivo> ::= true | false
public class NodoLiteralBooleano extends NodoLiteral {
    public NodoLiteralBooleano(Token literal) {
        super(literal);
    }
}
