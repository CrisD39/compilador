package Model.semantico;

import Model.lexico.Token;
import Model.semantico.ast.NodoBloque;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Constructor implements Chequeable{
    private Token nombre;
    private List<Parametro> parametros;
    // Clase que lo declaró (spec 0003, D4), la setea Clase al registrarlo.
    // Los constructores no se heredan, pero se guarda igual que en Atributo
    // y Metodo para que el recorrido de miembros propios sea uniforme.
    private Clase claseDeclarante;
    // null en el constructor por defecto y hasta que el parser enganche el
    // AST.
    private NodoBloque cuerpo;
    // Logro Visibilidad chequeada: true si se declaró private. Sin
    // modificador o con public es público, como el por defecto. Sólo lo mira el
    // chequeo de cuerpos (ChequeoVisibilidad); la Etapa 3 lo ignora.
    private boolean privado;

    public Constructor(Token nombre, List<Parametro> parametros){
        this.nombre = nombre;
        this.parametros = parametros;
    }

    public Token getNombre() {
        return nombre;
    }

    public List<Parametro> getParams() {
        return parametros;
    }

    public Clase getClaseDeclarante() {
        return claseDeclarante;
    }

    public void setClaseDeclarante(Clase claseDeclarante) {
        this.claseDeclarante = claseDeclarante;
    }

    public boolean isPrivado() {
        return privado;
    }

    public void setPrivado(boolean privado) {
        this.privado = privado;
    }

    public NodoBloque getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(NodoBloque cuerpo) {
        this.cuerpo = cuerpo;
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
