package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Atributo;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;
import Model.semantico.Variable;
import Model.semantico.VariableLocal;

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

    // Un atributo implícito es this.x, y en un método estático no hay this
    // (§10.1, semIIError08). Se controla después de resolver: una local o un
    // parámetro con ese nombre sí valen en un método estático. Un atributo
    // heredado se ve desde la clase actual: en "Final extends
    // Intermedia<String>", el T dato de Base es String (semIICorrecto13, D10).
    // Una local cuyo var falló no tiene tipo para seguir: la sentencia se
    // corta sin otro error (semIIMultiErrorError03). Un atributo private
    // heredado no se ve desde la subclase (logro Visibilidad chequeada).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Variable variable = tabla.buscarVariable(nombre.getLexema());
        if (variable == null) {
            throw new ExcepcionSemantica(nombre, "ERR_VAR_NO_DECLARADA");
        }
        if (variable instanceof VariableLocal local && local.esSinTipo()) {
            throw ExcepcionSemantica.enCascada(nombre);
        }
        if (variable instanceof Atributo atributo) {
            ChequeoVisibilidad.exigirAcceso(tabla, atributo.isPrivado(), atributo.getClaseDeclarante(),
                    tabla.getClaseActual(), nombre);
        }
        if (variable instanceof Atributo && tabla.enContextoEstatico()) {
            throw new ExcepcionSemantica(nombre, "ERR_ATRIBUTO_EN_ESTATICO");
        }
        Tipo tipo = variable.getTipo();
        if (variable instanceof Atributo) {
            tipo = tipo.sustituir(tabla.getClaseActual().getInstanciacion());
        }
        return chequearEncadenado(tabla, tipo);
    }
}
