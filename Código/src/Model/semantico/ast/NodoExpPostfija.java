package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <PostfijoOpcional> ::= ++ ... | -- ... (⚠5). Es recursivo: a++-- es
// Postfija(Postfija(a, ++), --), y el chequeo lo rechaza porque a++ no es
// asignable (D12).
public class NodoExpPostfija extends NodoExpCompuesta {
    private final NodoExpresion ladoIzq;
    private final Token operador;

    public NodoExpPostfija(NodoExpresion ladoIzq, Token operador) {
        this.ladoIzq = ladoIzq;
        this.operador = operador;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        ladoIzq.chequear(tabla);
        return null;
    }
}
