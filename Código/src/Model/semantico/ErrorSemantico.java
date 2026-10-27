package Model.semantico;

// Error semántico de "chequeo de declaraciones". Internamente se clasifica con
// un código fijo por regla violada (ERR_*, ver casos-de-prueba.md), que sirve
// para documentar los casos y elegir la descripción legible; hacia afuera se
// reporta con el formato de la cátedra, igual que léxico y sintáctico:
//   Error Semántico en línea <L>: <descripción>
//   [Error:<lexema>|<L>]
// donde lexema y línea salen del MISMO token (la entidad que dispara el error).
public final class ErrorSemantico {

    private final String codigo;
    private final int linea;
    private final String lexema;
    // Dato extra para la descripción cuando un mismo código cubre variantes
    // que el lexema solo no distingue. Hoy sólo lo usa
    // ERR_HERENCIA_TIPO_INCORRECTO (D1): qué se esperaba en la relación,
    // "una clase" o "una interfaz". null en el resto. Nunca va en la
    // etiqueta [Error:...].
    private final String detalle;

    public ErrorSemantico(String codigo, int linea, String lexema) {
        this(codigo, linea, lexema, null);
    }

    public ErrorSemantico(String codigo, int linea, String lexema, String detalle) {
        this.codigo = codigo;
        this.linea = linea;
        this.lexema = lexema;
        this.detalle = detalle;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getLinea() {
        return linea;
    }

    // Lexema de la entidad que dispara el error (va en la etiqueta [Error:...]).
    public String getLexema() {
        return lexema;
    }

    // Texto legible del error, análogo a ErrorLexico.getRazon(). Un código sin
    // descripción cae en el código mismo, así un ERR_* nuevo nunca se reporta
    // vacío aunque se olvide agregarlo acá.
    public String getDescripcion() {
        String x = "\"" + lexema + "\"";
        return switch (codigo) {
            case "ERR_CLASE_DUPLICADA" -> "ya existe una clase o interfaz llamada " + x;
            case "ERR_INTERFAZ_DUPLICADA" -> "la interfaz " + x + " está repetida en la lista de supertipos";
            case "ERR_ATRIBUTO_DUPLICADO" -> "el atributo " + x + " ya está declarado o heredado";
            case "ERR_METODO_DUPLICADO" -> "el método " + x + " ya está declarado con la misma cantidad de parámetros";
            case "ERR_CONSTRUCTOR_DUPLICADO" -> "ya existe un constructor de " + x + " con la misma cantidad de parámetros";
            case "ERR_CONSTRUCTOR_NOMBRE_INVALIDO" -> "el constructor " + x + " no tiene el nombre de su clase";
            case "ERR_PARAMETRO_DUPLICADO" -> "el parámetro " + x + " está repetido";
            case "ERR_TIPO_NO_DECLARADO" -> "el tipo " + x + " no está declarado";
            case "ERR_TIPO_GENERICO_NO_DECLARADO" -> "el parámetro de tipo " + x + " no está declarado en este alcance";
            case "ERR_TIPO_NO_GENERICO" -> "el tipo " + x + " no es genérico y no admite argumentos de tipo";
            case "ERR_TIPO_SIN_ARGUMENTO" -> "el tipo genérico " + x + " necesita un argumento de tipo";
            case "ERR_HERENCIA_CICLICA" -> x + " forma parte de un ciclo de herencia";
            case "ERR_HERENCIA_CLASE_FINAL" -> x + " no puede extender una clase final";
            case "ERR_HERENCIA_NO_PERMITIDA" -> x + " no figura en el permits de su supertipo sealed";
            case "ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR" ->
                    x + " extiende un tipo sealed y debe declararse final, sealed o non-sealed";
            case "ERR_NONSEALED_SIN_PADRE_SEALED" -> x + " es non-sealed pero no tiene un supertipo sealed";
            case "ERR_PERMITS_NO_ES_SUBTIPO" -> x + " figura en el permits pero no es subtipo directo";
            case "ERR_METODO_REDEFINE_FINAL" -> "el método " + x + " redefine un método final heredado";
            // Cubre la redefinición, el contrato de interfaz y una interfaz
            // contra Object: "heredado" solo no alcanzaba para los dos últimos.
            case "ERR_REDEFINICION_INCOMPATIBLE" ->
                    "el método " + x + " redefine o implementa un método con otra signatura";
            case "ERR_REDEFINE_ESTATICO" ->
                    "el método " + x + " tiene la clave de un método heredado y uno de los dos es estático";
            case "ERR_METODO_INTERFAZ_NOIMPLEMENTADO" -> "el método " + x + " de la interfaz no está implementado";
            case "ERR_METODO_RETORNO_INCOMPATIBLE" -> "el método " + x + " se hereda con signaturas incompatibles";
            case "ERR_HERENCIA_ARGUMENTOS_DISTINTOS" ->
                    x + " hereda un mismo tipo genérico con argumentos distintos";
            case "ERR_HERENCIA_TIPO_INCORRECTO" ->
                    x + " no es " + (detalle != null ? detalle : "del tipo esperado en la relación");
            // Chequeo de cuerpos (spec 0003). El lexema sigue la convención
            // de etapa4-semantico2/casos-de-prueba.md.
            case "ERR_VAR_NO_DECLARADA" -> "la variable " + x + " no está declarada";
            case "ERR_ATRIBUTO_EN_ESTATICO" -> "el atributo " + x + " no se puede usar en un método estático";
            case "ERR_REFERENCIA_ADELANTADA" -> "el atributo " + x + " se usa en un inicializador antes de su declaración";
            case "ERR_THIS_EN_ESTATICO" -> "this no se puede usar en un método estático";
            // Logro Super! (spec 0006, D6): mismo control que this.
            case "ERR_SUPER_EN_ESTATICO" -> "no se puede acceder a super desde un contexto estático";
            case "ERR_SUPER_FUERA_DE_LUGAR" ->
                    "la llamada al constructor de la superclase debe ser la primera sentencia de un constructor";
            case "ERR_REFERENCIA_ANTES_DE_SUPER" ->
                    "no se puede usar el objeto actual antes de llamar al constructor de la superclase";
            case "ERR_PARAMETRO_TIPO_EN_ESTATICO" ->
                    "el parámetro de tipo " + x + " de la clase no se puede usar en un método estático";
            case "ERR_ATRIBUTO_NO_ENCONTRADO" -> "el atributo " + x + " no existe en el tipo del receptor";
            case "ERR_RECEPTOR_SIN_MIEMBROS" ->
                    "no se puede seleccionar " + x + ": el receptor no es de una clase ni de una interfaz";
            case "ERR_METODO_NO_ENCONTRADO" -> "no existe un método " + x + " con esa cantidad de argumentos";
            case "ERR_METODO_NO_ESTATICO" -> "el método " + x + " no es estático y no hay un objeto receptor";
            // Con super (spec 0006, D8) el lexema hace de nombre de la clase
            // de new, y el texto nombra al constructor de la superclase.
            case "ERR_ARGUMENTO_INCOMPATIBLE" -> esSuper()
                    ? "los argumentos de super(...) no conforman con los parámetros del constructor de la superclase"
                    : "los argumentos de " + x + " no conforman con los tipos de sus parámetros";
            case "ERR_NEW_INTERFAZ" -> "no se puede crear un objeto de la interfaz " + x;
            case "ERR_CONSTRUCTOR_NO_ENCONTRADO" -> esSuper()
                    ? "la superclase no tiene un constructor con esa cantidad de argumentos"
                    : "la clase " + x + " no tiene un constructor con esa cantidad de argumentos";
            case "ERR_ASIGNACION_INCOMPATIBLE" -> "el valor asignado no conforma con el tipo del destino";
            case "ERR_DESTINO_NO_ASIGNABLE" -> "el lado izquierdo de " + x + " no es un destino asignable";
            case "ERR_OPERADOR_INCOMPATIBLE" -> "el operador " + x + " no admite los tipos de sus operandos";
            case "ERR_INDICE_NO_ENTERO" -> "el índice o el tamaño entre " + x + " y \"]\" tiene que ser int";
            case "ERR_NO_ES_ARREGLO" -> "sólo se puede indexar con " + x + " una expresión de tipo arreglo";
            case "ERR_SENTENCIA_INVALIDA" ->
                    "la expresión no es una sentencia válida (asignación, llamada, creación de objeto o postfijo)";
            case "ERR_LOCAL_DUPLICADA" ->
                    "la variable local " + x + " repite el nombre de otra local visible o de un parámetro";
            case "ERR_VAR_SIN_TIPO" -> "no se puede inferir el tipo de " + x + " a partir de null o de void";
            case "ERR_DECLARACION_COMO_CUERPO" ->
                    "una declaración local no puede ser el cuerpo directo de if, else, while o for";
            case "ERR_CONDICION_NO_BOOLEAN" -> "la condición de " + x + " tiene que ser boolean";
            // Logro For.
            case "ERR_FOR_INICIALIZACION" ->
                    "la inicialización del " + x + " tiene que ser una asignación o una declaración var";
            case "ERR_FOR_ACTUALIZACION" -> "la actualización del " + x + " tiene que ser una asignación";
            case "ERR_FOREACH_NO_ITERABLE" -> "la expresión del " + x + " no conforma con MiniIterable";
            case "ERR_FOREACH_TIPO_INCOMPATIBLE" ->
                    "los elementos que recorre el " + x + " no conforman con el tipo de su variable";
            // En new, x es el nombre de la clase, cuyo constructor es el
            // private; con super, el constructor es el de la superclase (un
            // acceso con super lo reporta sobre el nombre del miembro).
            case "ERR_ACCESO_PRIVADO" -> esSuper()
                    ? "no se puede llamar con super(...) al constructor de la superclase: es private y sólo se usa dentro de su clase"
                    : "no se puede acceder a " + x + ": es private y sólo se usa dentro de su clase";
            case "ERR_INSTANCEOF_GENERICO" -> "el tipo a la derecha de " + x + " no puede ser genérico";
            case "ERR_RETORNO_INCORRECTO" -> "el " + x + " no corresponde con el tipo de retorno de la unidad";
            // Logro Caminos de retorno (spec 0004).
            case "ERR_FALTA_RETORNO" -> "el método " + x + " no retorna un valor en todos sus caminos";
            case "ERR_SENTENCIA_INALCANZABLE" -> "la sentencia que empieza en " + x + " es inalcanzable";
            // Lo usan los nodos de logro (D11), que todavía no se chequean.
            case "ERR_NO_SOPORTADO" -> "la construcción en " + x + " todavía no se soporta";
            default -> codigo + " (" + x + ")";
        };
    }

    // Logro Super! (spec 0006, D8): super hace de nombre de la clase en los
    // errores de super(...), y el texto genérico no se leería bien con él.
    private boolean esSuper() {
        return "super".equals(lexema);
    }
}
