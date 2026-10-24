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

    @Override
    public Token getNombre() {
        return nombre;
    }

    @Override
    public Tipo getTipo() {
        return tipo;
    }
}
