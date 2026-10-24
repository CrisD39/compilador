package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

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

    // §12.4: la condición es boolean, sin conversiones. Se chequean las dos
    // ramas aunque una no se ejecute (semIIError97).
    @Override
    public void chequear(TablaSimbolos tabla) {
        if (!Tipo.BOOLEAN.equals(condicion.chequear(tabla))) {
            throw new ExcepcionSemantica(tokenIf, "ERR_CONDICION_NO_BOOLEAN");
        }
        chequearCuerpo(entonces, tabla);
        if (sino != null) {
            chequearCuerpo(sino, tabla);
        }
    }
}
