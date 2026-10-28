package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <Sentencia> ::= <TipoBase> <RestoDeclLocal> (int x = 1, y;, int[] a;). Un
// solo nodo por sentencia, no uno por nombre: el bloque recibe una sentencia
// por cada declaración del fuente, que es lo que cuentan el flujo (spec 0004)
// y la Multi-detección (spec 0009, D3). El tipo ya trae sus dimensiones, y
// los declaradores van en el orden del fuente, cada uno con su inicializador.
// El for arma el mismo nodo con un solo declarador. Logro Variables locales
// clásicas: todavía no soportado, se reporta sobre el primer nombre (D11).
public class NodoVarLocalClasica extends NodoSentencia {
    private final Tipo tipo;
    private final List<Declarador> declaradores;

    public NodoVarLocalClasica(Tipo tipo, List<Declarador> declaradores) {
        this.tipo = tipo;
        this.declaradores = declaradores;
    }

    public Token getPrimerNombre() {
        return declaradores.get(0).nombre();
    }

    // El tipo (int, A1) va antes que el primer nombre (spec 0004, D7). Una
    // local clásica inalcanzable da además su "no soportado" (D8).
    @Override
    public Token primerToken() {
        return tipo.getToken();
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(getPrimerNombre());
    }
}
