package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
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

    // Clase o interfaz donde .x y .m() buscan el miembro: la del tipo
    // ESTÁTICO del receptor (§10.1, §10.2). Un receptor que no es clase ni
    // interfaz (primitivo, void, T sin instanciar) no tiene miembros, y el
    // error va sobre el nombre del miembro.
    // Un arreglo tiene los miembros de instancia de Object (§11.8, "Receptor
    // arreglo" de requerimientos.md), sea cual sea su tipo base; length lo
    // resuelve NodoVarEncadenada antes de llegar acá (⚠10). Un T sin
    // instanciar no tiene cotas, así que tampoco tiene los miembros de Object
    // (semIIError11, 20). Con un genérico instanciado (Caja<String>) la
    // entidad es la misma, y cada nodo ve el miembro con
    // TablaSimbolos.instanciacionDe() (D10).
    protected static EntidadDeclarada entidadDelReceptor(TablaSimbolos tabla, Tipo receptor, Token miembro) {
        if (receptor.esArreglo()) {
            return tabla.getObject();
        }
        EntidadDeclarada entidad = tabla.entidadDe(receptor);
        if (entidad == null) {
            throw new ExcepcionSemantica(miembro, "ERR_RECEPTOR_SIN_MIEMBROS");
        }
        return entidad;
    }

    protected Tipo chequearSiguiente(TablaSimbolos tabla, Tipo tipoEslabon) {
        return siguienteEncadenado != null ? siguienteEncadenado.chequear(tabla, tipoEslabon) : tipoEslabon;
    }
}
