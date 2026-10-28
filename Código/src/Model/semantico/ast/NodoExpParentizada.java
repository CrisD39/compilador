package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// ( <Expresion> ) <ReferenciaResto> (⚠3). Conserva el tipo de adentro pero
// corta esAsignable() y esLlamable(), aun con encadenado: (x) = 3;,
// (m()); y (p1).m2(); no son válidos (§11.4, semIIError82).
public class NodoExpParentizada extends NodoPrimario {
    private final NodoExpresion expresion;

    public NodoExpParentizada(NodoExpresion expresion) {
        this.expresion = expresion;
    }

    @Override
    public Token primerToken() {
        return expresion.primerToken();
    }

    @Override
    public boolean esAsignable() {
        return false;
    }

    @Override
    public boolean esLlamable() {
        return false;
    }

    // (true) es constante; con encadenado ((x).m()) ya no es la expresión de
    // adentro.
    @Override
    public Boolean valorConstante() {
        return encadenado == null ? expresion.valorConstante() : null;
    }

    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        return chequearEncadenado(tabla, expresion.chequear(tabla));
    }

    // El paréntesis no cambia el valor, así que el tipo esperado baja a la
    // expresión de adentro (spec 0007, D2). Con encadenado no: en
    // (c ? b : a).getN() el esperado es el de getN(), no el del ternario.
    @Override
    public Tipo chequearConTipoEsperado(TablaSimbolos tabla, Tipo esperado) {
        if (encadenado != null) {
            return chequear(tabla);
        }
        return expresion.chequearConTipoEsperado(tabla, esperado);
    }
}
