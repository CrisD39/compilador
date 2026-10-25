package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Atributo;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;
import Model.semantico.Variable;
import Model.semantico.VariableLocal;

// <For> + <TrasIdForTipo> ::= : <Expresion> (for (T x : iterable)). El
// cuerpo se setea aparte, como en NodoFor. El parser sólo admite tipo
// explícito (no var).
public class NodoForEach extends NodoSentencia {
    private final Token tokenFor;
    private final Tipo tipo;
    private final Token nombre;
    private final NodoExpresion iterable;
    private NodoSentencia cuerpo;

    public NodoForEach(Token tokenFor, Tipo tipo, Token nombre, NodoExpresion iterable) {
        this.tokenFor = tokenFor;
        this.tipo = tipo;
        this.nombre = nombre;
        this.iterable = iterable;
    }

    public void setCuerpo(NodoSentencia cuerpo) {
        this.cuerpo = cuerpo;
    }

    // Logro For: a diferencia de Java, la expresión tiene que conformar con
    // la predefinida MiniIterable<E>, y E con el tipo de la variable. Es lo
    // que hace falta para leer el for como "start(); while (hasNext()) {
    // x = next(); cuerpo }". Por eso un arreglo no se recorre. El tipo de la
    // expresión se mira como MiniIterable con vistaComoAncestro(), así valen
    // las subclases, las interfaces que la extienden y los genéricos
    // (Lista<Persona> con "Lista<E> implements MiniIterable<E>" recorre
    // Persona). Heredado sin argumento (D5), la vista no da un E concreto:
    // queda sin argumento (es error) o con un parámetro de tipo ajeno, que
    // no conforma con ningún tipo escribible.
    // Los errores propios del for-each van sobre "for", como en NodoFor. El
    // tipo de la variable se valida primero, porque es lo primero que
    // aparece en el fuente; el ocultamiento es el de NodoVarLocal (§10.1).
    // La expresión se chequea antes de declarar la variable, que no se ve
    // en ella (en Java tampoco). La variable vive en un scope propio que
    // cubre sólo el cuerpo, y se cierra en el finally (ver NodoFor).
    @Override
    public void chequear(TablaSimbolos tabla) {
        Tipo tipoVariable = tipo.resolverEnCuerpo(tabla);
        Variable previa = tabla.buscarVariable(nombre.getLexema());
        if (previa != null && !(previa instanceof Atributo)) {
            throw new ExcepcionSemantica(nombre, "ERR_LOCAL_DUPLICADA");
        }
        Tipo tipoIterable = iterable.chequear(tabla);
        Tipo vista = tabla.entidadDe(tipoIterable) == null
                ? null : tabla.vistaComoAncestro(tipoIterable, tabla.getMiniIterable());
        if (vista == null) {
            throw new ExcepcionSemantica(tokenFor, "ERR_FOREACH_NO_ITERABLE");
        }
        Tipo elemento = vista.getArgumento();
        if (elemento == null || !elemento.conformaCon(tipoVariable, tabla)) {
            throw new ExcepcionSemantica(tokenFor, "ERR_FOREACH_TIPO_INCOMPATIBLE");
        }
        tabla.abrirBloque();
        try {
            tabla.declararLocal(new VariableLocal(nombre, tipoVariable));
            chequearCuerpo(cuerpo, tabla);
        } finally {
            tabla.cerrarBloque();
        }
    }
}
