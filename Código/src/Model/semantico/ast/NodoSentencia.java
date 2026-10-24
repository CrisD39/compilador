package Model.semantico.ast;

import Model.semantico.TablaSimbolos;

// Raíz de las sentencias del AST (requerimientos.md, "NODOS"). El estado del
// chequeo (scopes, clase y unidad actuales) vive en la tabla. Un error corta
// la sentencia con ExcepcionSemantica.
// Mientras los chequeos base no existan (tickets 03-06 de la spec 0003), los
// nodos base sólo recorren a sus hijos: así un nodo de logro anidado igual
// llega a reportar su "no soportado" (D11).
public abstract class NodoSentencia {
    public abstract void chequear(TablaSimbolos tabla);
}
