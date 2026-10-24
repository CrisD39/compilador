package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <Sentencia> ::= <Tipo> <RestoDeclLocal> (int x, y, z = 10;). Un solo nodo
// para toda la lista, y el inicializador vale sólo para el último nombre,
// como en Java (⚠7). Logro Variables locales clásicas: todavía no
// soportado, se reporta sobre el primer nombre (D11).
public class NodoVarLocalClasica extends NodoSentencia {
    private final Tipo tipo;
    private final List<Token> nombres;
    private final NodoExpresion inicializador;

    public NodoVarLocalClasica(Tipo tipo, List<Token> nombres, NodoExpresion inicializador) {
        this.tipo = tipo;
        this.nombres = nombres;
        this.inicializador = inicializador;
    }

    public Token getPrimerNombre() {
        return nombres.get(0);
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(getPrimerNombre());
    }
}
