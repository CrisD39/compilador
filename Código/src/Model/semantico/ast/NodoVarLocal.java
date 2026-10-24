package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;

// <VarLocal> ::= var idMetVar = <ExpresionCompuesta>
// El "var" es el lexema de ERR_DECLARACION_COMO_CUERPO (§12.3).
public class NodoVarLocal extends NodoSentencia {
    private final Token var;
    private final Token nombre;
    private final NodoExpresion inicializador;

    public NodoVarLocal(Token var, Token nombre, NodoExpresion inicializador) {
        this.var = var;
        this.nombre = nombre;
        this.inicializador = inicializador;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        inicializador.chequear(tabla);
    }
}
