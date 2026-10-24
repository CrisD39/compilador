package Model.semantico;

import Model.lexico.Token;
import Model.lexico.TokenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

// Tipo usado en una declaración (atributo, parámetro o retorno). Recursivo:
// el argumento genérico de "idClase<argumento>" es a su vez un Tipo (un idGen
// suelto, u otro idClase con su propio argumento), igual que
// <InstanciadoOParametrico> en la gramática. Un único argumento por nivel
// (REQ-AS-010). Guarda además las dimensiones (int[][] = 2) y, si es un
// parámetro de tipo, su origen. La jerarquía completa (TipoPrimitivo/
// TipoReferencia/TipoGenerico/TipoArreglo, ver "Diseño: jerarquía Tipo" en
// analizador-semantico.md) queda para la Etapa 4.
public class Tipo implements Chequeable {
    // Tipos fijos del chequeo de cuerpos (spec 0003, D5): los de los
    // literales, el del literal null y el resultado de llamar a un método
    // void (Metodo sigue representando void con tipoRetorno == null). Tokens
    // sintéticos en la línea 0, como las predefinidas. Se pueden compartir:
    // ninguno pasa por estaBienDeclarado(), el único que muta un Tipo.
    public static final Tipo INT = sintetico(TokenType.PR_INT, "int");
    public static final Tipo CHAR = sintetico(TokenType.PR_CHAR, "char");
    public static final Tipo BOOLEAN = sintetico(TokenType.PR_BOOLEAN, "boolean");
    public static final Tipo STRING = sintetico(TokenType.ID_CLASE, "String");
    public static final Tipo NULL = sintetico(TokenType.PR_NULL, "null");
    public static final Tipo VOID = sintetico(TokenType.PR_VOID, "void");

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

    private static Tipo sintetico(TokenType tipo, String lexema) {
        return new Tipo(new Token(tipo, lexema, 0));
    }

    public boolean esParametroDeTipo() {
        return token.getTipo() == TokenType.ID_GEN;
    }

    public boolean esPrimitivo() {
        TokenType tipo = token.getTipo();
        return dimensiones == 0
                && (tipo == TokenType.PR_INT || tipo == TokenType.PR_CHAR || tipo == TokenType.PR_BOOLEAN);
    }

    public boolean esArreglo() {
        return dimensiones > 0;
    }

    // Clases, interfaces, parámetros de tipo y arreglos (§9): lo que puede
    // valer null. No incluye al tipo del literal null ni a void.
    public boolean esReferencia() {
        TokenType tipo = token.getTipo();
        return esArreglo() || tipo == TokenType.ID_CLASE || tipo == TokenType.ID_GEN;
    }

    public boolean esVoid() {
        return token.getTipo() == TokenType.PR_VOID;
    }

    public boolean esNull() {
        return token.getTipo() == TokenType.PR_NULL;
    }

    // Conformidad S.conformaCon(D) (spec 0003, D9; §9, §11.2): si un valor
    // de este tipo puede ir donde se espera 'destino'. Sin conversiones entre
    // primitivos (char no conforma con int, §9.1), y void no conforma con
    // nada, ni con void: una llamada void no produce valor (§9.5). null
    // conforma con cualquier referencia y, como en Java (ADR 0004), con su
    // propio tipo: null == null es válido.
    // Una clase o interfaz conforma con sí misma y con sus ancestros por
    // extends/implements, transitivo y hasta Object (§9.2): B2 va donde se
    // espera A1, pero no al revés (semIIError37), y una interfaz no va donde
    // se espera la clase que la implementa (semIIError38).
    // Los arreglos son invariantes: tipo base y dimensiones iguales, así
    // String[] no va donde se espera Object[] (semIIError44) ni int[][]
    // donde se espera int[] (semIIError45). Todo arreglo conforma además con
    // Object (§9.4, semIICorrecto15).
    // Los argumentos genéricos son invariantes (§9.3, D9): este tipo visto
    // como el ancestro tiene que ser igual al destino, argumento incluido.
    // Caja<String> no conforma con Caja<Object> (semIIError40), y una
    // subclase de Base<String> conforma con Base<String> y no con
    // Base<Object>. Un parámetro de tipo no es clase ni interfaz: sólo
    // conforma con el mismo T (mismo origen), no con Object ni con String
    // (semIIError41-43).
    public boolean conformaCon(Tipo destino, TablaSimbolos tabla) {
        if (esVoid() || destino.esVoid()) {
            return false;
        }
        if (esNull()) {
            return destino.esNull() || destino.esReferencia();
        }
        if (esArreglo()) {
            return equals(destino) || tabla.entidadDe(destino) == tabla.getObject();
        }
        EntidadDeclarada entidad = tabla.entidadDe(this);
        EntidadDeclarada ancestro = tabla.entidadDe(destino);
        if (entidad == null || ancestro == null) {
            return equals(destino);
        }
        return destino.equals(tabla.vistaComoAncestro(this, ancestro));
    }

