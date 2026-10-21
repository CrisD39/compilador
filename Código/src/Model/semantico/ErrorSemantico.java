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
            case "ERR_HERENCIA_TIPO_INCORRECTO" ->
                    x + " no es " + (detalle != null ? detalle : "del tipo esperado en la relación");
            default -> codigo + " (" + x + ")";
        };
    }
}
