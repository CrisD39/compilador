package Model.semantico.ast;

import Model.lexico.Token;
import Model.lexico.TokenType;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ExpresionBasica> ::= <OperadorUnario> <Operando> <PostfijoOpcional>. El
// postfijo liga primero al operando (⚠5): -a++ es Unaria(-, Postfija(a, ++)).
public class NodoExpUnaria extends NodoExpCompuesta {
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpUnaria(Token operador, NodoExpresion ladoDer) {
        this.operador = operador;
        this.ladoDer = ladoDer;
    }

    // §11.2: + y - sobre int, ! sobre boolean; el resultado es del mismo tipo.
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Tipo operando = ladoDer.chequear(tabla);
        Tipo esperado = operador.getTipo() == TokenType.OP_NOT ? Tipo.BOOLEAN : Tipo.INT;
        if (!esperado.equals(operando)) {
            throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
        }
        return esperado;
    }
}
