package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
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
        // Pendiente (spec 0003, ticket 04): recorre a sus hijos y al resto de
        // la cadena, así un error adentro (o un nodo de logro) igual se
        // reporta, y corta en vez de devolver un tipo que todavía no calcula.
        chequearSiguiente(tabla, null);
        throw ExcepcionSemantica.noSoportado(nombre);
    }
}
