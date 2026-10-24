package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Primitivo>: una subclase por tipo de literal (⚠4), cada una con su tipo
// fijo. El String no está acá: admite encadenado y es un NodoPrimario.
public abstract class NodoLiteral extends NodoOperando {
    protected final Token literal;

    protected NodoLiteral(Token literal) {
        this.literal = literal;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        return null;
    }
}
