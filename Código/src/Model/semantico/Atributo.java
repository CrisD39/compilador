package Model.semantico;

import Model.lexico.Token;

import java.util.Map;

public class Atributo implements Chequeable {
    private Token nombre;
    private Tipo tipo;

    public Atributo(Token nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Token getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    // Clave del atributo: el nombre (los atributos no se sobrecargan).
    public String getClave() {
        return nombre.getLexema();
    }

    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        tipo.estaBienDeclarado(tabla, entornoGenerico);
    }
}
