package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ReferenciaResto> ::= . idMetVar <ReferenciaResto> sin argumentos: .x
// (⚠2). También resuelve "length" de un arreglo (⚠10), que no es asignable.
public class NodoVarEncadenada extends NodoEncadenado {
    private final Token nombre;

    public NodoVarEncadenada(Token nombre, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.nombre = nombre;
    }

    @Override
    protected boolean esAsignableSinSiguiente() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        return chequearSiguiente(tabla, null);
    }
}
