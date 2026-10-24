package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Return> ::= return <ExpresionOpcional>. expresion es null en "return;".
public class NodoReturn extends NodoSentencia {
    private final Token tokenReturn;
    private final NodoExpresion expresion;

    public NodoReturn(Token tokenReturn, NodoExpresion expresion) {
        this.tokenReturn = tokenReturn;
        this.expresion = expresion;
    }

    // §12.5. En un constructor o un método void sólo vale "return;" (un
    // return con expresión void también es error, semIIError103). En un
    // método no void la expresión es obligatoria y conforma con el retorno
    // declarado. La expresión se chequea primero, así un error adentro de
    // ella se reporta en su propio token. No se exige retorno en todos los
    // caminos (semIICorrecto31; logro aparte).
    @Override
    public void chequear(TablaSimbolos tabla) {
        Tipo tipo = expresion != null ? expresion.chequear(tabla) : null;
        Metodo metodo = tabla.getMetodoActual();
        Tipo esperado = metodo != null ? metodo.getTipoRetorno() : null;
        boolean correcto = esperado == null ? tipo == null : tipo != null && tipo.conformaCon(esperado, tabla);
        if (!correcto) {
            throw new ExcepcionSemantica(tokenReturn, "ERR_RETORNO_INCORRECTO");
        }
    }
}
