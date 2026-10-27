package Model.semantico;

import Model.lexico.Token;
import Model.semantico.ast.NodoExpresion;

import java.util.Map;

public class Atributo implements Chequeable, Variable {
    private Token nombre;
    private Tipo tipo;
    // Clase que lo declaró (spec 0003, D4): consolidar() comparte este mismo
    // objeto con las subclases, así que hace falta para chequear el
    // inicializador sólo en su clase de origen. La setea Clase.agregarAtributo().
    private Clase claseDeclarante;
    // Expresión de "= ..." (logro Atributos inicializados); null si se
    // declaró sin inicializador.
    private NodoExpresion inicializador;
    // El "=" de la declaración (spec 0005, D3): lexema de
    // ERR_ASIGNACION_INCOMPATIBLE, igual que en NodoExpAsignacion. null si no
    // hay inicializador.
    private Token igual;
    // Logro Visibilidad chequeada: true si se declaró private. Sin
    // modificador o con public es público (Parte I §6.1). Sólo lo mira el
    // chequeo de cuerpos (ChequeoVisibilidad); la Etapa 3 lo ignora.
    private boolean privado;

    public Atributo(Token nombre, Tipo tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    @Override
    public Token getNombre() {
        return nombre;
    }

    @Override
    public Tipo getTipo() {
        return tipo;
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

    public NodoExpresion getInicializador() {
        return inicializador;
    }

    public void setInicializador(Token igual, NodoExpresion inicializador) {
        this.igual = igual;
        this.inicializador = inicializador;
    }

    // Logro Atributos inicializados (spec 0005, D2): el valor tiene que
    // conformar con el tipo del atributo, con la misma conformidad que el
    // lado derecho de NodoExpAsignacion. Si una subexpresión lanza su propio
    // error, ése es el único error del atributo (sin cascada, D9). Se chequea
    // en contexto de instancia (D4): quien llama setea atributoActual, y
    // enContextoEstatico() da false. Quien llama atrapa el error, porque el
    // inicializador no pasa por un NodoBloque.
    public void chequearInicializador(TablaSimbolos tabla) {
        Tipo valor = inicializador.chequear(tabla);
        if (!valor.conformaCon(tipo, tabla)) {
            throw new ExcepcionSemantica(igual, "ERR_ASIGNACION_INCOMPATIBLE");
        }
    }

    // Clave del atributo: el nombre (los atributos no se sobrecargan).
    public String getClave() {
        return nombre.getLexema();
    }

    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico) {
        tipo.estaBienDeclarado(tabla, entornoGenerico);
    }
}
