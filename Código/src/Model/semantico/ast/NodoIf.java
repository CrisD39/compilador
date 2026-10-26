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

    // D3: sin else, el camino con la condición falsa sale como entró. El if
    // no se trata como constante, ni con if (true), igual que en Java
    // (JLS §14.22): if (true) return 1; puede terminar normalmente.
    @Override
    public Flujo salida(Flujo entrada) {
        Flujo salidaEntonces = entonces.salida(entrada);
        return sino == null ? entrada.unir(salidaEntonces) : salidaEntonces.unir(sino.salida(entrada));
    }
}
