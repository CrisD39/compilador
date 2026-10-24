package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;

// <While> ::= while ( <Expresion> ) <Sentencia>
public class NodoWhile extends NodoSentencia {
    private final Token tokenWhile;
    private final NodoExpresion condicion;
    private final NodoSentencia cuerpo;

    public NodoWhile(Token tokenWhile, NodoExpresion condicion, NodoSentencia cuerpo) {
        this.tokenWhile = tokenWhile;
        this.condicion = condicion;
        this.cuerpo = cuerpo;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        condicion.chequear(tabla);
        cuerpo.chequear(tabla);
    }
}
