package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <LlamadaMetodoEstatico> ::= idClase . idMetVar <ArgsActuales> (⚠6). Sale
// también de la rama "." de <SentIdClase> y de <TrasIdClaseFor> (⚠13).
public class NodoLlamadaEstatica extends NodoPrimario {
    private final Token clase;
    private final Token nombre;
    private final List<NodoExpresion> argumentos;

    public NodoLlamadaEstatica(Token clase, Token nombre, List<NodoExpresion> argumentos) {
        this.clase = clase;
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        for (NodoExpresion argumento : argumentos) {
            argumento.chequear(tabla);
        }
        return chequearEncadenado(tabla, null);
    }
}
