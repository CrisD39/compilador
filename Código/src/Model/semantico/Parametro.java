package Model.semantico;

import Model.lexico.Token;

import java.util.Map;

public class Parametro implements Chequeable, Variable {
    private Token nombre;
    private Tipo tipo;

    public Parametro(Token nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    @Override
    public Token getNombre(){
        return nombre;
    }

    @Override
    public Tipo getTipo(){
        return tipo;
    }

    @Override
    // 'tipo' es null si faltaba el tipo y hubo un error sintáctico ("int a, )"):
    // tipo() devuelve null en ese caso, y consolidar() corre igual aunque
    // haya errores sintácticos. No hay nada que validar, y el error ya se
    // reportó (semError90). Mismo criterio que Tipo con un argumento null.
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico){
        if (tipo == null) {
            return;
        }
        tipo.estaBienDeclarado(tabla, entornoGenerico);
    }
}
