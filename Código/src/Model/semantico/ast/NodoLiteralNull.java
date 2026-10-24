package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Tipo;

// <Primitivo> ::= null
public class NodoLiteralNull extends NodoLiteral {
    public NodoLiteralNull(Token literal) {
        super(literal, Tipo.NULL);
    }
}
