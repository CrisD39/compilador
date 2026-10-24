package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.Constructor;
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
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    // §10.3, §11.7: la clase tiene que existir (ERR_TIPO_NO_DECLARADO, el de
    // la Etapa 3) y no ser una interfaz. El constructor se elige por aridad
    // entre los de la clase: el por defecto sólo existe si no declaró
    // ninguno (semIIError25), y los de los ancestros no cuentan porque los
    // constructores no se heredan (semIIError26). Todos los errores van
    // sobre el nombre de la clase. Los argumentos de tipo siguen las reglas
    // de la Etapa 3, con sus códigos: una clase genérica sin argumento es un
    // tipo raw (ADR 0007, semIIError24) y una no genérica no admite uno.
    // Pendiente (ticket 06): new Caja<String>(...), con los parámetros del
    // constructor vistos desde Caja<String> (D10). Corta en vez de comparar
    // los argumentos contra un T sin reemplazar.
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
        if (!(entidad instanceof Clase clase)) {
            throw new ExcepcionSemantica(nombre, "ERR_NEW_INTERFAZ");
        }
        boolean esGenerica = !clase.getParametrosTipo().isEmpty();
        if (esGenerica && tipo.getArgumento() == null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_SIN_ARGUMENTO");
        }
        if (!esGenerica && tipo.getArgumento() != null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_NO_GENERICO");
        }
        if (esGenerica) {
            throw ExcepcionSemantica.noSoportado(nombre);
        }
        Constructor constructor = clase.getConstructores().stream()
                .filter(c -> c.getParams().size() == argumentos.size())
                .findFirst()
                .orElseThrow(() -> new ExcepcionSemantica(nombre, "ERR_CONSTRUCTOR_NO_ENCONTRADO"));
        List<Tipo> tiposArgumentos = ChequeoLlamada.chequearArgumentos(tabla, argumentos);
        ChequeoLlamada.exigirConformidad(tabla, constructor.getParams(), tiposArgumentos, nombre);
        return chequearEncadenado(tabla, tipo);
    }
}
