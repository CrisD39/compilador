package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <SentSuper> ::= <ArgsActuales> ;   (logro Super!, spec 0006, D4)
// Llamada al constructor de la superclase. Es una sentencia y no una
// expresión (ADR 0010): no produce un valor. En el flujo termina
// normalmente, como una sentencia expresión (salida() por defecto), y su
// primer token, el de la sentencia inalcanzable, es super.
public class NodoLlamadaSuperConstructor extends NodoSentencia {
    private final Token tokenSuper;
    private final List<NodoExpresion> argumentos;
    // D7: lo marca Constructor.setCuerpo() si es literalmente la primera
    // sentencia de su cuerpo. La ubicación sale del AST y no de un estado de
    // la tabla, así una segunda llamada, una anidada o una en un método
    // quedan sin marcar.
    private boolean enLugar;

    public NodoLlamadaSuperConstructor(Token tokenSuper, List<NodoExpresion> argumentos) {
        this.tokenSuper = tokenSuper;
        this.argumentos = argumentos;
    }

    public void marcarEnLugar() {
        enLugar = true;
    }

    @Override
    public Token primerToken() {
        return tokenSuper;
    }

    // D7: fuera de lugar es error antes de mirar los argumentos; en un método
    // estático gana este error y no el de contexto estático. D8: en lugar,
    // hace lo mismo que new sobre la clase de la superclase, con super en el
    // papel del nombre de la clase. Los parámetros se ven desde el extends
    // tal como se escribió: con extends A<String>, A(T t) es A(String t). La
    // superclase existe y es una clase porque, si no, la Etapa 3 hubiera dado
    // error y este chequeo no correría (D5); sin extends es Object, que sólo
    // tiene el constructor sin parámetros.
    @Override
    public void chequear(TablaSimbolos tabla) {
        if (!enLugar) {
            throw new ExcepcionSemantica(tokenSuper, "ERR_SUPER_FUERA_DE_LUGAR");
        }
        Tipo tipoSuperclase = tabla.getClaseActual().getTipoSuperclase();
        Clase superclase = (Clase) tabla.buscarClase(tipoSuperclase.getToken().getLexema());
        ChequeoLlamada.chequearConstructor(tabla, superclase, tipoSuperclase, argumentos, tokenSuper);
    }
}
