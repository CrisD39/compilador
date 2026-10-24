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

    public NodoExpresion getInicializador() {
        return inicializador;
    }

    public void setInicializador(NodoExpresion inicializador) {
        this.inicializador = inicializador;
    }

    // Logro Atributos inicializados: todavía no soportado (spec 0003, D11),
    // se reporta sobre el nombre del atributo. Quien llama atrapa el error,
    // porque el inicializador no pasa por un NodoBloque.
    public void chequearInicializador(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(nombre);
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
