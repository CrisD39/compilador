package Model.semantico;

import Model.lexico.Token;
import Model.lexico.TokenType;

import java.util.*;

// No implementa Chequeable: no es una declaración que se chequea, es el
// contexto/sumidero de errores que las declaraciones usan para reportar
// (ver Clase/Interfaz/Metodo/Constructor.estaBienDeclarado(TablaSimbolos)).
public class TablaSimbolos {
    private LinkedHashMap<String, EntidadDeclarada> clases;
    private Clase claseActual;
    private Metodo metodoActual;
    private final List<ErrorSemantico> errores = new ArrayList<>();
    // Nombres que ya dispararon ERR_CLASE_DUPLICADA alguna vez. Sin esto,
    // una TERCERA declaración del mismo nombre no colisionaría con nada
    // (la segunda ya removió la entrada de 'clases') y se insertaría limpia
    // -- "resucitando" un nombre que el logro pide descartar para siempre
    // en cuanto se detecta la repetición, sin importar cuántas veces más
    // se repita.

    private Alcance scopeActual;
    public TablaSimbolos() {
        this.clases = new LinkedHashMap<>();
        registrarPredefinidas();
        scopeActual = null;
    }

    // Tipos predefinidos (Parte I §7, ADR 0004): Object, String y System
    // están en la tabla ANTES de analizar el programa, así que se pueden
    // usar como tipos sin declararlos. Se registran primero para que el
    // LinkedHashMap los recorra primero en consolidar() (los hijos
    // implícitos de Object lo necesitan consolidado). Tokens sintéticos en
    // la línea 0: ningún error del programa puede caer ahí.
    // Lo que el enunciado no dice se completa como Java (ADR 0004): String y
    // System son finales. Ninguna declara 'extends': su padre efectivo es
    // Object por la regla general de Clase.consolidarHerencia(). Object y
    // String tienen constructor por defecto; System no tiene ninguno (no se
    // instancia: todos sus métodos son estáticos).
    private void registrarPredefinidas() {
        Clase object = crearPredefinida("Object", false);
        object.agregarConstructorPorDefectoSiFalta();
        object.agregarMetodo(metodoPredefinido("debugPrint", true, null,
                parametroPredefinido("i", tipoPrimitivo(TokenType.PR_INT, "int"))));
        object.agregarMetodo(metodoPredefinido("toString", false, tipoClase("String")));

        crearPredefinida("String", true).agregarConstructorPorDefectoSiFalta();

        // Cada método recibe Tipos propios (no compartidos): así ningún
        // cambio posterior sobre un Tipo se propaga entre firmas.
        Clase system = crearPredefinida("System", true);
        system.agregarMetodo(metodoPredefinido("read", true, tipoPrimitivo(TokenType.PR_INT, "int")));
        agregarPrintPredefinido(system, "printB", "b", TokenType.PR_BOOLEAN, "boolean");
        agregarPrintPredefinido(system, "printC", "c", TokenType.PR_CHAR, "char");
        agregarPrintPredefinido(system, "printI", "i", TokenType.PR_INT, "int");
        system.agregarMetodo(metodoPredefinido("printS", true, null, parametroPredefinido("s", tipoClase("String"))));
        system.agregarMetodo(metodoPredefinido("println", true, null));
        agregarPrintPredefinido(system, "printBln", "b", TokenType.PR_BOOLEAN, "boolean");
        agregarPrintPredefinido(system, "printCln", "c", TokenType.PR_CHAR, "char");
        agregarPrintPredefinido(system, "printIln", "i", TokenType.PR_INT, "int");
        system.agregarMetodo(metodoPredefinido("printSln", true, null, parametroPredefinido("s", tipoClase("String"))));
    }

    // static void <nombre>(<primitivo> <param>) -- forma de los print* de System.
    private static void agregarPrintPredefinido(Clase system, String nombre, String param,
                                                TokenType tipo, String lexemaTipo) {
        system.agregarMetodo(metodoPredefinido(nombre, true, null,
                parametroPredefinido(param, tipoPrimitivo(tipo, lexemaTipo))));
    }

    private Clase crearPredefinida(String nombre, boolean esFinal) {
        Clase clase = new Clase(new Token(TokenType.ID_CLASE, nombre, 0), this);
        clase.setPredefinida(true);
        clase.setFinal(esFinal);
        clases.put(nombre, clase);
        return clase;
    }

