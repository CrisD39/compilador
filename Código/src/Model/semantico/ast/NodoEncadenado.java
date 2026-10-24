package Model.semantico.ast;

import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// Un eslabón de <ReferenciaResto> (.x, .m() o [i]), en lista enlazada (⚠2).
// chequear() recibe el tipo de lo que está a la izquierda.
public abstract class NodoEncadenado {
    protected final NodoEncadenado siguienteEncadenado;

    protected NodoEncadenado(NodoEncadenado siguienteEncadenado) {
        this.siguienteEncadenado = siguienteEncadenado;
    }

    public abstract Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo);

    public boolean esAsignable() {
        return siguienteEncadenado != null ? siguienteEncadenado.esAsignable() : esAsignableSinSiguiente();
    }

    public boolean esLlamable() {
        return siguienteEncadenado != null ? siguienteEncadenado.esLlamable() : esLlamableSinSiguiente();
    }

    protected boolean esAsignableSinSiguiente() {
        return false;
    }

    protected boolean esLlamableSinSiguiente() {
        return false;
    }

    protected Tipo chequearSiguiente(TablaSimbolos tabla, Tipo tipoEslabon) {
        return siguienteEncadenado != null ? siguienteEncadenado.chequear(tabla, tipoEslabon) : tipoEslabon;
    }
}
