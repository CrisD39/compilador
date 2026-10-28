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

    @Override
    public Token primerToken() {
        return tokenReturn;
    }

    // §12.5. En un constructor o un método void sólo vale "return;" (un
    // return con expresión void también es error, semIIError103). En un
    // método no void la expresión es obligatoria y conforma con el retorno
    // declarado. La expresión se chequea primero, así un error adentro de
    // ella se reporta en su propio token. El retorno en todos los caminos lo
    // controla el logro Caminos de retorno (salida(), ADR 0008). En un
    // método no void el retorno es el tipo esperado de la expresión (spec
    // 0007, D3); en un constructor o un método void el esperado es null, o
    // sea sin tipo esperado.
    @Override
    public void chequear(TablaSimbolos tabla) {
        Metodo metodo = tabla.getMetodoActual();
        Tipo esperado = metodo != null ? metodo.getTipoRetorno() : null;
        Tipo tipo = null;
        if (expresion != null) {
            tipo = expresion.chequearConTipoEsperado(tabla, esperado);
        }
        boolean correcto = esperado == null ? tipo == null : tipo != null && tipo.conformaCon(esperado, tabla);
        if (!correcto) {
            throw new ExcepcionSemantica(tokenReturn, "ERR_RETORNO_INCORRECTO");
        }
    }

    @Override
    public Flujo salida(Flujo entrada) {
        return Flujo.MUERTO;
    }
}
