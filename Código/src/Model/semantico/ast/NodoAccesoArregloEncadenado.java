package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ReferenciaResto> ::= [ <Expresion> ] <ReferenciaResto> (⚠2). El "[" es el
// lexema de ERR_INDICE_NO_ENTERO y ERR_NO_ES_ARREGLO.
public class NodoAccesoArregloEncadenado extends NodoEncadenado {
    private final Token corchete;
    private final NodoExpresion indice;

    public NodoAccesoArregloEncadenado(Token corchete, NodoExpresion indice, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.corchete = corchete;
        this.indice = indice;
    }

    // Un componente es un destino asignable (§11.9), pero no una sentencia
    // (p1[0]; es error, semIIError78).
    @Override
    protected boolean esAsignableSinSiguiente() {
        return true;
    }

    // §11.8: sólo se indexa un arreglo por su tipo estático, así que un
    // arreglo guardado en un Object no se indexa (semIIError72). El receptor
    // se controla antes que el índice, en el orden del fuente. Cada
    // indexación quita una dimensión: con v de tipo int[], v[0] es int y
    // v[0][1] es error (semIIError75).
    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        if (!tipoIzquierdo.esArreglo()) {
            throw new ExcepcionSemantica(corchete, "ERR_NO_ES_ARREGLO");
        }
        if (!indice.chequear(tabla).conformaCon(Tipo.INT, tabla)) {
            throw new ExcepcionSemantica(corchete, "ERR_INDICE_NO_ENTERO");
        }
        return chequearSiguiente(tabla, tipoIzquierdo.conDimensiones(tipoIzquierdo.getDimensiones() - 1));
    }
}
