package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Tipo;

// <Primitivo> ::= intLiteral
public class NodoLiteralEntero extends NodoLiteral {
    public NodoLiteralEntero(Token literal) {
        super(literal, Tipo.INT);
    }
}
