package Model.semantico.ast;

import Model.lexico.Token;
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
        return chequearSiguiente(tabla, null);
    }
}
