package Model.semantico.ast;

import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// Raíz de las expresiones del AST: chequear() devuelve el tipo de la
// expresión, o lanza ExcepcionSemantica.
public abstract class NodoExpresion {
    public abstract Tipo chequear(TablaSimbolos tabla);
}
