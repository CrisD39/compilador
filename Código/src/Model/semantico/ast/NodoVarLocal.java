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
    // se ve a sí mismo (semIIError90). Ocultamiento (§10.1): el nombre no
    // puede ser el de una local visible (de este bloque o de uno contenedor)
    // ni el de un parámetro, pero sí el de un atributo. Después, null y void
    // no dan un tipo para inferir. Los dos errores van sobre el nombre.
    // Si el inicializador falla o no da tipo, la local se declara igual, sin
    // tipo: sus usos posteriores no suman un "no declarada" cada uno
    // (semIIMultiErrorError03). Si el nombre ya era una local o un
    // parámetro, sigue valiendo esa y no se declara nada.
    @Override
    public void chequear(TablaSimbolos tabla) {
        Tipo tipo;
        try {
            tipo = inicializador.chequear(tabla);
        } catch (ExcepcionSemantica e) {
            declararSinTipoSiEsNueva(tabla);
            throw e;
        }
        Variable previa = tabla.buscarVariable(nombre.getLexema());
        if (previa != null && !(previa instanceof Atributo)) {
            throw new ExcepcionSemantica(nombre, "ERR_LOCAL_DUPLICADA");
        }
        if (tipo.esNull() || tipo.esVoid()) {
            tabla.declararLocal(VariableLocal.sinTipo(nombre));
            throw new ExcepcionSemantica(nombre, "ERR_VAR_SIN_TIPO");
        }
        tabla.declararLocal(new VariableLocal(nombre, tipo));
    }

    private void declararSinTipoSiEsNueva(TablaSimbolos tabla) {
        Variable previa = tabla.buscarVariable(nombre.getLexema());
        if (previa == null || previa instanceof Atributo) {
            tabla.declararLocal(VariableLocal.sinTipo(nombre));
        }
    }
}
