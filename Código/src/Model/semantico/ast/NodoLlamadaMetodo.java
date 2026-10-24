package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <Operando> ::= idMetVar <TrasId> con <ArgsActuales>: llamada con receptor
// implícito, m(...) (⚠6).
public class NodoLlamadaMetodo extends NodoPrimario {
    private final Token nombre;
    private final List<NodoExpresion> argumentos;

    public NodoLlamadaMetodo(Token nombre, List<NodoExpresion> argumentos) {
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        for (NodoExpresion argumento : argumentos) {
            argumento.chequear(tabla);
        }
        return chequearEncadenado(tabla, null);
    }
}