    // Un tipo escrito en un cuerpo (new Caja<T>(), new T[10]) no pasa por
    // estaBienDeclarado(): se valida al chequear la expresión, con las reglas
    // de la Etapa 3 y sus códigos, pero lanzando en vez de agregar el error
    // (D7). Devuelve una copia con el origen de cada idGen resuelto, que es lo
    // que necesitan equals() y sustituir(). La cabeza se valida antes que el
    // argumento, así el error informado es el primero del fuente.
    public Tipo resolverEnCuerpo(TablaSimbolos tabla) {
        if (esParametroDeTipo()) {
            Tipo copia = conDimensiones(dimensiones);
            copia.origen = origenEnCuerpo(tabla);
            return copia;
        }
        if (token.getTipo() == TokenType.ID_CLASE) {
            EntidadDeclarada entidad = tabla.buscarClase(token.getLexema());
            if (entidad == null) {
                throw new ExcepcionSemantica(token, "ERR_TIPO_NO_DECLARADO");
            }
            boolean esGenerica = !entidad.getParametrosTipo().isEmpty();
            if (esGenerica && argumento == null) {
                throw new ExcepcionSemantica(token, "ERR_TIPO_SIN_ARGUMENTO");
            }
            if (!esGenerica && argumento != null) {
                throw new ExcepcionSemantica(token, "ERR_TIPO_NO_GENERICO");
            }
        }
        if (argumento == null) {
            return this;
        }
        return new Tipo(token, argumento.resolverEnCuerpo(tabla), dimensiones);
    }

    // Quién declara este idGen dentro del cuerpo actual: el método, si es uno
    // de sus parámetros de tipo propios (pisan a los de la clase, como en
    // estaBienDeclarado() de Metodo), o la clase. Un estático no ve los de la
    // clase (ADR 0006, §10.4): el T de Caja en new T[10] dentro de un static
    // es ERR_PARAMETRO_TIPO_EN_ESTATICO (semIIError32), no "no declarado".
    private OrigenParametroTipo origenEnCuerpo(TablaSimbolos tabla) {
        String nombre = token.getLexema();
        Metodo metodo = tabla.getMetodoActual();
        if (metodo != null && declara(metodo.getParametrosTipoPropios(), nombre)) {
            return metodo;
        }
        Clase clase = tabla.getClaseActual();
        if (declara(clase.getParametrosTipo(), nombre)) {
            if (tabla.enContextoEstatico()) {
                throw new ExcepcionSemantica(token, "ERR_PARAMETRO_TIPO_EN_ESTATICO");
            }
            return clase;
        }
        throw new ExcepcionSemantica(token, "ERR_TIPO_GENERICO_NO_DECLARADO");
    }

