package Model.semantico;

import Model.lexico.Token;
import Model.semantico.ast.NodoBloque;

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
    // Clase que lo declaró (spec 0003, D4), la setea Clase.agregarMetodo().
    // consolidar() comparte este mismo objeto con las subclases, así que el
    // cuerpo se chequea sólo en la clase donde miClase == this. null en los
    // métodos de interfaz, que no tienen cuerpo.
    private Clase miClase;
    // Cuerpo del método; null hasta que el parser enganche el AST.
    private NodoBloque cuerpo;
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

    // Tipo de una llamada a este método en una expresión (spec 0003, D5):
    // el retorno, o Tipo.VOID si es void. La declaración sigue con null, del
    // que depende la comparación de signaturas.
    public Tipo getTipoDeLlamada() {
        return tipoRetorno != null ? tipoRetorno : Tipo.VOID;
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

    public Clase getMiClase() {
        return miClase;
    }

    public void setMiClase(Clase miClase) {
        this.miClase = miClase;
    }

    public NodoBloque getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(NodoBloque cuerpo) {
        this.cuerpo = cuerpo;
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
        return clave(nombre.getLexema(), parametros.size());
    }

    // La misma clave armada desde una llamada, que tiene el nombre y la
    // cantidad de argumentos pero todavía no el método (§10.2).
    public static String clave(String nombre, int aridad) {
        return nombre + "/" + aridad;
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
     * (Clase.chequearRedefinicion()), en el contrato de interfaz
     * (Clase.consolidarInterfaces()), en el conflicto entre superinterfaces
     * (Interfaz.heredarMetodosDe()) y en la comparación de una interfaz con
     * Object (Interfaz.heredarDeObject()).
     *
     * 'vista' transforma cada tipo de los DOS métodos antes de comparar: es
     * cómo se ven desde el tipo que compara. Es la instanciación genérica:
     * el parámetro de cada ancestro reemplazado por el argumento con que se
     * lo hereda (Tipo.sustituir() con el mapa de instanciación). Se aplica a
     * los dos lados porque ambos pueden venir de ancestros genéricos: en
     * "interface Lista<U> extends Fuente<U>, Origen<U>", el "T get()" de
     * Fuente y el de Origen nombran T distintos, y son la misma signatura
     * sólo si los dos pasan a U (semCorrecto34). Para un método declarado en
     * el tipo que compara la vista no cambia nada (el mapa sólo tiene
     * ancestros). No modifica a ninguno.
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
    public boolean mismaSignatura(Metodo otro, UnaryOperator<Tipo> vista) {
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
        UnaryOperator<Tipo> verElOtro =
                tipo -> vista.apply(tipo).reemplazarParametros(renombrarPropiosDelOtro);
        if (!mismoTipo(tipoRetorno, otro.tipoRetorno, vista, verElOtro)) {
            return false;
        }
        for (int i = 0; i < parametros.size(); i++) {
            if (!mismoTipo(parametros.get(i).getTipo(), otro.parametros.get(i).getTipo(), vista, verElOtro)) {
                return false;
            }
        }
        return true;
    }

    // null es void (ver tipoRetorno): sólo es igual a otro void.
    private static boolean mismoTipo(Tipo propio, Tipo delOtro,
                                     UnaryOperator<Tipo> verEste, UnaryOperator<Tipo> verElOtro) {
        if (propio == null || delOtro == null) {
            return propio == delOtro;
        }
        return verEste.apply(propio).equals(verElOtro.apply(delOtro));
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
        // Las variables locales son chequeo de sentencias (tercera pasada,
        // TablaSimbolos.chequearSentencias()). Acá solo se valida que los
        // parámetros no se repitan entre sí.
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
