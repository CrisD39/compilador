package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;

// <Sentencia> ::= ;
// El ";" es el lexema de la sentencia vacía inalcanzable (spec 0004, D7).
public class NodoSentenciaVacia extends NodoSentencia {
    private final Token puntoYComa;

    public NodoSentenciaVacia(Token puntoYComa) {
        this.puntoYComa = puntoYComa;
    }

    @Override
    public Token primerToken() {
        return puntoYComa;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
    }
}
