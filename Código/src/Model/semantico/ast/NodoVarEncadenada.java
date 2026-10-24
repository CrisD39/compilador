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
    // true si chequear() lo resolvió como el length de un arreglo, que es de
    // sólo lectura (§11.8). Depende del tipo del receptor, así que recién se
    // sabe después de chequear: por eso NodoExpAsignacion vuelve a preguntar
    // esAsignable() tras chequear el destino (semIIError67).
    private boolean esLength;

    public NodoVarEncadenada(Token nombre, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.nombre = nombre;
    }

    @Override
    protected boolean esAsignableSinSiguiente() {
        return !esLength;
    }

    // §10.1: x se busca en el tipo estático del receptor (ver
    // entidadDelReceptor()), con los atributos heredados ya copiados por
    // consolidar(), y no en la clase del objeto (semIIError09). Una interfaz
    // no tiene atributos. La visibilidad se ignora (D11). El único atributo
    // de un arreglo es length, de tipo int (⚠10); otro nombre se busca en
    // Object, que no tiene atributos. Con tipo estático Object tampoco hay
    // length (semIIError73).
    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        esLength = tipoIzquierdo.esArreglo() && nombre.getLexema().equals("length");
        if (esLength) {
            return chequearSiguiente(tabla, Tipo.INT);
        }
        EntidadDeclarada entidad = entidadDelReceptor(tabla, tipoIzquierdo, nombre);
        Atributo atributo = entidad instanceof Clase clase ? clase.getAtributo(nombre.getLexema()) : null;
        if (atributo == null) {
            throw new ExcepcionSemantica(nombre, "ERR_ATRIBUTO_NO_ENCONTRADO");
        }
        return chequearSiguiente(tabla, atributo.getTipo());
    }
}
