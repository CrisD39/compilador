package Model.semantico;

import Model.lexico.Token;
import Model.lexico.TokenType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final Set<String> nombresInvalidados = new HashSet<>();

    // Estado del chequeo de sentencias (spec 0003, D4/D6). La unidad actual
    // son tres campos y a lo sumo uno es distinto de null (ver
    // setUnidadActual()); metodoActual es el mismo que usa el parser.
    private Constructor constructorActual;
    private Atributo atributoActual;
    // Un mapa por bloque abierto, el tope es el más interno. Lo abren y
    // cierran los nodos que abren un scope, siempre en un finally.
    private final Deque<Map<String, VariableLocal>> bloques = new ArrayDeque<>();

    public TablaSimbolos() {
        this.clases = new LinkedHashMap<>();
        registrarPredefinidas();
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

    // Lo que hace quien atrapa una ExcepcionSemantica (NodoBloque, el
    // inicializador de atributo): la agrega como error, salvo que sea en
    // cascada de un error ya reportado.
    public void registrarError(ExcepcionSemantica e) {
        if (!e.esEnCascada()) {
            agregarError(e.comoError());
        }
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

    // Clase o interfaz que nombra un tipo usado en una expresión (spec 0003,
    // ticket 04); null si no nombra ninguna: primitivos, void, null,
    // parámetros de tipo y arreglos. Con D3 no hay tipos sin declarar en las
    // declaraciones, así que un null acá es siempre "no es clase ni
    // interfaz". El nombre suelto de una clase (C.m(), new C()) lo resuelve
    // cada nodo con buscarClase().
    public EntidadDeclarada entidadDe(Tipo tipo) {
        if (tipo.esArreglo() || tipo.getToken().getTipo() != TokenType.ID_CLASE) {
            return null;
        }
        return clases.get(tipo.getToken().getLexema());
    }

    // 'tipo' (una clase o interfaz, no un arreglo) visto como 'ancestro', con
    // el argumento con que lo ve (§9.2, §9.3, spec 0003 D9): con
    // "Sub extends Caja<String>", Sub visto como Caja es Caja<String>. null
    // si 'ancestro' no es el tipo ni uno de sus ancestros por extends/
    // implements, a cualquier distancia. Se sube por los supertipos tal como
    // están escritos, reemplazando en cada uno el parámetro de la entidad por
    // el argumento de 'tipo'; así cubre también los ancestros que llegan por
    // 'implements', que Clase.getInstanciacion() no incluye. Heredado sin
    // argumento, el parámetro del ancestro se conserva (D5) y no iguala a
    // ningún argumento concreto. Sin errores de declaraciones (D3) no hay
    // ciclos ni relaciones con el tipo equivocado, así que la recursión
    // termina.
    public Tipo vistaComoAncestro(Tipo tipo, EntidadDeclarada ancestro) {
        EntidadDeclarada entidad = entidadDe(tipo);
        if (entidad == ancestro) {
            return tipo;
        }
        Map<OrigenParametroTipo, Tipo> propio = tipo.getArgumento() == null
                ? Map.of() : Map.of((OrigenParametroTipo) entidad, tipo.getArgumento());
        for (Tipo supertipo : supertiposDe(entidad)) {
            Tipo vista = vistaComoAncestro(supertipo.sustituir(propio), ancestro);
            if (vista != null) {
                return vista;
            }
        }
        return null;
    }

    // Supertipos directos de una entidad, como tipos. Una clase sube por su
    // padre efectivo, que es Object si no escribió 'extends', y por su
    // interfaz; una interfaz, por sus 'extends'. Toda interfaz llega además a
    // Object, como en Java (ADR 0004): ya contiene sus métodos de instancia
    // (spec 0002).
    private List<Tipo> supertiposDe(EntidadDeclarada entidad) {
        List<Tipo> supertipos = new ArrayList<>();
        if (entidad instanceof Clase clase) {
            Clase padre = clase.getPadreEfectivo();
            if (padre != null) {
                supertipos.add(clase.getHerencia() != null ? clase.getHerencia() : new Tipo(padre.getNombre()));
            }
            if (clase.getInterfaz() != null) {
                supertipos.add(clase.getInterfaz());
            }
        } else {
            ((Interfaz) entidad).getHerencias().forEach(supertipos::add);
            supertipos.add(new Tipo(getObject().getNombre()));
        }
        return supertipos;
    }

    // Vista desde el receptor (§5.1, spec 0003 D10): el mapa con que se ven
    // los miembros de un receptor de tipo 'receptor'. Es el de la entidad
    // (sus ancestros, ya instanciados desde ella) compuesto con el argumento
    // del propio receptor: desde Caja<String>, T de Caja es String. Vacío para
    // un receptor sin entidad (arreglo, primitivo, T) o sin argumento.
    public Map<OrigenParametroTipo, Tipo> instanciacionDe(Tipo receptor) {
        EntidadDeclarada entidad = entidadDe(receptor);
        if (entidad == null) {
            return Collections.emptyMap();
        }
        return Tipo.componerInstanciacion(
                entidad.getInstanciacion(), (OrigenParametroTipo) entidad, receptor.getArgumento());
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

    // Tercera pasada (spec 0003, D4): chequea los cuerpos. El controller la
    // llama sólo si no hubo errores léxicos, sintácticos ni de declaraciones
    // (sin cascada, D3), así que acá la tabla ya está consolidada y sana.
    // Recorre en orden de declaración (LinkedHashMap) y sólo clases del
    // programa: las interfaces no tienen cuerpos y las predefinidas no se
    // chequean. Se resetea la unidad actual porque el parser deja
    // metodoActual apuntando al último método parseado.
    public void chequearSentencias() {
        setUnidadActual(null, null, null);
        for (EntidadDeclarada entidad : clases.values()) {
            if (!(entidad instanceof Clase) || esPredefinida(entidad)) {
                continue;
            }
            Clase clase = (Clase) entidad;
            setClaseActual(clase);
            clase.chequearSentencias(this);
        }
    }

    // Unidad que se está chequeando: un método, un constructor o el
    // inicializador de un atributo. Quien la setea pasa null en los otros
    // dos, así nunca quedan dos campos a la vez.
    public void setUnidadActual(Metodo metodo, Constructor constructor, Atributo atributo) {
        metodoActual = metodo;
        constructorActual = constructor;
        atributoActual = atributo;
    }

    public Metodo getMetodoActual() {
        return metodoActual;
    }

    public Constructor getConstructorActual() {
        return constructorActual;
    }

    public Atributo getAtributoActual() {
        return atributoActual;
    }

    // Un método estático no tiene this: no admite el acceso implícito a un
    // atributo (§10.1). Constructores e inicializadores de atributo nunca son
    // estáticos (la gramática sólo admite static en métodos, §6.1).
    public boolean enContextoEstatico() {
        return metodoActual != null && metodoActual.isEstatico();
    }

    public void abrirBloque() {
        bloques.push(new HashMap<>());
    }

    public void cerrarBloque() {
        bloques.pop();
    }

    // Registra la local en el bloque más interno. El ocultamiento
    // (§10.1/§12.3) lo controla quien la declara, antes de llamar acá.
    public void declararLocal(VariableLocal local) {
        bloques.peek().put(local.getNombre().getLexema(), local);
    }

    // Resolución de un nombre suelto (spec 0003, D6), en este orden:
    // locales del bloque más interno hacia afuera, parámetros de la unidad
    // (un inicializador de atributo no tiene) y atributos de la clase
    // actual, que ya incluyen los heredados porque consolidar() los copia.
    // null si no aparece: quien llama reporta ERR_VAR_NO_DECLARADA.
    public Variable buscarVariable(String nombre) {
        for (Map<String, VariableLocal> bloque : bloques) {
            VariableLocal local = bloque.get(nombre);
            if (local != null) {
                return local;
            }
        }
        List<Parametro> parametros = metodoActual != null ? metodoActual.getParams()
                : constructorActual != null ? constructorActual.getParams()
                : Collections.emptyList();
        for (Parametro parametro : parametros) {
            if (parametro.getNombre().getLexema().equals(nombre)) {
                return parametro;
            }
        }
        return claseActual.getAtributo(nombre);
    }
}
