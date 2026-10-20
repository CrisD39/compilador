package Model.semantico;

import Model.lexico.Token;
import Model.lexico.TokenType;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

// Tipo usado en una declaración (atributo, parámetro o retorno). Recursivo:
// el argumento genérico de "idClase<argumento>" es a su vez un Tipo (un idGen
// suelto, u otro idClase con su propio argumento), igual que
// <InstanciadoOParametrico> en la gramática. Un único argumento por nivel
// (REQ-AS-010). Guarda además las dimensiones (int[][] = 2) y, si es un
// parámetro de tipo, su origen. La jerarquía completa (TipoPrimitivo/
// TipoReferencia/TipoGenerico/TipoArreglo, ver "Diseño: jerarquía Tipo" en
// analizador-semantico.md) queda para la Etapa 4.
public class Tipo implements Chequeable {
    private final Token token;
    private final Tipo argumento;
    // Cantidad de "[]" tras el tipo base: int[] e int son tipos distintos al
    // comparar signaturas (historia 42 de la spec 0001). Sólo el tipo de
    // nivel superior las tiene; <InstanciadoOParametrico> no admite arreglos.
    private final int dimensiones;
    // Sólo para un idGen: la clase, interfaz o método que lo declara. Lo
    // resuelve estaBienDeclarado contra el entorno vigente; queda null si el
    // idGen está fuera de alcance (ya se reportó el error) o si el Tipo
    // nunca pasó por el control de declaraciones.
    private OrigenParametroTipo origen;

    public Tipo(Token token) {
        this(token, null, 0);
    }

    public Tipo(Token token, Tipo argumento) {
        this(token, argumento, 0);
    }

    public Tipo(Token token, Tipo argumento, int dimensiones) {
        this.token = token;
        this.argumento = argumento;
        this.dimensiones = dimensiones;
    }

    public Token getToken() {
        return token;
    }

    public Tipo getArgumento() {
        return argumento;
    }

    public int getDimensiones() {
        return dimensiones;
    }

    public boolean esParametroDeTipo() {
        return token.getTipo() == TokenType.ID_GEN;
    }

    public OrigenParametroTipo getOrigen() {
        return origen;
    }

    // Mismo tipo con otra cantidad de dimensiones -- el parser arma primero
    // el tipo base y recién después consume <DimensionesOpcionales>.
    public Tipo conDimensiones(int dimensiones) {
        Tipo copia = new Tipo(token, argumento, dimensiones);
        copia.origen = origen;
        return copia;
    }

    // Un parámetro de tipo ya resuelto, sin pasar por estaBienDeclarado: lo
    // usa la comparación de signaturas para renombrar los parámetros de tipo
    // de un método por los de otro (D6, Metodo.mismaSignatura()).
    static Tipo parametroDeTipo(Token token, OrigenParametroTipo origen) {
        Tipo parametro = new Tipo(token);
        parametro.origen = origen;
        return parametro;
    }

    // Vista de este tipo con algunos parámetros de tipo reemplazados: para
    // cada idGen (a cualquier profundidad), 'reemplazo' devuelve el tipo que
    // va en su lugar, o null para dejarlo como está. Las dimensiones del
    // idGen se suman a las del reemplazo (T[] con T := int es int[]). No
    // modifica este Tipo ni comparte estado mutable con él: devuelve una
    // copia, o this si no cambió nada (§5.1: las vistas no modifican las
    // declaraciones originales). Es la base de la comparación de signaturas:
    // hoy la usa el emparejamiento por posición de D6; la instanciación
    // genérica (ticket 10) sustituye con ella el parámetro de un ancestro
    // por el argumento de la herencia.
    public Tipo reemplazarParametros(Function<Tipo, Tipo> reemplazo) {
        if (esParametroDeTipo()) {
            Tipo nuevo = reemplazo.apply(this);
            return nuevo == null ? this : nuevo.conDimensiones(nuevo.dimensiones + dimensiones);
        }
        if (argumento == null) {
            return this;
        }
        Tipo nuevoArgumento = argumento.reemplazarParametros(reemplazo);
        if (nuevoArgumento == argumento) {
            return this;
        }
        Tipo copia = new Tipo(token, nuevoArgumento, dimensiones);
        copia.origen = origen;
        return copia;
    }

    // true si algún idGen de este tipo (a cualquier profundidad) cumple
    // 'condicion'.
    public boolean contieneParametro(Predicate<Tipo> condicion) {
        if (esParametroDeTipo() && condicion.test(this)) {
            return true;
        }
        return argumento != null && argumento.contieneParametro(condicion);
    }

    // Logro 5: cada nivel se valida contra la tabla (idClase) o contra el
    // entorno de genéricos vigente (idGen). El argumento se recorre aunque
    // la cabeza falle, para reportar todos los errores en la misma corrida
    // (Logro 4). Un raw type (Caja sin <...> siendo genérica) se acepta,
    // como en Java. De paso, un idGen en alcance queda con su origen
    // resuelto: el entorno ya trae quién declara cada nombre, con el
    // shadowing aplicado (el método pisa al contenedor).
    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        if (token.getTipo() == TokenType.ID_GEN) {
            origen = entornoGenerico.get(token.getLexema());
            if (origen == null) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_TIPO_GENERICO_NO_DECLARADO", token.getLinea(), token.getLexema()));
            }
        } else if (token.getTipo() == TokenType.ID_CLASE) {
            EntidadDeclarada entidad = tabla.buscarClase(token.getLexema());
            if (entidad == null) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_TIPO_NO_DECLARADO", token.getLinea(), token.getLexema()));
            } else if (argumento != null && entidad.getParametrosTipo().isEmpty()) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_TIPO_NO_GENERICO", token.getLinea(), token.getLexema()));
            }
        }
        if (argumento != null) {
            argumento.estaBienDeclarado(tabla, entornoGenerico);
        }
    }

    // Igualdad estructural, la que usa la comparación de signaturas: misma
    // cabeza (lexema y categoría), mismo argumento (recursivo), mismas
    // dimensiones y, en un parámetro de tipo, mismo origen (por identidad:
    // el T de D no es el T de Base). Los parámetros de tipo de MÉTODO se
    // emparejan por posición al comparar dos signaturas (D6); eso lo resuelve
    // quien compara, no esta igualdad.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tipo otro)) {
            return false;
        }
        return token.getTipo() == otro.token.getTipo()
                && token.getLexema().equals(otro.token.getLexema())
                && dimensiones == otro.dimensiones
                && Objects.equals(argumento, otro.argumento)
                && origen == otro.origen;
    }

    // Sin el origen: se resuelve después de construir el Tipo, y el hash no
    // puede cambiar si el Tipo ya está en un HashSet/HashMap. Iguales siguen
    // teniendo el mismo hash.
    @Override
    public int hashCode() {
        return Objects.hash(token.getTipo(), token.getLexema(), dimensiones, argumento);
    }

    // Forma legible (Caja<T>[]) para descripciones de error y depuración.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(token.getLexema());
        if (argumento != null) {
            sb.append('<').append(argumento).append('>');
        }
        sb.append("[]".repeat(dimensiones));
        return sb.toString();
    }
}
