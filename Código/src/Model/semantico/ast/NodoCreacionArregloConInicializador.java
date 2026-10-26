package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// new T[][]{...}: <TrasCorcheteNew> ::= ] <MasCorchetesVaciosNew>
// <InicializadorArreglo> (⚠11). Logro: todavía no soportado, se reporta
// sobre "new" (D11).
public class NodoCreacionArregloConInicializador extends NodoPrimario {
    private final Token tokenNew;
    private final Tipo tipoBase;
    private final int dimensiones;
    private final NodoInicializadorArreglo inicializador;

    public NodoCreacionArregloConInicializador(Token tokenNew, Tipo tipoBase, int dimensiones,
                                               NodoInicializadorArreglo inicializador) {
        this.tokenNew = tokenNew;
        this.tipoBase = tipoBase;
        this.dimensiones = dimensiones;
        this.inicializador = inicializador;
    }

    @Override
    public Token primerToken() {
        return tipoBase.getToken();
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(tokenNew);
    }
}
