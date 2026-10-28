package Model.semantico;

import Model.lexico.Token;

// Error de una sentencia o expresión en el chequeo de cuerpos (spec 0003,
// D7). Se lanza en vez de propagar un "tipo error" por todo el árbol: corta
// la sentencia que lo produjo, y NodoBloque lo atrapa y sigue con la próxima
// (piso del logro Multi-detección). El token es el que da lexema y línea a
// la etiqueta [Error:<lexema>|<línea>], según la convención de lexemas de
// etapa4-semantico2/casos-de-prueba.md.
public class ExcepcionSemantica extends RuntimeException {
    private final Token token;
    private final String codigo;
    // Pasa tal cual al ErrorSemantico que se registra (ver su campo detalle):
    // quien lanza sabe qué variante del código es, y el token solo no lo dice.
    private final String detalle;
    private final boolean enCascada;

    public ExcepcionSemantica(Token token, String codigo) {
        this(token, codigo, null, false);
    }

    public ExcepcionSemantica(Token token, String codigo, String detalle) {
        this(token, codigo, detalle, false);
    }

    private ExcepcionSemantica(Token token, String codigo, String detalle, boolean enCascada) {
        super(codigo + " (" + token.getLexema() + ")");
        this.token = token;
        this.codigo = codigo;
        this.detalle = detalle;
        this.enCascada = enCascada;
    }

    // Corta una sentencia que usa una local cuyo var no tuvo tipo
    // (VariableLocal.sinTipo()). Ese error ya se reportó en el var: esta
    // excepción no agrega otro (TablaSimbolos.registrarError()), así un var
    // que falla no arrastra un "no declarada" por cada uso posterior.
    public static ExcepcionSemantica enCascada(Token token) {
        return new ExcepcionSemantica(token, "ERR_EN_CASCADA", null, true);
    }

    public boolean esEnCascada() {
        return enCascada;
    }

    // Lo lanzan los nodos de logro que todavía no se chequean (spec 0003,
    // D11), en vez de aceptar la construcción en silencio. El token es el de
    // la tabla de D11 (for, ?, ->, new, el nombre de la local, ...).
    public static ExcepcionSemantica noSoportado(Token token) {
        return new ExcepcionSemantica(token, "ERR_NO_SOPORTADO");
    }

    public Token getToken() {
        return token;
    }

    public String getCodigo() {
        return codigo;
    }

    // Lo que se agrega a la tabla al atraparla: mismo formato de salida que
    // los errores de declaraciones.
    public ErrorSemantico comoError() {
        return new ErrorSemantico(codigo, token.getLinea(), token.getLexema(), detalle);
    }
}
