package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <Operando> ::= this <ReferenciaResto>
public class NodoThis extends NodoPrimario {
    private final Token tokenThis;

    public NodoThis(Token tokenThis) {
        this.tokenThis = tokenThis;
    }

    // §10.4: un método estático no tiene objeto receptor, tampoco adentro de
    // paréntesis ((this).a1, semIIError31). Un constructor o el inicializador
    // de un atributo sí lo tienen. El tipo es el de la clase actual.
    // Pendiente (ticket 06): en una clase genérica es Caja<T>; sin el
    // argumento, return this; daría un error falso, así que corta.
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (tabla.enContextoEstatico()) {
            throw new ExcepcionSemantica(tokenThis, "ERR_THIS_EN_ESTATICO");
        }
        Clase clase = tabla.getClaseActual();
        if (!clase.getParametrosTipo().isEmpty()) {
            throw ExcepcionSemantica.noSoportado(tokenThis);
        }
        return chequearEncadenado(tabla, new Tipo(clase.getNombre()));
    }
}