    private static boolean declara(List<Token> parametrosTipo, String nombre) {
        return parametrosTipo.stream().anyMatch(parametro -> parametro.getLexema().equals(nombre));
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
    // la usan el emparejamiento por posición de D6 y la instanciación
    // genérica (sustituir()).
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

    // Instanciación genérica (spec 0001, "Instanciación genérica"): este
    // tipo visto desde un descendiente, con cada parámetro de tipo de un
    // ancestro reemplazado por el argumento con que ese descendiente lo
    // hereda. 'instanciacion' es el mapa ancestro -> argumento (ver
    // componerInstanciacion()); como cada tipo declara a lo sumo un
    // parámetro (REQ-AS-010), el origen alcanza para identificarlo. Un
    // parámetro cuyo origen no tiene entrada (herencia sin argumento, o un
    // parámetro de método) queda como está: no se borra a Object (D5, §5).
    // Es una vista para comparar; no modifica este Tipo.
    public Tipo sustituir(Map<OrigenParametroTipo, Tipo> instanciacion) {
        if (instanciacion.isEmpty()) {
            return this;
        }
        return reemplazarParametros(parametro -> instanciacion.get(parametro.getOrigen()));
    }

    // Mapa "ancestro -> argumento visto desde acá" de un tipo que hereda de
    // 'supertipo' escribiendo 'argumento' (el de "extends Base<String>";
    // null si hereda sin argumento). Se compone a partir del mapa ya
    // calculado del supertipo ('delSupertipo'): cada argumento que el
    // supertipo da a SUS ancestros puede nombrar el parámetro del propio
    // supertipo, y ese se reemplaza por 'argumento'. Así la cadena
    // "Final extends Intermedia<String>", "Intermedia<U> extends Base<U>"
    // deja Base -> String e Intermedia -> String (semCorrecto26). Sin
    // argumento no se agrega la entrada del supertipo, y lo que el
    // supertipo pasaba en función de su parámetro sigue nombrándolo (D5).
    // Lo usa Clase al consolidar su 'extends'; el contrato de una interfaz
    // (ticket 11) puede armar el suyo igual, por 'implements' y por el
    // 'extends' de una interfaz.
    public static Map<OrigenParametroTipo, Tipo> componerInstanciacion(
            Map<OrigenParametroTipo, Tipo> delSupertipo, OrigenParametroTipo supertipo, Tipo argumento) {
        Map<OrigenParametroTipo, Tipo> propio = new HashMap<>();
        if (argumento != null) {
            propio.put(supertipo, argumento);
        }
        Map<OrigenParametroTipo, Tipo> compuesto = new HashMap<>(propio);
        for (Map.Entry<OrigenParametroTipo, Tipo> entrada : delSupertipo.entrySet()) {
            compuesto.put(entrada.getKey(), entrada.getValue().sustituir(propio));
        }
        return compuesto;
    }

    // Logro 5: cada nivel se valida contra la tabla (idClase) o contra el
    // entorno de genéricos vigente (idGen). El argumento se recorre aunque
    // la cabeza falle, para reportar todos los errores en la misma corrida
    // (Logro 4). Un tipo raw (Caja sin <...> siendo genérica) es error
    // (§5.2, ADR 0007): ésta es la validación de los tipos de atributos,
    // parámetros, retornos y arreglos. De paso, un idGen en alcance queda
    // con su origen resuelto: el entorno ya trae quién declara cada nombre,
    // con el shadowing aplicado (el método pisa al contenedor).
    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        validar(tabla, entornoGenerico, false);
    }

    // Validación del supertipo de un 'extends'/'implements': única
    // excepción a la regla de los tipos raw, porque heredar sin argumento
    // conserva el parámetro del padre (§5). La excepción vale sólo para la
    // cabeza: en "extends Caja<Lista>" el argumento Lista sigue siendo raw.
    public void estaBienDeclaradoComoSupertipo(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        validar(tabla, entornoGenerico, true);
    }

    private void validar(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico,
                         boolean admiteSinArgumento) {
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
            } else if (argumento == null && !admiteSinArgumento && !entidad.getParametrosTipo().isEmpty()) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_TIPO_SIN_ARGUMENTO", token.getLinea(), token.getLexema()));
            }
        }
        if (argumento != null) {
            argumento.validar(tabla, entornoGenerico, false);
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
