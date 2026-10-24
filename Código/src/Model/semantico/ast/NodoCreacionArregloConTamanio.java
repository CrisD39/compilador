package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// new T[e1][e2]...: <DimensionesNew> con <TrasCorcheteNew> ::= <Expresion> ]
// <DimensionesConTamanioOpc> (⚠11). corchetes guarda el "[" de cada tamaño,
// que es el lexema de ERR_INDICE_NO_ENTERO.
public class NodoCreacionArregloConTamanio extends NodoPrimario {
    private final Tipo tipoBase;
    private final List<Token> corchetes;
    private final List<NodoExpresion> tamanios;

    public NodoCreacionArregloConTamanio(Tipo tipoBase, List<Token> corchetes, List<NodoExpresion> tamanios) {
        this.tipoBase = tipoBase;
        this.corchetes = corchetes;
        this.tamanios = tamanios;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        for (NodoExpresion tamanio : tamanios) {
            tamanio.chequear(tabla);
        }
        // Pendiente (spec 0003, ticket 05): recorre a sus hijos y al resto de
        // la cadena, así un error adentro (o un nodo de logro) igual se
        // reporta, y corta en vez de devolver un tipo que todavía no calcula.
        chequearEncadenado(tabla, null);
        throw ExcepcionSemantica.noSoportado(corchetes.get(0));
    }
}
