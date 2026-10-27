package Model.semantico.ast;

import Model.lexico.Token;
import Model.lexico.TokenType;
import Model.semantico.Tipo;

// <Primitivo> ::= true | false
public class NodoLiteralBooleano extends NodoLiteral {
    public NodoLiteralBooleano(Token literal) {
        super(literal, Tipo.BOOLEAN);
    }

    @Override
    public Boolean valorConstante() {
        return literal.getTipo() == TokenType.PR_TRUE;
    }
}
