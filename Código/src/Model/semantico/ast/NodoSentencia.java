package Model.semantico.ast;

import Model.semantico.TablaSimbolos;

// Raíz de las sentencias del AST (requerimientos.md, "NODOS"). El estado del
// chequeo (scopes, clase y unidad actuales) vive en la tabla. Un error corta
// la sentencia con ExcepcionSemantica.
public abstract class NodoSentencia {
    public abstract void chequear(TablaSimbolos tabla);
}
