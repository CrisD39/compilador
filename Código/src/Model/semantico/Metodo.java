package Model.semantico;

import Model.lexico.Token;
import Model.lexico.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Metodo implements Chequeable, OrigenParametroTipo {
    private Token nombre;
    private Tipo tipoRetorno; // null representa "void" ?? not sure
    private boolean estatico;
    private boolean esFinal;
    private List<Parametro> parametros;
    // Variables locales del cuerpo (ver "Chequeo de sentencias" en
    // analizador-semantico.md): se reutiliza Atributo (nombre+tipo) en vez de
    // una clase nueva, mismo criterio que para Constructor con Metodo. Todavía
    // no se puebla — bloque() sigue siendo puramente sintáctico (Pasada 2).
    private List<Atributo> variablesLocales = new ArrayList<>();
    //No sé como hacer para que el metodo sepa que atributos estan habilitados para utilizar.
    private Clase miClase; //la clase que tengo asociadaaa
    // Parámetros de tipo propios del método (ej. el T de "<T> T metodo(T x)"),
    // distintos del/los de la clase/interfaz contenedora -- ver
    // TablaSimbolos.setParametrosTipoAMetodoActual().
    private List<Token> parametrosTipoPropios = new ArrayList<>();

    public Metodo(Token nombre, Tipo tipoRetorno, boolean estatico, List<Parametro> params) {
        this.nombre = nombre;
        this.tipoRetorno = tipoRetorno;
        this.estatico = estatico;
        this.parametros = params;
    }

    public Token getNombre() {
        return nombre;
    }

    // Parchea el placeholder creado por crearMetodo() (Estrategia A de
    // transición, ver "Estrategia de construcción" en analizador-semantico.md)
    // con los valores reales una vez que Pasada 2 los termina de resolver.
    public void setTipoRetorno(Tipo tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public void setParametros(List<Parametro> parametros) {
        this.parametros = parametros;
    }

    public Tipo getTipoRetorno() {
        return tipoRetorno;
    }

    public boolean isEstatico() {
        return estatico;
    }

    public void setFinal(boolean esFinal) {
        this.esFinal = esFinal;
    }

    public boolean isFinal() {
        return esFinal;
    }

    public List<Parametro> getParams() {
        return parametros;
    }

    public List<Token> getParametrosTipoPropios() {
        return parametrosTipoPropios;
    }

    public void setParametrosTipoPropios(List<Token> parametrosTipoPropios) {
        this.parametrosTipoPropios = parametrosTipoPropios;
    }

    private int indiceEnPropios(String lexema) {
        for (int i = 0; i < parametrosTipoPropios.size(); i++) {
            if (parametrosTipoPropios.get(i).getLexema().equals(lexema)) {
                return i;
            }
        }
        return -1;
    }

    // Para el retorno: un idGen que es parametro de tipo PROPIO de este
    // metodo se identifica por posicion, no por el nombre elegido -- "<T> T
    // m(T x)" y "<U> U m(U x)" tienen el MISMO retorno (D6), igual que en
    // Java real donde el nombre del type variable propio no importa. Todo
    // lo demas (primitivos, idClase, idGen de la clase/interfaz
    // contenedora) sigue comparandose por lexema crudo.
    private String tipoCanonico(Token tipoToken) {
        if (tipoToken.getTipo() == TokenType.ID_GEN) {
            int indice = indiceEnPropios(tipoToken.getLexema());
            if (indice >= 0) {
                return "#" + indice;
            }
        }
        return tipoToken.getLexema();
    }

    /**
     * Clave del método (glosario: Clave): "nombre/aridad". La Parte I sólo
     * admite sobrecarga por cantidad de parámetros (D4), así que
     * imprimir(int) e imprimir(char) tienen la misma clave imprimir/1 y son
     * una declaración repetida, aunque difieran los tipos o el retorno
     * (semError56, semError85). Indexa los métodos de Clase e Interfaz y es
     * lo que se compara al heredar y al cumplir un contrato; la signatura
     * (tipos) se compara aparte, en la redefinición.
     */
    public String getClave() {
        return nombre.getLexema() + "/" + parametros.size();
    }

    /**
     * Signatura (glosario: Signatura) igual a la de 'otro', sin
     * instanciación: ver {@link #mismaSignatura(Metodo, UnaryOperator)}.
     */
    public boolean mismaSignatura(Metodo otro) {
        return mismaSignatura(otro, UnaryOperator.identity());
    }

    /**
     * true si este método y 'otro' tienen la misma signatura: misma cantidad
     * de parámetros, los mismos tipos en orden y el mismo retorno (void sólo
     * es igual a void), con la igualdad estructural de Tipo (cabeza,
     * argumento, dimensiones y origen de los parámetros de tipo). Los nombres
     * de los parámetros no cuentan. Se usa en la redefinición
     * (Clase.consolidarConPadre(), ticket 09); el contrato de interfaz
     * (ticket 11) la reutiliza.
     *
     * 'vistaDelOtro' transforma cada tipo de 'otro' antes de comparar: es
     * cómo se ve 'otro' desde el tipo que compara. En la redefinición es la
     * instanciación genérica: el parámetro de cada ancestro reemplazado por
     * el argumento de la herencia (Tipo.sustituir() con el mapa de
     * Clase.getInstanciacion()). No modifica a 'otro'.
     *
     * Los parámetros de tipo PROPIOS de cada método se emparejan por
     * posición (D6): "<T> T m(T x)" y "<U> U m(U x)" tienen la misma
     * signatura (semCorrecto20, semError42). Para eso, después de la vista,
     * cada idGen cuyo origen es 'otro' se renombra al parámetro de tipo de
     * esta posición en este método, y recién ahí se compara con
     * Tipo.equals(). Si la cantidad de parámetros de tipo propios difiere,
     * las signaturas difieren ("<T> T id(T x)" contra "Object id(Object x)",
     * semError80).
     */
    public boolean mismaSignatura(Metodo otro, UnaryOperator<Tipo> vistaDelOtro) {
        if (parametros.size() != otro.parametros.size()
                || parametrosTipoPropios.size() != otro.parametrosTipoPropios.size()) {
            return false;
        }
        Function<Tipo, Tipo> renombrarPropiosDelOtro = parametro -> {
            if (parametro.getOrigen() != otro) {
                return null;
            }
            int indice = otro.indiceEnPropios(parametro.getToken().getLexema());
            return indice < 0 ? null : Tipo.parametroDeTipo(parametrosTipoPropios.get(indice), this);
        };
        UnaryOperator<Tipo> verDesdeAca =
                tipo -> vistaDelOtro.apply(tipo).reemplazarParametros(renombrarPropiosDelOtro);
        if (!mismoTipo(tipoRetorno, otro.tipoRetorno, verDesdeAca)) {
            return false;
        }
        for (int i = 0; i < parametros.size(); i++) {
            if (!mismoTipo(parametros.get(i).getTipo(), otro.parametros.get(i).getTipo(), verDesdeAca)) {
                return false;
            }
        }
        return true;
    }

    // null es void (ver tipoRetorno): sólo es igual a otro void.
    private static boolean mismoTipo(Tipo propio, Tipo delOtro, UnaryOperator<Tipo> verDesdeAca) {
        if (propio == null || delOtro == null) {
            return propio == delOtro;
        }
        return propio.equals(verDesdeAca.apply(delOtro));
    }

    // Retorno canonicalizado, usado para comparar compatibilidad de retorno
    // entre métodos con la misma clave (Clase.consolidarInterfaces(),
    // Interfaz.heredarMetodosDe()). null representa "void", igual que
    // tipoRetorno. Compara por lexema, no por origen: el ticket 11 lo
    // reemplaza por mismaSignatura() con la vista instanciada (sin ella, el
    // T de una interfaz genérica nunca sería igual al de la clase).
    public String getRetornoCanonico() {
        return tipoRetorno == null ? null : tipoCanonico(tipoRetorno.getToken());
    }

    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoContenedor) {
        // Entorno vigente para la signatura de ESTE método: el de la clase/
        // interfaz contenedora más los propios -- shadowing permitido (si el
        // método reutiliza un nombre ya vigente, no es error, ver decisión
        // de diseño en analizador-semantico.md). El put pisa al contenedor:
        // en "class C<T> { <T> T m(T x) }" el T de m tiene origen m, como en
        // Java.
        Map<String, OrigenParametroTipo> entornoVigente = new HashMap<>(entornoContenedor);
        for (Token parametroTipo : parametrosTipoPropios) {
            entornoVigente.put(parametroTipo.getLexema(), this);
        }
        if (tipoRetorno != null) {
            tipoRetorno.estaBienDeclarado(tabla, entornoVigente);
        }
        // El chequeo de variables locales (variablesLocales) es chequeo de
        // SENTENCIAS, fuera de alcance de esta etapa -- ver
        // analizador-semantico.md. Acá solo se valida que los parámetros no
        // se repitan entre sí.
        Set<String> nombresVisibles = new HashSet<>();
        for (Parametro parametro : parametros) {
            if (!nombresVisibles.add(parametro.getNombre().getLexema())) {
                tabla.agregarError(new ErrorSemantico(
                        "ERR_PARAMETRO_DUPLICADO", parametro.getNombre().getLinea(), parametro.getNombre().getLexema()));
            }
            parametro.estaBienDeclarado(tabla, entornoVigente);
        }
    }
}
