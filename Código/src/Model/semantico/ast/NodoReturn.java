package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;

// <Return> ::= return <ExpresionOpcional>. expresion es null en "return;".
public class NodoReturn extends NodoSentencia {
    private final Token tokenReturn;
    private final NodoExpresion expresion;

    public NodoReturn(Token tokenReturn, NodoExpresion expresion) {
        this.tokenReturn = tokenReturn;
        this.expresion = expresion;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        if (expresion != null) {
            expresion.chequear(tabla);
        }
    }
}
