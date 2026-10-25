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

    @Override
    public void chequear(TablaSimbolos tabla) {
        expresion.chequear(tabla);
        if (!expresion.esSentenciaValida()) {
            throw new ExcepcionSemantica(puntoYComa, "ERR_SENTENCIA_INVALIDA");
        }
    }
}
