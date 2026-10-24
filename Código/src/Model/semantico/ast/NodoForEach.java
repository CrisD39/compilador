package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <For> + <TrasIdForTipo> ::= : <Expresion> (for (T x : iterable)). El
// cuerpo se setea aparte, como en NodoFor. Logro For: todavía no soportado
// (D11).
public class NodoForEach extends NodoSentencia {
    private final Token tokenFor;
    private final Tipo tipo;
    private final Token nombre;
    private final NodoExpresion iterable;
    private NodoSentencia cuerpo;

    public NodoForEach(Token tokenFor, Tipo tipo, Token nombre, NodoExpresion iterable) {
        this.tokenFor = tokenFor;
        this.tipo = tipo;
        this.nombre = nombre;
        this.iterable = iterable;
    }

    public void setCuerpo(NodoSentencia cuerpo) {
        this.cuerpo = cuerpo;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(tokenFor);
    }
}
