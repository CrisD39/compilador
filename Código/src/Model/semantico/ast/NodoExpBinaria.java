package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ExpresionCompuestaResto> ::= <OperadorBinario> <ExpresionBasica> ...
// Sin precedencia (política de Etapa 2): el parser arma una sola cadena
// asociativa a izquierda, y 2+4*b queda como (2+4)*b.
public class NodoExpBinaria extends NodoExpCompuesta {
    private final NodoExpresion ladoIzq;
    private final Token operador;
    private final NodoExpresion ladoDer;

    public NodoExpBinaria(NodoExpresion ladoIzq, Token operador, NodoExpresion ladoDer) {
        this.ladoIzq = ladoIzq;
        this.operador = operador;
        this.ladoDer = ladoDer;
    }

    @Override
    public Token primerToken() {
        return ladoIzq.primerToken();
    }

    // §11.2: sin promoción ni concatenación ("a" + "b" y 'a' + 1 son error).
    // Un operando void no es int ni boolean, y no conforma con nada en ==.
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Tipo izq = ladoIzq.chequear(tabla);
        Tipo der = ladoDer.chequear(tabla);
        //Aca tengo que agreagar la coersión para los floats.
        return switch (operador.getTipo()) {
            case OP_MAS, OP_MENOS, OP_MULT, OP_DIV, OP_MOD -> exigir(izq, der, Tipo.INT, Tipo.INT);
            case OP_MENOR, OP_MAYOR, OP_MENOR_IGUAL, OP_MAYOR_IGUAL -> exigir(izq, der, Tipo.INT, Tipo.BOOLEAN);
            case OP_AND, OP_OR -> exigir(izq, der, Tipo.BOOLEAN, Tipo.BOOLEAN);
            case OP_IGUAL, OP_DISTINTO -> {
                if (!izq.conformaCon(der, tabla) && !der.conformaCon(izq, tabla)) {
                    throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
                }
                yield Tipo.BOOLEAN;
            }
            default -> throw new IllegalStateException("Operador binario inesperado: " + operador.getLexema());
        };
    }

    private Tipo exigir(Tipo izq, Tipo der, Tipo operandos, Tipo resultado) {
        if (!operandos.equals(izq) || !operandos.equals(der)) {
            throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
        }
        return resultado;
    }
}
