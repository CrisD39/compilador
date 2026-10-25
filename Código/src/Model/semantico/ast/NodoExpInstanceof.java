package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Interfaz;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <ExpresionCompuestaResto> ::= instanceof <TipoReferencia> <ExpresionCompuestaResto>
// Logro Instanceof. Entra en la misma cadena sin precedencia que los
// operadores binarios: a instanceof B && c es (a instanceof B) && c. El
// destino es un tipo y no una expresión. La gramática sólo admite un
// idClase, así que T, los arreglos y la variable asociada (a instanceof B b)
// ya son errores sintácticos. El <...> sí se parsea, para rechazarlo acá.
public class NodoExpInstanceof extends NodoExpCompuesta {
    private final NodoExpresion ladoIzq;
    private final Token operador;
    private final Tipo tipoDestino;

    public NodoExpInstanceof(NodoExpresion ladoIzq, Token operador, Tipo tipoDestino) {
        this.ladoIzq = ladoIzq;
        this.operador = operador;
        this.tipoDestino = tipoDestino;
    }

    // Como en Java, la prueba tiene que poder dar true alguna vez: el
    // operando es una referencia (null incluido) y los dos tipos están
    // relacionados. No hace falta que sean iguales, para eso existe el
    // operador. A diferencia de Java, el destino no puede ser genérico, ni
    // con argumento (Caja<A1>) ni raw (Caja).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Tipo origen = ladoIzq.chequear(tabla);
        if (!origen.esReferencia() && !origen.esNull()) {
            throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
        }
        EntidadDeclarada destino = tabla.entidadDe(tipoDestino);
        if (destino == null) {
            throw new ExcepcionSemantica(tipoDestino.getToken(), "ERR_TIPO_NO_DECLARADO");
        }
        if (tipoDestino.getArgumento() != null || !destino.getParametrosTipo().isEmpty()) {
            throw new ExcepcionSemantica(operador, "ERR_INSTANCEOF_GENERICO");
        }
        if (!sonCompatibles(origen, destino, tabla)) {
            throw new ExcepcionSemantica(operador, "ERR_OPERADOR_INCOMPATIBLE");
        }
        return Tipo.BOOLEAN;
    }

    // La conversión por cast de Java entre referencias. Vale bajar (A1 a su
    // subclase B2) y subir (B2 a A1). null y un T, que se borra a Object,
    // pueden probarse contra cualquier clase. Un arreglo sólo contra Object,
    // que es lo único con lo que conforma.
    private boolean sonCompatibles(Tipo origen, EntidadDeclarada destino, TablaSimbolos tabla) {
        if (origen.esNull() || origen.esParametroDeTipo()) {
            return true;
        }
        if (origen.conformaCon(tipoDestino, tabla) || tipoDestino.conformaCon(origen, tabla)) {
            return true;
        }
        EntidadDeclarada entidadOrigen = tabla.entidadDe(origen);
        return entidadOrigen != null && puedenTenerSubtipoComun(entidadOrigen, destino);
    }

    // Una interfaz y una clase que no es final siempre pueden tener un
    // subtipo común (una subclase que implemente la interfaz), aunque hoy
    // no exista: Java las acepta, y dos interfaces también. Una clase final
    // no tiene más subtipos, así que con ella vale sólo la conformidad (de
    // ahí que un String no pueda probarse contra una interfaz propia). No se
    // sigue la regla de Java 17 que además usa sealed/permits para probar
    // que dos tipos son disjuntos.
    private static boolean puedenTenerSubtipoComun(EntidadDeclarada a, EntidadDeclarada b) {
        if (a instanceof Interfaz && b instanceof Interfaz) {
            return true;
        }
        if (a instanceof Interfaz && b instanceof Clase clase) {
            return !clase.isFinal();
        }
        if (b instanceof Interfaz && a instanceof Clase clase) {
            return !clase.isFinal();
        }
        return false;
    }
}
