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
    // Pendiente: el receptor arreglo (length y toString(), ticket 05) y el
    // genérico instanciado, cuyos miembros se ven con T sustituido
    // (Caja<String>, ticket 06, D10). Cortan en vez de devolver un tipo con
    // T sin reemplazar, que daría un error falso.
    protected static EntidadDeclarada entidadDelReceptor(TablaSimbolos tabla, Tipo receptor, Token miembro) {
        if (receptor.esArreglo() || receptor.getArgumento() != null) {
            throw ExcepcionSemantica.noSoportado(miembro);
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
