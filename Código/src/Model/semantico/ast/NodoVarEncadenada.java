package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Atributo;
import Model.semantico.Clase;
import Model.semantico.EntidadDeclarada;
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

    // §10.1: x se busca en el tipo estático del receptor (ver
    // entidadDelReceptor()), con los atributos heredados ya copiados por
    // consolidar(), y no en la clase del objeto (semIIError09). Una interfaz
    // no tiene atributos. La visibilidad se ignora (D11).
    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        EntidadDeclarada entidad = entidadDelReceptor(tabla, tipoIzquierdo, nombre);
        Atributo atributo = entidad instanceof Clase clase ? clase.getAtributo(nombre.getLexema()) : null;
        if (atributo == null) {
            throw new ExcepcionSemantica(nombre, "ERR_ATRIBUTO_NO_ENCONTRADO");
        }
        return chequearSiguiente(tabla, atributo.getTipo());
    }
}
