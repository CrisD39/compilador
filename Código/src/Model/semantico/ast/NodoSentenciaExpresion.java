package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

// <Sentencia> ::= <Expresion> ; (y la rama "." de <SentIdClase>). Único
// nodo que envuelve una expresión como sentencia (⚠1): la asignación y la
// llamada son expresiones, y acá se valida que la expresión sirva como
// sentencia (§12.2). El ";" es el lexema de ese error.
public class NodoSentenciaExpresion extends NodoSentencia {
    private final NodoExpresion expresion;
    private final Token puntoYComa;

    public NodoSentenciaExpresion(NodoExpresion expresion, Token puntoYComa) {
        this.expresion = expresion;
        this.puntoYComa = puntoYComa;
    }

    public NodoExpresion getExpresion() {
        return expresion;
    }

    // Spec 0004, D7: se deriva de la expresión, bajando por la izquierda
    // (x = 2; da x, new A1().m(); da A1, (x).m(); da x).
    @Override
    public Token primerToken() {
        return expresion.primerToken();
    }

    // La forma se controla antes de tipar, como javac ("not a statement"):
    // esSentenciaValida() es estructural, así que en c ? v() : v(); con v
    // void el error es el ";" y no la rama void (spec 0007, D5).
    @Override
    public void chequear(TablaSimbolos tabla) {
        if (!expresion.esSentenciaValida()) {
            throw new ExcepcionSemantica(puntoYComa, "ERR_SENTENCIA_INVALIDA");
        }
        expresion.chequear(tabla);
    }
}
