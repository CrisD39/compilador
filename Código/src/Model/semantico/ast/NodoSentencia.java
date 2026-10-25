package Model.semantico.ast;

import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

// Raíz de las sentencias del AST (requerimientos.md, "NODOS"). El estado del
// chequeo (scopes, clase y unidad actuales) vive en la tabla. Un error corta
// la sentencia con ExcepcionSemantica.
public abstract class NodoSentencia {
    public abstract void chequear(TablaSimbolos tabla);

    // Cuerpo de if, else, while o for (§12.3, ⚠9): una declaración local no puede ir
    // sola, sin bloque, porque su alcance no tendría sentido. Fuera de esa
    // regla, un cuerpo sin llaves no abre scope: no puede declarar nada. La
    // regla es base y vale también para una local clásica (int x = 1;), antes
    // de su "no soportado" de logro; sin "var", va sobre su primer nombre.
    protected static void chequearCuerpo(NodoSentencia cuerpo, TablaSimbolos tabla) {
        if (cuerpo instanceof NodoVarLocal declaracion) {
            throw new ExcepcionSemantica(declaracion.getVar(), "ERR_DECLARACION_COMO_CUERPO");
        }
        if (cuerpo instanceof NodoVarLocalClasica declaracion) {
            throw new ExcepcionSemantica(declaracion.getPrimerNombre(), "ERR_DECLARACION_COMO_CUERPO");
        }
        cuerpo.chequear(tabla);
    }
}
