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
    public Token primerToken() {
        return nombre;
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
        return chequear(tabla, false);
    }

    // Sólo el nombre sin encadenado es el destino directo del "=": en
    // b[0] = 1 o b.x = 1 se lee b para llegar al componente o al atributo, y
    // sigue siendo una referencia adelantada (spec 0005, D7).
    @Override
    public Tipo chequearComoDestino(TablaSimbolos tabla) {
        return chequear(tabla, encadenado == null);
    }

    private Tipo chequear(TablaSimbolos tabla, boolean esDestinoDirecto) {
        Variable variable = tabla.buscarVariable(nombre.getLexema());
        if (variable == null) {
            throw new ExcepcionSemantica(nombre, "ERR_VAR_NO_DECLARADA");
        }
        if (variable instanceof VariableLocal local && local.esSinTipo()) {
            throw ExcepcionSemantica.enCascada(nombre);
        }
        Tipo tipo = variable.getTipo();
        if (variable instanceof Atributo atributo) {
            ChequeoVisibilidad.exigirAcceso(tabla, atributo.isPrivado(), atributo.getClaseDeclarante(),
                    tabla.getClaseActual(), nombre);
            if (tabla.enContextoEstatico()) {
                throw new ExcepcionSemantica(nombre, "ERR_ATRIBUTO_EN_ESTATICO");
            }
            if (!esDestinoDirecto) {
                exigirDeclaradoAntes(tabla, atributo);
            }
            tipo = tipo.sustituir(tabla.getClaseActual().getInstanciacion());
        }
        return chequearEncadenado(tabla, tipo);
    }

    // Referencia adelantada (spec 0005, D6; JLS §8.3.3): en el inicializador
    // de un atributo, un atributo de la misma clase declarante que no está
    // declarado antes (el propio incluido: int x = x + 1;). Va antes del
    // encadenado, así b.x, b[0] y m(b) cuentan. this.b y obj.b no pasan por
    // acá (son NodoVarEncadenada), un heredado lo declara otra clase y no se
    // puede ocultar, y en métodos y constructores atributoActual es null.
    private void exigirDeclaradoAntes(TablaSimbolos tabla, Atributo atributo) {
        Atributo inicializado = tabla.getAtributoActual();
        if (inicializado != null && atributo.getClaseDeclarante() == tabla.getClaseActual()
                && atributo.getIndiceDeclaracion() >= inicializado.getIndiceDeclaracion()) {
            throw new ExcepcionSemantica(nombre, "ERR_REFERENCIA_ADELANTADA");
        }
    }
}
