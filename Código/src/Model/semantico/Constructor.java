package Model.semantico;

import Model.lexico.Token;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Constructor implements Chequeable{
    private Token nombre;
    private List<Parametro> parametros;

    public Constructor(Token nombre, List<Parametro> parametros){
        this.nombre = nombre;
        this.parametros = parametros;
    }

    public Token getNombre() {
        return nombre;
    }

    // Clave del constructor: la aridad (D4, mismo criterio que
    // Metodo.getClave()). El nombre no participa porque todos los
    // constructores de una clase se llaman igual; Punto(int) y Punto(char)
    // tienen la misma clave y son un constructor duplicado (semError76).
    public String getClave() {
        return String.valueOf(parametros.size());
    }

    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        Set<String> nombresVisibles = new HashSet<>();
        for (Parametro parametro : parametros) {
            if (!nombresVisibles.add(parametro.getNombre().getLexema())) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_PARAMETRO_DUPLICADO", parametro.getNombre().getLinea(), parametro.getNombre().getLexema()));
            }
            parametro.estaBienDeclarado(tabla, entornoGenerico);
        }
    }
}
