package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// x -> e, () -> e, (x) -> e y (x, y) -> e: sale de <TrasId>, <TrasParen>,
// <TrasParenId> y <DecidirTrasCierre>. Logro Minilambdas: todavía no
// soportado, se reporta sobre "->" (D11).
public class NodoLambda extends NodoOperando {
    private final List<Token> parametros;
    private final Token flecha;
    private final NodoExpresion cuerpo;

    public NodoLambda(List<Token> parametros, Token flecha, NodoExpresion cuerpo) {
        this.parametros = parametros;
        this.flecha = flecha;
        this.cuerpo = cuerpo;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(flecha);
    }
}