    private static Metodo metodoPredefinido(String nombre, boolean estatico, Tipo retorno, Parametro... params) {
        return new Metodo(new Token(TokenType.ID_MET_VAR, nombre, 0), retorno, estatico,
                new ArrayList<>(List.of(params)));
    }

    private static Parametro parametroPredefinido(String nombre, Tipo tipo) {
        return new Parametro(new Token(TokenType.ID_MET_VAR, nombre, 0), tipo);
    }

    private static Tipo tipoPrimitivo(TokenType tipo, String lexema) {
        return new Tipo(new Token(tipo, lexema, 0));
    }

    private static Tipo tipoClase(String nombre) {
        return new Tipo(new Token(TokenType.ID_CLASE, nombre, 0));
    }

    // Object implícito (Parte I §4): padre efectivo de toda clase sin
    // 'extends'. Nunca es null después del constructor -- redeclarar Object
    // no lo saca de la tabla (ver insertarClase()).
    public Clase getObject() {
        return (Clase) clases.get("Object");
    }

    public void agregarError(ErrorSemantico error) {
        errores.add(error);
    }

    public List<ErrorSemantico> getErrores() {
        return errores;
    }

    public void setClaseActual(Clase actual){
        claseActual = actual;
    }

    public Clase getClaseActual() {
        return claseActual;
    }

    public void setMetodoActual(Metodo actual){
        metodoActual = actual;
    }

    // Comparten namespace (Pasada 1 del EDT): si el nombre ya existe, se
    // descartan AMBAS entidades (Logro 4 -- "nombres repetidos... descarta
    // ambas") y se reporta sobre la línea de la SEGUNDA declaración, la que
    // "ya existe" cuando se intenta registrar.
    //
    // Excepción (Parte I §4, ADR 0004): redeclarar una predefinida descarta
    // SÓLO la declaración del programa. La predefinida se conserva y su
    // nombre no se invalida; si se descartaran las dos, cada uso de String
    // (printS, toString) daría errores en cascada.
    public boolean insertarClase(EntidadDeclarada entidad) {
        String nombre = entidad.getName();
        EntidadDeclarada existente = clases.get(nombre);
        if (esPredefinida(existente)) {
            agregarError(new ErrorSemantico("ERR_CLASE_DUPLICADA", entidad.getLinea(), nombre));
            return false;
        }
        if (clases.containsKey(nombre) || nombresInvalidados.contains(nombre)) {
            clases.remove(nombre);
            nombresInvalidados.add(nombre);
            agregarError(new ErrorSemantico("ERR_CLASE_DUPLICADA", entidad.getLinea(), nombre));
            return false;
        }
        clases.put(nombre, entidad);
        return true;
    }

    public EntidadDeclarada buscarClase(String nombre) {
        return clases.get(nombre);
    }
    // Clase e Interfaz comparten namespace (ver insertarClase / analizador-
    // semantico.md), así que se resuelven contra el mismo mapa.
    public EntidadDeclarada buscarInterfaz(String nombre) {
        return clases.get(nombre);
    }

    // Relación con el tipo equivocado (D1): el 'extends' de una clase tiene
    // que nombrar una clase; el 'implements' de una clase y el 'extends' de
    // una interfaz, interfaces. Como Clase e Interfaz comparten namespace,
    // buscarClase()/buscarInterfaz() devuelven cualquiera de las dos, y el
    // chequeo de categoría queda en manos de quien declara la relación.
    // Se reporta sobre el tipo nombrado (lexema y línea de ese token, igual
    // que ERR_TIPO_NO_DECLARADO) y una sola vez, en el control de
    // declaraciones: la consolidación saltea la relación sin reportar (una
    // clase queda con Object como padre efectivo). Un supertipo que no
    // resuelve ya lo reportó Tipo.estaBienDeclarado() (D7), acá no se toca.
    // Sólo reporta: la consolidación vuelve a mirar la categoría del tipo
    // resuelto para decidir si ignora la relación.
    public void chequearCategoriaSupertipo(Tipo supertipo, boolean esperaClase) {
        Token token = supertipo.getToken();
        EntidadDeclarada entidad = clases.get(token.getLexema());
        if (entidad != null && (entidad instanceof Clase) != esperaClase) {
            agregarError(new ErrorSemantico("ERR_HERENCIA_TIPO_INCORRECTO",
                    token.getLinea(), token.getLexema(), esperaClase ? "una clase" : "una interfaz"));
        }
    }

