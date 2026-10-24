package Model.semantico.ast;

import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <RestoNew> ::= idClase <TipoGenericoOpcionalNew> <ArgsActuales>. El token
// del tipo (el nombre de la clase) es el lexema de sus errores. Con
// diamante (new C<>()) el tipo queda sin argumento, y el logro Genericidad
// avanzada todavía no está soportado (D11).
public class NodoCreacionObjeto extends NodoPrimario {
    private final Tipo tipo;
    private final boolean esDiamante;
    private final List<NodoExpresion> argumentos;

    public NodoCreacionObjeto(Tipo tipo, boolean esDiamante, List<NodoExpresion> argumentos) {
        this.tipo = tipo;
        this.esDiamante = esDiamante;
        this.argumentos = argumentos;
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (esDiamante) {
            throw ExcepcionSemantica.noSoportado(tipo.getToken());
        }
        for (NodoExpresion argumento : argumentos) {
            argumento.chequear(tabla);
        }
        return chequearEncadenado(tabla, null);
    }
}
