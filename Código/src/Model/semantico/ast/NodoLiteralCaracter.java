package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Tipo;

// <Primitivo> ::= charLiteral
public class NodoLiteralCaracter extends NodoLiteral {
    public NodoLiteralCaracter(Token literal) {
        super(literal, Tipo.CHAR);
    }
}
