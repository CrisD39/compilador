package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Clase;
import Model.semantico.Constructor;
import Model.semantico.ErrorSemantico;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.OrigenParametroTipo;
import Model.semantico.Parametro;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// Lo que comparten las tres llamadas (m(), C.m() y e.m(), ⚠6) y la creación
// de objetos: tipar los argumentos y controlarlos contra los parámetros del
// método o constructor ya elegido por nombre/aridad (§10.2, §10.3). No es un
// nodo: cada uno resuelve su método a su manera y después delega acá.
final class ChequeoLlamada {
    private ChequeoLlamada() {
    }

    // Cada nodo los chequea recién después de resolver el método o el
    // constructor, que sólo necesita la cantidad: con un error por sentencia
    // (D7), el informado es el primero en el orden del fuente, y en
    // noExiste(z) gana noExiste y no z. El tipo esperado de cada argumento
    // es su formal visto desde el receptor (spec 0007, D3; ADR 0011): en
    // cs.poner(c ? "a" : null) con Caja<String>, cada rama se compara con
    // String. Un esperado null es sin tipo esperado. Sólo lo usa un ternario;
    // la conformidad la sigue controlando exigirConformidad(), después de
    // tipar todos, así en m(1, c ? x : "a") con (String, int) gana el "?" y
    // no el 1 (semIITernarioError22).
    private static List<Tipo> chequearArgumentos(TablaSimbolos tabla, List<NodoExpresion> argumentos,
                                                 List<Tipo> esperados) {
        List<Tipo> tipos = new ArrayList<>();
        for (int i = 0; i < argumentos.size(); i++) {
            tipos.add(argumentos.get(i).chequearConTipoEsperado(tabla, esperados.get(i)));
        }
        return tipos;
    }

    // Cada formal visto desde el receptor ('vista'): en new Caja<String>(5)
    // el parámetro T es String (semIIError28). Se calcula una vez y sirve de
    // tipo esperado de los argumentos y para su conformidad.
    private static List<Tipo> formalesVistos(List<Parametro> formales, Map<OrigenParametroTipo, Tipo> vista) {
        List<Tipo> tipos = new ArrayList<>();
        for (Parametro formal : formales) {
            tipos.add(formal.getTipo().sustituir(vista));
        }
        return tipos;
    }

    // Resolución de un constructor de la clase que nombra 'tipo', compartida
    // por new C(...) y super(...) (logro Super!, spec 0006, D8). 'tipo'
    // nombra siempre una clase: new ya descartó lo que no existe y las
    // interfaces, y la superclase de super(...) lo es por D5 (sin errores de
    // declaración no corre el chequeo); si eso se rompe, falla acá con un
    // mensaje y no con un cast. Se elige por aridad: el por defecto sólo
    // existe si la clase no declaró ninguno (semIIError25), y los de los
    // ancestros no cuentan porque no se heredan (semIIError26). Un
    // constructor private sólo vale dentro de su clase (logro Visibilidad
    // chequeada). Los parámetros se ven desde 'tipo': en new Caja<String>(5)
    // o en super(5) con extends Caja<String>, T es String (D10,
    // semIIError28). Todos los errores van sobre 'token': el nombre de la
    // clase en new, super en la llamada al constructor de la superclase.
    // 'deSuper' dice si la llamada es super(...) y no new, y de ahí salen
    // dos cosas: esos errores llevan ErrorSemantico.DETALLE_SUPER, que elige
    // su descripción, y los argumentos se chequean en el contexto previo a
    // la construcción (D9). La marca se prende sólo alrededor de ellos y
    // vuelve a su valor anterior aunque uno lance, así el resto del
    // constructor usa el objeto actual normalmente y un new anidado
    // (super(new C(this))) no la apaga.
    static void chequearConstructor(TablaSimbolos tabla, Tipo tipo, List<NodoExpresion> argumentos,
                                    Token token, boolean deSuper) {
        String detalle = deSuper ? ErrorSemantico.DETALLE_SUPER : null;
        if (!(tabla.entidadDe(tipo) instanceof Clase clase)) {
            throw new IllegalStateException("Constructor de algo que no es una clase: " + tipo.getToken().getLexema());
        }
        Constructor constructor = clase.getConstructores().stream()
                .filter(c -> c.getParams().size() == argumentos.size())
                .findFirst()
                .orElseThrow(() -> new ExcepcionSemantica(token, "ERR_CONSTRUCTOR_NO_ENCONTRADO", detalle));
        ChequeoVisibilidad.exigirAcceso(tabla, constructor.isPrivado(), clase, clase, token, detalle);
        List<Tipo> formales = formalesVistos(constructor.getParams(), tabla.instanciacionDe(tipo));
        List<Tipo> tiposArgumentos;
        if (deSuper) {
            boolean anterior = tabla.prenderContextoPrevioAConstruccion();
            try {
                tiposArgumentos = chequearArgumentos(tabla, argumentos, formales);
            } finally {
                tabla.restaurarContextoPrevioAConstruccion(anterior);
            }
        } else {
            tiposArgumentos = chequearArgumentos(tabla, argumentos, formales);
        }
        exigirConformidad(tabla, formales, tiposArgumentos, token, detalle);
    }

    // Tipo de la llamada a un método ya resuelto, con sus argumentos. 'vista'
    // es el mapa con que se ven sus parámetros y su retorno desde el receptor
    // (D10): en c.obtener() con c de tipo Caja<String>, T obtener() devuelve
    // String (semIICorrecto12). Un método con parámetros de tipo propios es
    // del logro Métodos genéricos (D11): sus formales pueden nombrar esos T,
    // así que los argumentos se tipan sin tipo esperado, como antes de la
    // spec 0007, y se corta con ERR_NO_SOPORTADO antes de compararlos contra
    // sus T.
    static Tipo tipoDeLlamada(TablaSimbolos tabla, Metodo metodo, Map<OrigenParametroTipo, Tipo> vista,
                              Token nombre, List<NodoExpresion> argumentos) {
        if (!metodo.getParametrosTipoPropios().isEmpty()) {
            chequearArgumentos(tabla, argumentos, Collections.nCopies(argumentos.size(), null));
            throw ExcepcionSemantica.noSoportado(nombre);
        }
        List<Tipo> formales = formalesVistos(metodo.getParams(), vista);
        exigirConformidad(tabla, formales, chequearArgumentos(tabla, argumentos, formales), nombre, null);
        return metodo.getTipoDeLlamada().sustituir(vista);
    }

    // Cada argumento conforma con su parámetro, en orden (§10.2). La aridad
    // ya coincide porque se eligió por clave, y no se busca otra variante por
    // tipos (semIIError16). Un argumento void no conforma con nada
    // (semIIError49). 'formales' ya viene visto desde el receptor
    // (formalesVistos()). El error va sobre el nombre del método o de la
    // clase, con el 'detalle' de chequearConstructor() (null en una llamada a
    // método).
    private static void exigirConformidad(TablaSimbolos tabla, List<Tipo> formales, List<Tipo> actuales,
                                          Token token, String detalle) {
        for (int i = 0; i < formales.size(); i++) {
            if (!actuales.get(i).conformaCon(formales.get(i), tabla)) {
                throw new ExcepcionSemantica(token, "ERR_ARGUMENTO_INCOMPATIBLE", detalle);
            }
        }
    }
}
