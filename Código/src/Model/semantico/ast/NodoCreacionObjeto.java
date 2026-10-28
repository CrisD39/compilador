package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <RestoNew> ::= idClase <TipoGenericoOpcionalNew> <ArgsActuales>. El token
// del tipo (el nombre de la clase) es el lexema de sus errores. Con
// diamante (new C<>()) el tipo queda sin argumento, y el logro Genericidad
// avanzada todavía no está soportado (D11).
public class NodoCreacionObjeto extends NodoPrimario {
    private final Tipo tipo;
    private final boolean esDiamante;
    private final List<NodoExpresion> argumentos;

    public NodoCreacionObjeto(Tipo tipo, boolean esDiamante, List<NodoExpresion> argumentos) {
        this.tipo = tipo;
        this.esDiamante = esDiamante;
        this.argumentos = argumentos;
    }

    @Override
    public Token primerToken() {
        return tipo.getToken();
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    // §10.3, §11.7: la clase tiene que existir (ERR_TIPO_NO_DECLARADO, el de
    // la Etapa 3) y no ser una interfaz. Todos los errores van sobre el
    // nombre de la clase. Los argumentos de tipo siguen las reglas de la
    // Etapa 3, con sus códigos (Tipo.resolverEnCuerpo()): una clase genérica
    // sin argumento es un tipo raw (ADR 0007, semIIError24) y una no
    // genérica no admite uno. El constructor se resuelve desde el tipo creado
    // (ChequeoLlamada.chequearConstructor()), igual que en super(...).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        Token nombre = tipo.getToken();
        if (esDiamante) {
            throw ExcepcionSemantica.noSoportado(nombre);
        }
        EntidadDeclarada entidad = tabla.buscarClase(nombre.getLexema());
        if (entidad == null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_NO_DECLARADO");
        }
        if (!(entidad instanceof Clase)) {
            throw new ExcepcionSemantica(nombre, "ERR_NEW_INTERFAZ");
        }
        Tipo creado = tipo.resolverEnCuerpo(tabla);
        ChequeoLlamada.chequearConstructor(tabla, creado, argumentos, nombre, false);
        return chequearEncadenado(tabla, creado);
    }
}
