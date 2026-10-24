package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= stringLiteral <ReferenciaResto>. Es un primario y no un
// NodoLiteral porque admite encadenado ("hola".toString(), ⚠4).
public class NodoLiteralString extends NodoPrimario {
    private final Token literal;

    public NodoLiteralString(Token literal) {
        this.literal = literal;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        return chequearEncadenado(tabla, null);
    }
}
