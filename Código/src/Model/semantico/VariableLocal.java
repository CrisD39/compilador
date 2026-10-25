package Model.semantico;

import Model.lexico.Token;

// Local declarada con 'var' (o, como logro, con tipo explícito). Vive en el
// mapa de su bloque mientras el bloque está abierto (TablaSimbolos.bloques).
public class VariableLocal implements Variable {
    private final Token nombre;
    private final Tipo tipo;

    public VariableLocal(Token nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    // Una local cuyo var falló (inicializador con error, null o void): el
    // nombre queda declarado, pero sin tipo. Usarla corta la sentencia sin
    // otro error (NodoAccesoVar, ExcepcionSemantica.enCascada()).
    public static VariableLocal sinTipo(Token nombre) {
        return new VariableLocal(nombre, null);
    }

    public boolean esSinTipo() {
        return tipo == null;
    }

    @Override
    public Token getNombre() {
        return nombre;
    }

    @Override
    public Tipo getTipo() {
        return tipo;
    }
}
