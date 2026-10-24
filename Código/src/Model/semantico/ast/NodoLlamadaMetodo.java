package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
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

    // §10.2: se busca en la clase actual por nombre/aridad, con los heredados
    // ya copiados por consolidar(). En un método estático no hay this, así
    // que sólo sirve otro estático (semIIError17).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Metodo metodo = tabla.getClaseActual().buscarMetodo(Metodo.clave(nombre.getLexema(), argumentos.size()));
        if (metodo == null) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ENCONTRADO");
        }
        if (!metodo.isEstatico() && tabla.enContextoEstatico()) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ESTATICO");
        }
        List<Tipo> tiposArgumentos = ChequeoLlamada.chequearArgumentos(tabla, argumentos);
        return chequearEncadenado(tabla, ChequeoLlamada.tipoDeLlamada(tabla, metodo, nombre, tiposArgumentos));
    }
}
