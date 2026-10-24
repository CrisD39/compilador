package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Atributo;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;
import Model.semantico.Variable;
import Model.semantico.VariableLocal;

// <VarLocal> ::= var idMetVar = <ExpresionCompuesta>
// El "var" es el lexema de ERR_DECLARACION_COMO_CUERPO (§12.3).
public class NodoVarLocal extends NodoSentencia {
    private final Token var;
    private final Token nombre;
    private final NodoExpresion inicializador;

    public NodoVarLocal(Token var, Token nombre, NodoExpresion inicializador) {
        this.var = var;
        this.nombre = nombre;
        this.inicializador = inicializador;
    }

    public Token getVar() {
        return var;
    }

    // §12.3. El inicializador se chequea antes de declarar, así var x = x no
    // se ve a sí mismo (semIIError90); si falla, la local no se registra
    // (D6). Ocultamiento (§10.1): el nombre no puede ser el de una local
    // visible (de este bloque o de uno contenedor) ni el de un parámetro,
    // pero sí el de un atributo. Después, null y void no dan un tipo para
    // inferir. Los dos errores van sobre el nombre.
    @Override
    public void chequear(TablaSimbolos tabla) {
        Tipo tipo = inicializador.chequear(tabla);
        Variable previa = tabla.buscarVariable(nombre.getLexema());
        if (previa != null && !(previa instanceof Atributo)) {
            throw new ExcepcionSemantica(nombre, "ERR_LOCAL_DUPLICADA");
        }
        if (tipo.esNull() || tipo.esVoid()) {
            throw new ExcepcionSemantica(nombre, "ERR_VAR_SIN_TIPO");
        }
        tabla.declararLocal(new VariableLocal(nombre, tipo));
    }
}
