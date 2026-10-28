package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.OrigenParametroTipo;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;
import java.util.Map;

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
    public Token primerToken() {
        return nombre;
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    // §10.2: se busca en la clase actual por nombre/aridad, con los heredados
    // ya copiados por consolidar(). En un método estático no hay this, así
    // que sólo sirve otro estático (semIIError17). Un heredado se ve desde la
    // clase actual: en "Final extends Intermedia<String>", el T obtener() de
    // Base devuelve String (semIICorrecto13, D10). Un método private heredado
    // no se ve desde la subclase (logro Visibilidad chequeada). En los
    // argumentos de super(...) un método de instancia tampoco tiene objeto
    // construido (spec 0006, D9).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Metodo metodo = tabla.getClaseActual().buscarMetodo(Metodo.clave(nombre.getLexema(), argumentos.size()));
        if (metodo == null) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ENCONTRADO");
        }
        ChequeoVisibilidad.exigirAcceso(tabla, metodo.isPrivado(), metodo.getMiClase(), tabla.getClaseActual(), nombre);
        if (!metodo.isEstatico()) {
            tabla.exigirObjetoActual(nombre, "ERR_METODO_NO_ESTATICO");
        }
        Map<OrigenParametroTipo, Tipo> vista = tabla.getClaseActual().getInstanciacion();
        Tipo tipo = ChequeoLlamada.tipoDeLlamada(tabla, metodo, vista, nombre, argumentos);
        return chequearEncadenado(tabla, tipo);
    }
}
