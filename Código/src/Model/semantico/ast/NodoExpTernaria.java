package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <TernarioOpcional> ::= ? <ExpresionCompuesta> : <ExpresionCompuesta>.
// ladoIzq es la rama antes de ":" y ladoDer la de después. Logro Ternarios
// (spec 0007, ADR 0011): todos sus errores van sobre el "?", el único token
// propio que guarda el nodo. No es asignable ni llamable, y nunca es una
// condición constante (ADR 0009): valorConstante() queda en null.
public class NodoExpTernaria extends NodoExpCompuesta {
    private final NodoExpresion condicion;
    private final Token operador;
    private final NodoExpresion ladoIzq;
    private final NodoExpresion ladoDer;

    public NodoExpTernaria(NodoExpresion condicion, Token operador, NodoExpresion ladoIzq, NodoExpresion ladoDer) {
        this.condicion = condicion;
        this.operador = operador;
        this.ladoIzq = ladoIzq;
        this.ladoDer = ladoDer;
    }

    @Override
    public Token primerToken() {
        return condicion.primerToken();
    }

    // D4, pasos 1 y 3: sin tipo esperado, el ternario tiene el tipo de la
    // rama con la que la otra conforma. No se busca un supertipo común, así
    // que dos clases hermanas son error (a diferencia de Java). El null sale
    // de conformaCon: null : null es null, null con una referencia da esa
    // referencia, y con un primitivo es error (no hay boxing).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        chequearCondicion(tabla);
        Tipo izq = chequearRama(ladoIzq, tabla);
        Tipo der = chequearRama(ladoDer, tabla);
        if (izq.conformaCon(der, tabla)) {
            return der;
        }
        if (der.conformaCon(izq, tabla)) {
            return izq;
        }
        throw new ExcepcionSemantica(operador, "ERR_TERNARIO_INCOMPATIBLE");
    }

    // La condición va antes que las ramas, así el error informado es el
    // primero del fuente.
    private void chequearCondicion(TablaSimbolos tabla) {
        if (!Tipo.BOOLEAN.equals(condicion.chequear(tabla))) {
            throw new ExcepcionSemantica(operador, "ERR_CONDICION_NO_BOOLEAN");
        }
    }

    // Filtro explícito de void aunque conformaCon ya lo rechace: una rama
    // void tiene su propio error, aunque lo sean las dos (c ? m() : n()).
    private Tipo chequearRama(NodoExpresion rama, TablaSimbolos tabla) {
        Tipo tipo = rama.chequear(tabla);
        if (tipo.esVoid()) {
            throw new ExcepcionSemantica(operador, "ERR_TERNARIO_RAMA_VOID");
        }
        return tipo;
    }
}
