package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= idMetVar <TrasId> sin argumentos: una local, un parámetro
// o un atributo, en ese orden (D6). Sin encadenado es asignable.
public class NodoAccesoVar extends NodoPrimario {
    private final Token nombre;

    public NodoAccesoVar(Token nombre) {
        this.nombre = nombre;
    }

    @Override
    protected boolean esAsignableSinEncadenado() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        return chequearEncadenado(tabla, null);
    }
}