    // Object, String o System (ADR 0004), que tienen reglas propias: no se
    // descartan si el programa las redeclara y no pasan por el control de
    // declaraciones.
    private static boolean esPredefinida(EntidadDeclarada entidad) {
        return entidad instanceof Clase && ((Clase) entidad).isPredefinida();
    }
    // Parchean el placeholder dejado por crearMetodo() (Estrategia A de
    // transición, ver "Estrategia de construcción" en analizador-semantico.md)
    // delegando sobre metodoActual, igual criterio que
    // agregarMetodoActualAClaseActual delega sobre claseActual.
    public void setTipoRetornoAMetodoActual(Tipo tipoRetorno) {
        metodoActual.setTipoRetorno(tipoRetorno);
    }

    public void setParametrosAMetodoActual(List<Parametro> parametros) {
        metodoActual.setParametros(parametros);
    }

    public void setParametrosTipoAMetodoActual(List<Token> parametrosTipo) {
        metodoActual.setParametrosTipoPropios(parametrosTipo);
    }

    // Recién ahora -- después de setParametrosAMetodoActual, con los
    // parámetros reales ya parcheados -- se puede registrar en
    // Clase.metodos sin que Clase.agregarMetodo() calcule la clave
    // (nombre/aridad) sobre el placeholder vacío. Antes esto se hacía
    // dentro de crearMetodo(), con params=[] todavía, y la clave quedaba
    // fijada mal para siempre (ver el comentario en crearMetodo()
    // en AnalizadorSintacticoImpl.java).
    public void agregarMetodoActualAClaseActual(){
        if (claseActual == null) {
            return;
        }
        claseActual.agregarMetodo(metodoActual);
    }

    // Control de declaraciones (ver "TODO — Chequeo de corrección" en
    // analizador-semantico.md): corre DESPUÉS de que Pasada 1+2 ya armaron
    // toda la tabla, y ANTES de consolidar().
    // Chequea cada declaración tal como la escribió el usuario (parámetros
    // duplicados, tipos no declarados, etc.), delegando hacia abajo. Tiene
    // que ir ANTES de consolidar la herencia: consolidarConPadre copia los
    // miembros del padre dentro de la hija, y si este chequeo corriera
    // después los re-validaría en la hija -- errores duplicados por cada
    // subclase, y los genéricos del padre (T) evaluados contra el entorno de
    // la hija, donde no existen.
    // Las predefinidas no pasan por este control: las armó la tabla, no el
    // usuario, y no hay nada que reportarle sobre ellas.
    public void estaBienDeclarado() {
        for (EntidadDeclarada entidad : clases.values()) {
            if (esPredefinida(entidad)) {
                continue;
            }
            // Constructor por defecto (§6.3): recién acá, con el parseo
            // terminado, se sabe si la clase quedó sin constructores
            // registrados. Sin parámetros, no tiene nada que chequear.
            if (entidad instanceof Clase) {
                ((Clase) entidad).agregarConstructorPorDefectoSiFalta();
            }
            if (entidad instanceof Chequeable) {
                ((Chequeable) entidad).estaBienDeclarado(this, Collections.emptyMap());
            }
        }
    }

    // Consolidación: resuelve herencia/ciclos e interfaces. Asume que ya
    // corrió estaBienDeclarado() (ver ahí por qué el orden importa).
    // Recorre en orden de declaración (LinkedHashMap) para que, ante un
    // ciclo, la clase/interfaz reportada sea determinística. Las
    // predefinidas sí se consolidan (String hereda toString de Object), y
    // van primero porque se registraron en el constructor.
    public void consolidar() {
        for (EntidadDeclarada entidad : clases.values()) {
            if (entidad instanceof Clase) {
                ((Clase) entidad).consolidar();
            } else if (entidad instanceof Interfaz) {
                ((Interfaz) entidad).consolidar();
            }
        }
    }

    //Chequeo de los AST.
    public void chequearSentencias(){

    }

    //Toda la parte de creación y cierre de scope, buscar e insertar atributos.

    public void abrirScope(){
        if (scopeActual != null)
            scopeActual.setAlcancePadre(scopeActual);
        scopeActual = new Alcance();
    }

    public  void cerrarScope(){
        if(scopeActual.getScopePadre()!=null)
            scopeActual = scopeActual.getScopePadre();
    }

    public void añadirAtributoScope(Atributo atributoNuevo){
        scopeActual.insertarAtributo(atributoNuevo);
    }
    public Atributo buscarAtributo(String atributoBuscado) {
        if (scopeActual != null)
            return scopeActual.buscarAtributo(atributoBuscado);
        else {
            //TODO:ERROR buscar cuando no existe scope.
        }
    }
}
