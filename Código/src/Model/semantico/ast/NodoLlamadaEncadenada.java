package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <ReferenciaResto> ::= . idMetVar <ArgsActuales> <ReferenciaResto>: .m(...)
// (⚠2, ⚠6).
public class NodoLlamadaEncadenada extends NodoEncadenado {
    private final Token nombre;
    private final List<NodoExpresion> argumentos;

    public NodoLlamadaEncadenada(Token nombre, List<NodoExpresion> argumentos, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    @Override
    protected boolean esLlamableSinSiguiente() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        for (NodoExpresion argumento : argumentos) {
            argumento.chequear(tabla);
        }
        // Pendiente (spec 0003, ticket 04): recorre a sus hijos y al resto de
        // la cadena, así un error adentro (o un nodo de logro) igual se
        // reporta, y corta en vez de devolver un tipo que todavía no calcula.
        chequearSiguiente(tabla, null);
        throw ExcepcionSemantica.noSoportado(nombre);
    }
}
