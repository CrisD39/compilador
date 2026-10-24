package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;

// <If> ::= if ( <Expresion> ) <Sentencia> <ElseOpcional>. Un solo nodo con y
// sin else (⚠8): sino es null cuando no hay else.
public class NodoIf extends NodoSentencia {
    private final Token tokenIf;
    private final NodoExpresion condicion;
    private final NodoSentencia entonces;
    private final NodoSentencia sino;

    public NodoIf(Token tokenIf, NodoExpresion condicion, NodoSentencia entonces, NodoSentencia sino) {
        this.tokenIf = tokenIf;
        this.condicion = condicion;
        this.entonces = entonces;
        this.sino = sino;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        condicion.chequear(tabla);
        entonces.chequear(tabla);
        if (sino != null) {
            sino.chequear(tabla);
        }
    }
}
