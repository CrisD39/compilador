package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
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

    // Logro Postfijos (D12): el operando tiene que ser int y un destino
    // asignable, porque se modifica. El resultado es int y no es asignable
    // (hereda esAsignable() == false).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Tipo operando = ladoIzq.chequear(tabla);
        if (!Tipo.INT.equals(operando)) {
            throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
        }
        if (!ladoIzq.esAsignable()) {
            throw new ExcepcionSemantica(operador, "ERR_DESTINO_NO_ASIGNABLE");
        }
        return Tipo.INT;
    }

    // a++; es una sentencia válida (D12).
    @Override
    public boolean esSentenciaValida() {
        return true;
    }
}
