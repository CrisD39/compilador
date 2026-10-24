package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
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
        // Pendiente (spec 0003, ticket 04): recorre a sus hijos y al resto de
        // la cadena, así un error adentro (o un nodo de logro) igual se
        // reporta, y corta en vez de devolver un tipo que todavía no calcula.
        chequearEncadenado(tabla, null);
        throw ExcepcionSemantica.noSoportado(tokenThis);
    }
}
