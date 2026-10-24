package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Expresion> ::= <ExpresionCompuesta> <RestoAsignacion> con
// <RestoAsignacion> != ϵ. Es una expresión (⚠1, §11.9): puede aparecer en
// return x = 3; o como condición.
public class NodoExpAsignacion extends NodoExpresion {
    private final NodoExpresion ladoIzq;
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpAsignacion(NodoExpresion ladoIzq, Token operador, NodoExpresion ladoDer) {
        this.ladoIzq = ladoIzq;
        this.operador = operador;
        this.ladoDer = ladoDer;
    }

    // §11.9: el destino tiene que ser asignable (una variable, .x o [i], sin
    // paréntesis) y el valor conformar con su tipo. Los dos errores van
    // sobre el "=". El resultado tiene el tipo del destino: if (a1 = 3) es
    // una condición int (semIIError96). esAsignable() es estructural, así que
    // va antes de chequear el destino: en m9() = 3 el error es el destino, no
    // que m9 no exista.
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (!ladoIzq.esAsignable()) {
            throw new ExcepcionSemantica(operador, "ERR_DESTINO_NO_ASIGNABLE");
        }
        Tipo destino = ladoIzq.chequear(tabla);
        Tipo valor = ladoDer.chequear(tabla);
        if (!valor.conformaCon(destino, tabla)) {
            throw new ExcepcionSemantica(operador, "ERR_ASIGNACION_INCOMPATIBLE");
        }
        return destino;
    }

    @Override
    public boolean esSentenciaValida() {
        return true;
    }
}
