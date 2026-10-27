package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= this <ReferenciaResto>
public class NodoThis extends NodoPrimario {
    private final Token tokenThis;

    public NodoThis(Token tokenThis) {
        this.tokenThis = tokenThis;
    }

    @Override
    public Token primerToken() {
        return tokenThis;
    }

    // §10.4: un método estático no tiene objeto receptor, tampoco adentro de
    // paréntesis ((this).a1, semIIError31). Un constructor o el inicializador
    // de un atributo sí lo tienen. El tipo es el de la clase actual con su
    // parámetro de tipo: Caja<T> dentro de Caja<T> (§11.3, semIICorrecto34).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (tabla.enContextoEstatico()) {
            throw new ExcepcionSemantica(tokenThis, "ERR_THIS_EN_ESTATICO");
        }
        if (tabla.enContextoPrevioAConstruccion()) {
            throw new ExcepcionSemantica(tokenThis, "ERR_REFERENCIA_ANTES_DE_SUPER");
        }
        return chequearEncadenado(tabla, tabla.getClaseActual().comoTipo());
    }
}
