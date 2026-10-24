package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= this <ReferenciaResto>
public class NodoThis extends NodoPrimario {
    private final Token tokenThis;

    public NodoThis(Token tokenThis) {
        this.tokenThis = tokenThis;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        return chequearEncadenado(tabla, null);
    }
}
