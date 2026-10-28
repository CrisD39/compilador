package Model.semantico;

import Model.lexico.Token;

import java.util.*;

public class Clase implements Chequeable, EntidadDeclarada, OrigenParametroTipo {
    private Token nombre;
    // Tipo nombrado en 'extends' (cabeza más argumento opcional, D2): se
    // valida como cualquier tipo referencia en estaBienDeclarado(). null si
    // la clase no escribe 'extends' (el padre efectivo es Object igual).
    private Tipo herencia;
    private HashMap<String, Atributo> atributos;
    // Atributos propios registrados hasta ahora: da el índice de declaración
    // del siguiente (spec 0005, D5).
    private int atributosDeclarados = 0;
    private HashMap<String, Metodo> metodos;
    private List<Constructor> constructores ;
    // Interfaz implementada: a lo sumo una (ADR 0005, la gramática ya no
    // admite lista en 'implements'). Es un Tipo por el mismo motivo que
    // 'herencia' (D2). null si la clase no implementa nada.
    private Tipo interfaz;
    private boolean consolidado = false;
    private boolean enCiclo = false;
    private final TablaSimbolos tablaSimbolos;
    private boolean sealed = false;
    private boolean nonSealed = false;
    private boolean esFinal = false;
    private LinkedHashSet<Token> permitidos = new LinkedHashSet<Token>();
    // Parámetro de tipo propio de esta clase (ej. el T de "class Caja<T>"):
    // 0 o 1 elemento (REQ-AS-010).
    private List<Token> parametrosTipo = new ArrayList<>();
    // true si 'extends' o 'implements' resolvió a un tipo sealed real (clase
    // o interfaz), sin importar si el 'permits' de ese tipo efectivamente
    // incluye a esta clase -- eso se chequea aparte (ERR_HERENCIA_NO_PERMITIDA/
    // ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR). Alcanza con esto para que
    // 'nonsealed' sea válido (Java real: el modificador solo exige tener AL
    // MENOS UN supertipo directo sealed, da igual si es por extends o por
    // implements).
    private boolean tieneSupertipoSellado = false;
    // Object, String o System (ADR 0004): las registra la tabla al
    // construirse, no pasan por el control de declaraciones y no se
    // descartan si el programa las redeclara (TablaSimbolos.insertarClase()).
    private boolean predefinida = false;

    public void setPredefinida(boolean predefinida) {
        this.predefinida = predefinida;
    }

    @Override
    public boolean isPredefinida() {
        return predefinida;
    }

    // Padre resuelto en la consolidación: el de 'extends' o, si no hay,
    // Object (ver consolidarHerencia()). Un 'extends' a un tipo inexistente
    // también deja Object (D7). null para Object y para una clase en un
    // ciclo de herencia (o que depende de uno).
    private Clase padreEfectivo;

    public Clase getPadreEfectivo() {
        return padreEfectivo;
    }

    // El padre efectivo como tipo de super (ver getTipoSuperclase()).
    private Tipo tipoSuperclase;

    // Instanciación genérica (spec 0001): para cada ancestro genérico, el
    // argumento con que esta clase lo hereda, visto desde acá (con Base<T>,
    // "class Dd extends Base<String>" deja Base -> String). Se compone en
    // consolidarHerencia() a partir del del padre (Tipo.componerInstanciacion()),
    // así cubre la cadena entera. Un ancestro heredado sin argumento no tiene
    // entrada: su parámetro se conserva (D5). Se usa para comparar
    // signaturas y, en el chequeo de cuerpos, para ver los miembros heredados
    // desde acá (TablaSimbolos.instanciacionDe()); los miembros copiados del
    // padre no se reescriben (§5.1).
    // Vacío para Object, para una clase en un ciclo y antes de consolidar.
    private Map<OrigenParametroTipo, Tipo> instanciacion = Collections.emptyMap();

    @Override
    public Map<OrigenParametroTipo, Tipo> getInstanciacion() {
        return Collections.unmodifiableMap(instanciacion);
    }

    // Esta clase como tipo, con su propio parámetro de tipo como argumento:
    // Caja<T> dentro de Caja<T>. Es el tipo de this (§11.3, semIICorrecto34).
    public Tipo comoTipo() {
        if (parametrosTipo.isEmpty()) {
            return new Tipo(nombre);
        }
        Token parametro = parametrosTipo.get(0);
        return new Tipo(nombre, Tipo.parametroDeTipo(parametro, this));
    }

    // Método con esa clave (nombre/aridad), propio o ya copiado de un
    // ancestro; null si no hay. Lo usa consolidarInterfaces() para buscar
    // en Object, y las llamadas del chequeo de sentencias.
    @Override
    public Metodo buscarMetodo(String clave) {
        return metodos.get(clave);
    }

    // Métodos propios y ya copiados de los ancestros. Lo usa
    // Interfaz.heredarDeObject() para recorrer los de Object.
    public Iterable<Metodo> getMetodos() {
        return metodos.values();
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public void setNonSealed(boolean nonSealed) {
        this.nonSealed = nonSealed;
    }

    public void setFinal(boolean esFinal) {
        this.esFinal = esFinal;
    }

    public boolean isFinal() {
        return esFinal;
    }

    public void agregarPermitido(Token permitido) {
        permitidos.add(permitido);
    }

    // Con una sola interfaz no hay repetición posible: el control de
    // interfaz repetida (ERR_INTERFAZ_DUPLICADA) queda sólo en el 'extends'
    // de una interfaz (Interfaz.agregarHerencia()).
    public void setInterfaz(Tipo interfaz) {
        this.interfaz = interfaz;
    }

    // Tal como se escribió (con su argumento), no resuelta: puede nombrar
    // algo que no existe o que no es una interfaz.
    public Tipo getInterfaz() {
        return interfaz;
    }

    public Clase(Token nombre, TablaSimbolos tablaSimbolos) {
        atributos = new HashMap<String,Atributo>();
        metodos = new HashMap<String,Metodo>();
        constructores = new LinkedList<Constructor>();
        this.nombre = nombre;
        this.tablaSimbolos = tablaSimbolos;
    }

    public String getName()
    {
        return nombre.getLexema();
    }

    public Token getNombre()
    {
        return nombre;
    }

    @Override
    public int getLinea() {
        return nombre.getLinea();
    }

    public void agregarAtributo(Atributo atributo)
    {
        String clave = atributo.getClave();
        if(atributos.containsKey(clave)){
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_ATRIBUTO_DUPLICADO", atributo.getNombre().getLinea(), atributo.getNombre().getLexema()));
        }
        else
        {
            atributo.setClaseDeclarante(this);
            atributo.setIndiceDeclaracion(atributosDeclarados++);
            atributos.put(clave,atributo);
        }
    }

    // Atributo con ese nombre, propio o ya copiado de un ancestro; null si no
    // hay. Lo usa TablaSimbolos.buscarVariable() (spec 0003, D6).
    public Atributo getAtributo(String nombre) {
        return atributos.get(nombre);
    }

    /** Reporta ERR_METODO_DUPLICADO si ya había un método con la misma clave
     *  (nombre/aridad, ver Metodo.getClave()) — key por clave, no por nombre,
     *  para que las sobrecargas por aridad convivan en el mapa (D4). */
    public void agregarMetodo(Metodo metodo)
    {
        String clave = metodo.getClave();
        if (metodos.containsKey(clave))
        {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_METODO_DUPLICADO", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            return;
        }
        metodo.setMiClase(this);
        metodos.put(clave, metodo);
    }

    // D3 (Parte I §6.3): un constructor se llama como su clase. Si no, se
    // reporta ERR_CONSTRUCTOR_NOMBRE_INVALIDO sobre el nombre escrito y NO
    // se registra: no entra al control de duplicados ni al de parámetros, y
    // si era el único la clase recibe el constructor por defecto. Corre al
    // parsear el constructor (lo llama el sintáctico), así que el error sale
    // en orden de aparición. Vale aunque la clase esté descartada por nombre
    // repetido: son errores independientes.
    public void agregarConstructor(Constructor constructor)
    {
        if (!constructor.getNombre().getLexema().equals(getName())) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_CONSTRUCTOR_NOMBRE_INVALIDO", constructor.getNombre().getLinea(), constructor.getNombre().getLexema()));
            return;
        }
        String clave = constructor.getClave();
        for (Constructor existente : constructores) {
            if (existente.getClave().equals(clave)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_CONSTRUCTOR_DUPLICADO", constructor.getNombre().getLinea(), constructor.getNombre().getLexema()));
                return;
            }
        }
        constructor.setClaseDeclarante(this);
        constructores.add(constructor);
    }

    // Constructor por defecto (Parte I §6.3, como Java): una clase sin
    // constructores registrados recibe uno sin parámetros, con el token del
    // nombre de la clase (así su clave y su nombre son los de cualquier
    // constructor propio). Una clase con al menos uno no lo recibe. Se
    // llama una vez terminado el parseo (TablaSimbolos.consolidar()), cuando
    // ya se sabe si quedó alguno registrado -- uno con nombre inválido no
    // cuenta (D3). Las predefinidas lo reciben al registrarse: Object y
    // String sí, System no (ADR 0004). Los constructores no se heredan, así
    // que consolidarConPadre() no los copia.
    public void agregarConstructorPorDefectoSiFalta() {
        if (constructores.isEmpty()) {
            Constructor porDefecto = new Constructor(nombre, new ArrayList<>());
            porDefecto.setClaseDeclarante(this);
            constructores.add(porDefecto);
        }
    }

    // Lista de solo lectura: se agregan sólo vía agregarConstructor() y
    // agregarConstructorPorDefectoSiFalta(), que aplican D3/D4.
    public List<Constructor> getConstructores() {
        return Collections.unmodifiableList(constructores);
    }

    public void setHerencia(Tipo herencia)
    {
        this.herencia = herencia;
    }

    // Tal como se escribió (con su argumento), no resuelta: para el padre
    // ya resuelto, getPadreEfectivo().
    public Tipo getHerencia() {
        return herencia;
    }

    // Tipo de super (logro Super!, spec 0006, D5): el 'extends' tal como se
    // escribió, con su argumento (A<String>), así el encadenado ve los
    // miembros instanciados desde ahí; sin 'extends', Object, el padre
    // efectivo. Se fija en consolidarHerencia() junto con el padre efectivo,
    // así no se arma un Object nuevo en cada super. Sólo lo usa el chequeo de
    // sentencias, que no corre si hubo errores de declaración: el 'extends'
    // existe, es una clase y no está en un ciclo. Una clase con 'implements'
    // no tiene 'extends' (ADR 0005), así que su superclase es Object, nunca
    // la interfaz. null antes de consolidar, para Object y en un ciclo.
    public Tipo getTipoSuperclase() {
        return tipoSuperclase;
    }

    public List<Token> getParametrosTipo() {
        return parametrosTipo;
    }

    public void setParametrosTipo(List<Token> parametrosTipo) {
        this.parametrosTipo = parametrosTipo;
    }

    // 'entornoGenericoExterno' llega siempre vacío -- las clases no están
    // anidadas en este lenguaje -- pero se mantiene por la firma uniforme de
    // Chequeable.
    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenericoExterno)
    {
        Map<String, OrigenParametroTipo> entornoClase = new HashMap<>();
        for (Token parametroTipo : parametrosTipo) {
            entornoClase.put(parametroTipo.getLexema(), this);
        }
        // Supertipos (D2, D7): misma validación que cualquier tipo referencia
        // (no declarado, no genérico, parámetro de tipo no declarado), en el
        // entorno de la clase -- en "class Bb<T> extends Caja<T>" el T es el
        // de Bb. Heredar sin argumento de un genérico es válido (§5). Un
        // supertipo inexistente se reporta sólo acá: la consolidación lo
        // saltea sin volver a reportarlo. Lo mismo vale para una relación
        // con el tipo equivocado (D1): 'extends' de una interfaz o
        // 'implements' de una clase.
        if (herencia != null) {
            herencia.estaBienDeclaradoComoSupertipo(tabla, entornoClase);
            tabla.chequearCategoriaSupertipo(herencia, true);
        }
        if (interfaz != null) {
            interfaz.estaBienDeclaradoComoSupertipo(tabla, entornoClase);
            tabla.chequearCategoriaSupertipo(interfaz, false);
        }
        // ADR 0006 (§10.4): la firma de un estático no ve los parámetros de
        // tipo de la clase, sólo los propios del método (que agrega
        // Metodo.estaBienDeclarado()). Un T de la clase queda fuera de
        // alcance y se reporta como ERR_TIPO_GENERICO_NO_DECLARADO.
        Map<String, OrigenParametroTipo> entornoEstatico = new HashMap<>();
        for(Metodo metodo: metodos.values())
        {
           metodo.estaBienDeclarado(tabla, metodo.isEstatico() ? entornoEstatico : entornoClase);
        }
        for(Atributo atributo: atributos.values())
        {
           atributo.estaBienDeclarado(tabla, entornoClase);
        }
        for(Constructor constructor: constructores){
           constructor.estaBienDeclarado(tabla, entornoClase);
        }
    }

    // Mezcla en esta clase los métodos/atributos heredados de 'padre' (ya
    // resuelto y validado por consolidarHerencia — acá no se vuelve a chequear
    // ciclo ni existencia). No sobreescribe lo que esta clase ya declaró.
    private void consolidarConPadre(Clase padre) {
        for (Metodo metodoHeredado : padre.metodos.values())
        {
            Metodo propio = metodos.get(metodoHeredado.getClave());
            if (propio != null)
            {
                chequearRedefinicion(propio, metodoHeredado);
            }
            else
            {
                // Se copian también los estáticos (§6.2): en Etapa 4,
                // A.debugPrint(3) tiene que encontrarlo en A. Como 'padre' ya
                // está consolidado, sus heredados ya vienen copiados y la
                // regla alcanza a cualquier ancestro, no sólo al directo
                // (semError82).
                metodos.put(metodoHeredado.getClave(), metodoHeredado);
            }
        }
        for (Atributo atributo : padre.atributos.values())
        {
            if (atributos.containsKey(atributo.getClave()))
            {
                Atributo propio = atributos.get(atributo.getClave());
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_ATRIBUTO_DUPLICADO", propio.getNombre().getLinea(), propio.getNombre().getLexema()));
            }
            else
            {
                atributos.put(atributo.getClave(), atributo);
            }
        }
    }

    // 'propio' (declarado en esta clase) tiene la clave de 'heredado' (de un
    // ancestro). Reglas de la Parte I §6.2, en este orden, una sola por
    // método (como javac, que reporta un único motivo por redefinición):
    // - Estáticos (ERR_REDEFINE_ESTATICO): si el heredado es estático, nadie
    //   abajo declara otro método con su clave, sea estático o de instancia
    //   (void debugPrint(int x) es un error en cualquier clase, semError58,
    //   81, 82); y un estático no puede tener la clave de un método de
    //   instancia heredado (semError59).
    // - Ambos de instancia: es una redefinición. Si el heredado es 'final',
    //   ERR_METODO_REDEFINE_FINAL (semError22, 42) -- con eso ya no importa
    //   la signatura. Si no, la signatura tiene que ser exactamente igual
    //   (ERR_REDEFINICION_INCOMPATIBLE, semError57, 60, 78).
    // Todo se reporta sobre el método de esta clase.
    // La signatura del heredado se compara instanciada desde esta clase:
    // "T get()" de Base<T> es "String get()" en "Dd extends Base<String>"
    // (semCorrecto25, 26). Heredado sin argumento, el T de Base se conserva y
    // no iguala ni a Object ni al T de esta clase (semError79, 61; D5).
    private void chequearRedefinicion(Metodo propio, Metodo heredado) {
        String codigo = null;
        if (heredado.isEstatico() || propio.isEstatico()) {
            codigo = "ERR_REDEFINE_ESTATICO";
        } else if (heredado.isFinal()) {
            codigo = "ERR_METODO_REDEFINE_FINAL";
        } else if (!propio.mismaSignatura(heredado, tipo -> tipo.sustituir(instanciacion))) {
            codigo = "ERR_REDEFINICION_INCOMPATIBLE";
        }
        if (codigo != null) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    codigo, propio.getNombre().getLinea(), propio.getNombre().getLexema()));
        }
    }

    // Contrato con la interfaz implementada (a lo sumo una, ADR 0005). La
    // interfaz ya trae, tras su consolidar(), los métodos que hereda de sus
    // superinterfaces, y los conflictos entre ellos los reporta ella misma
    // (Interfaz.consolidarHerencia()). Acá sólo se compara la clase -- sus
    // métodos propios y los heredados de su padre, ya copiados por
    // consolidarHerencia() -- contra cada método de la interfaz (spec 0001,
    // "Contratos e interfaces"; historias 55-60, 64):
    // - Falta la clave, o sólo hay un estático con ella (un estático no
    //   cumple un contrato, §6.2; semError83): ERR_METODO_INTERFAZ_NOIMPLEMENTADO
    //   sobre el método de la interfaz (semError32).
    // - Está la clave con otra signatura: ERR_REDEFINICION_INCOMPATIBLE sobre
    //   el método de la clase (semError64), como en la redefinición.
    // El método que cumple puede ser heredado: el toString de Object cumple
    // "String toString()" (semCorrecto30).
    // La signatura se compara instanciada desde esta clase: el argumento de
    // 'implements' compuesto con el mapa de la propia interfaz, que cubre sus
    // superinterfaces. Así "implements Proveedor<String>" pide
    // "String obtener()" (semCorrecto31), también a través de
    // "Lista<U> extends Fuente<U>" (semCorrecto34, semError88), y sin
    // argumento el T de la interfaz se conserva y no lo cumple Object
    // (semError71, D5). No hace falta unirle 'instanciacion': la gramática no
    // deja escribir 'extends' e 'implements' juntos, así que una clase con
    // interfaz tiene a Object (no genérico) como padre.
    private void consolidarInterfaces(){
        if (interfaz == null) {
            return;
        }
        EntidadDeclarada interfaceActual = tablaSimbolos.buscarInterfaz(interfaz.getToken().getLexema());
        if (interfaceActual == null) {
            // Ya reportado como ERR_TIPO_NO_DECLARADO en estaBienDeclarado()
            // (D7): la relación que no resuelve se saltea sin reportar.
            return;
        }
        if (!(interfaceActual instanceof Interfaz)) {
            // D1: 'implements' de una clase. Ya reportado como
            // ERR_HERENCIA_TIPO_INCORRECTO en estaBienDeclarado(); la
            // relación se ignora (no hay contrato que chequear).
            return;
        }
        Interfaz interfazResuelta = (Interfaz) interfaceActual;
        interfazResuelta.consolidar();
        // Mismo chequeo que consolidarHerencia() para 'extends', pero del
        // lado 'implements': una interfaz sealed también controla qué clases
        // pueden implementarla (Java real no distingue entre "subtipo por
        // extends" y "subtipo por implements" -- ambos son subtipos directos
        // y el permits aplica igual).
        if (interfazResuelta.isSealed()) {
            tieneSupertipoSellado = true;
            if (!interfazResuelta.permiteComoSubclase(nombre.getLexema())) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_HERENCIA_NO_PERMITIDA", nombre.getLinea(), nombre.getLexema()));
            } else if (!esFinal && !sealed && !nonSealed) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR", nombre.getLinea(), nombre.getLexema()));
            }
        }
        // Mismo criterio que el argumento de 'extends' (consolidarHerencia()):
        // sólo cuenta si la interfaz es genérica; si no, ERR_TIPO_NO_GENERICO
        // ya salió y no hay parámetro que reemplazar.
        Tipo argumentoDeLaInterfaz =
                interfazResuelta.getParametrosTipo().isEmpty() ? null : interfaz.getArgumento();
        Map<OrigenParametroTipo, Tipo> vistaContrato = Tipo.componerInstanciacion(
                interfazResuelta.getInstanciacion(), interfazResuelta, argumentoDeLaInterfaz);
        for (Metodo metodo : interfazResuelta.getMetodos()) {
            Metodo propio = metodos.get(metodo.getClave());
            Metodo deObject = tablaSimbolos.getObject().buscarMetodo(metodo.getClave());
            if (metodo == deObject) {
                // Lo copió la interfaz desde Object (Interfaz.heredarDeObject(),
                // spec 0002), no lo declaró. La clase siempre lo tiene por su
                // padre efectivo, y si lo redefine mal ya lo reporta
                // chequearRedefinicion(). Sin este salteo, una clase con
                // "static String toString()" sumaría un
                // ERR_METODO_INTERFAZ_NOIMPLEMENTADO sobre el token sintético
                // de Object, en la línea 0.
                continue;
            }
            if (propio == null || propio.isEstatico()) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_METODO_INTERFAZ_NOIMPLEMENTADO", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            } else if (deObject != null && !deObject.isEstatico()) {
                // La clave es la de un método de instancia de Object
                // (toString) que la interfaz redeclaró. El contrato ya quedó
                // decidido por las otras dos comparaciones: la interfaz contra
                // Object (Interfaz.heredarDeObject(), historia 62, error sobre
                // el método de la interfaz) y la clase contra Object, su padre
                // efectivo (chequearRedefinicion(), error sobre el método de
                // la clase). Repetirla acá daba un segundo error idéntico
                // sobre el método de la clase (semError94), o uno sobre el
                // toString de Object, en la línea 0 de un token sintético,
                // si la clase lo heredaba sin redefinirlo.
                continue;
            } else if (!propio.mismaSignatura(metodo, tipo -> tipo.sustituir(vistaContrato))) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_REDEFINICION_INCOMPATIBLE", propio.getNombre().getLinea(), propio.getNombre().getLexema()));
            }
        }
    }

    public void consolidar() {
        consolidarHerencia(new LinkedHashSet<Clase>());
        consolidarInterfaces();
        validarNonSealed();
        validarPermitidos();
    }

    // Tercera pasada (spec 0003, D4): chequea los cuerpos de los miembros
    // PROPIOS: atributos inicializados, constructores y métodos. Los
    // heredados son los mismos objetos del padre (consolidarConPadre()), y
    // chequearlos acá repetiría sus errores con la clase actual equivocada.
    // Cada unidad setea su campo de unidad actual y deja los otros dos en
    // null. Un cuerpo o inicializador null (todavía sin AST, o el constructor
    // por defecto) no tiene nada que chequear.
    // Las unidades de los tres tipos van en una sola lista ordenada por la
    // línea de su nombre (atributos y metodos son HashMap, su orden no es el
    // del fuente): con Multi-detección los errores salen en el orden en que
    // aparecen en el programa, aunque un constructor esté después de un
    // método (semIIMultiErrorError02). Ordenar cada tipo por separado no
    // alcanza.
    // El inicializador no pasa por un NodoBloque, así que atrapa su propio
    // error: un atributo mal inicializado no corta el resto de la clase.
    public void chequearSentencias(TablaSimbolos tabla) {
        List<UnidadAChequear> unidades = new ArrayList<>();
        // Los atributos se encolan por índice de declaración y no en el orden
        // del HashMap: el ordenamiento por línea es estable, así varios
        // atributos en una misma línea quedan en el orden del fuente
        // (Multi-detección, spec 0005, D5).
        List<Atributo> propios = new ArrayList<>();
        for (Atributo atributo : atributos.values()) {
            if (atributo.getClaseDeclarante() == this && atributo.getInicializador() != null) {
                propios.add(atributo);
            }
        }
        propios.sort(Comparator.comparingInt(Atributo::getIndiceDeclaracion));
        for (Atributo atributo : propios) {
            unidades.add(new UnidadAChequear(atributo.getNombre(), () -> {
                tabla.setUnidadActual(null, null, atributo);
                try {
                    atributo.chequearInicializador(tabla);
                } catch (ExcepcionSemantica e) {
                    tabla.registrarError(e);
                }
            }));
        }
        for (Constructor constructor : constructores) {
            if (constructor.getClaseDeclarante() != this || constructor.getCuerpo() == null) {
                continue;
            }
            unidades.add(new UnidadAChequear(constructor.getNombre(), () -> {
                tabla.setUnidadActual(null, constructor, null);
                constructor.getCuerpo().chequear(tabla);
            }));
        }
        for (Metodo metodo : metodos.values()) {
            if (metodo.getMiClase() != this || metodo.getCuerpo() == null) {
                continue;
            }
            unidades.add(new UnidadAChequear(metodo.getNombre(), () -> {
                tabla.setUnidadActual(metodo, null, null);
                metodo.exigirRetorno(tabla);
                metodo.getCuerpo().chequear(tabla);
            }));
        }
        unidades.sort(Comparator.comparingInt(u -> u.nombre().getLinea()));
        for (UnidadAChequear unidad : unidades) {
            unidad.chequeo().run();
        }
    }

    // Un miembro propio con algo que chequear: el nombre da la línea para
    // ordenar.
    private record UnidadAChequear(Token nombre, Runnable chequeo) {}

    // 'permits' solo se puebla si la clase es sealed (ver permitidosSealed()
    // en el parser), así que alcanza con iterar 'permitidos' sin guardia
    // aparte. Solo mira UN nivel (el nombre tiene que ser una subclase
    // DIRECTA), mismo criterio que el resto de los chequeos de sealed. Lee
    // el campo 'herencia' crudo de la clase permitida (ya poblado desde la
    // Pasada 1/2 del parser, sin depender de que esa clase ya haya corrido
    // su propio consolidar()) -- no hace falta esperar ningún orden.
    private void validarPermitidos() {
        for (Token permitido : permitidos) {
            EntidadDeclarada entidad = tablaSimbolos.buscarClase(permitido.getLexema());
            if (entidad == null) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_TIPO_NO_DECLARADO", permitido.getLinea(), permitido.getLexema()));
            } else if (!esSubclaseDirecta(entidad)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_PERMITS_NO_ES_SUBTIPO", permitido.getLinea(), permitido.getLexema()));
            }
        }
    }

    // Solo una Clase puede 'extends' de una Clase (una interfaz no puede)
    // -- por eso alcanza con mirar Clase.herencia, nunca Interfaz.herencias
    // ni Clase.interfaz.
    private boolean esSubclaseDirecta(EntidadDeclarada entidad) {
        if (!(entidad instanceof Clase)) {
            return false;
        }
        Tipo herenciaHija = ((Clase) entidad).getHerencia();
        return herenciaHija != null && herenciaHija.getToken().getLexema().equals(nombre.getLexema());
    }

    /*
        consolidarHerencia
        Camina la cadena de 'extends' de esta clase resolviendo el padre contra
        la tabla de símbolos (inyectada por constructor). 'camino' es la pila
        de recursión actual (las clases entre la raíz de este recorrido y
        'this'): si el padre ya está ahí, hay un ciclo.

        - Si el padre no existe en la tabla: no es un ciclo, y ya se reportó
          como tipo no declarado en estaBienDeclarado() (D7). Se saltea la
          relación sin reportar: la clase queda con Object como padre
          efectivo y marcada consolidada, así sus hijas no vuelven a
          tropezar con el abuelo inexistente.
        - Si el padre es una interfaz (D1): mismo tratamiento que el caso
          anterior; ERR_HERENCIA_TIPO_INCORRECTO ya salió en
          estaBienDeclarado().
        - Si el padre ya está en 'camino': es un ciclo. Se reporta una sola
          vez, sobre esta clase (la última visitada antes de repetir), y se
          marca 'enCiclo' en todo el tramo del camino que efectivamente cierra
          el ciclo — así, cuando TablaSimbolos.consolidar() le llegue el turno
          a cualquier otra clase de ese mismo ciclo (el recorrido llama a esto
          una vez por cada clase de la tabla, no una vez por ciclo), el guard
          de abajo corta antes de volver a caminar y volver a reportar.
        - 'consolidado'/'enCiclo' son memoization: si esta clase ya fue
          resuelta (bien o mal) en una pasada anterior, no se repite trabajo
          ni error.
     */
    public void consolidarHerencia(LinkedHashSet<Clase> camino)
    {
        if (consolidado || enCiclo)
        {
            return;
        }
        camino.add(this);
        Clase padre = null;
        // Argumento escrito en 'extends' (el String de "extends Base<String>"),
        // sólo si la relación resolvió a una clase genérica: con un padre no
        // genérico ya salió ERR_TIPO_NO_GENERICO (semError69) y el argumento
        // no tiene parámetro que reemplazar.
        Tipo argumentoDelPadre = null;
        if (herencia != null) {
            EntidadDeclarada padreEntidad = tablaSimbolos.buscarClase(herencia.getToken().getLexema());
            if (padreEntidad == null) {
                // D7: reportado una sola vez en estaBienDeclarado(); acá la
                // relación se ignora, como si no hubiera 'extends'.
                padre = tablaSimbolos.getObject();
            } else if (padreEntidad instanceof Interfaz) {
                // D1: 'extends' de una interfaz. Ya reportado como
                // ERR_HERENCIA_TIPO_INCORRECTO en estaBienDeclarado(); la
                // relación se ignora igual que la de arriba.
                padre = tablaSimbolos.getObject();
            } else {
                padre = (Clase) padreEntidad;
                if (!padre.getParametrosTipo().isEmpty()) {
                    argumentoDelPadre = herencia.getArgumento();
                }
            }
        } else if (this != tablaSimbolos.getObject()) {
            // Object implícito (Parte I §4): sin 'extends', el padre efectivo
            // es Object -- también si la clase implementa una interfaz. Se
            // resuelve sólo acá: 'herencia' sigue en null, porque sealed/
            // permits (esSubclaseDirecta) tiene que distinguir 'class A' de
            // 'class A extends Object'. Object no es final ni sealed y no
            // puede cerrar un ciclo, así que los chequeos de abajo no le
            // agregan errores; sólo copian toString/debugPrint.
            padre = tablaSimbolos.getObject();
        }
        if (padre != null) {
            if (camino.contains(padre)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_HERENCIA_CICLICA", nombre.getLinea(), nombre.getLexema()));
                marcarCicloDesde(padre, camino);
                camino.remove(this);
                return;
            }
            padre.consolidarHerencia(camino);
            if (padre.enCiclo) {
                enCiclo = true;
                camino.remove(this);
                return;
            }
            // 'final' en el padre: nadie puede extenderlo, sin excepción (no
            // hay 'permits' que valga -- la gramática ya impide que una
            // clase sea 'final' y 'sealed' a la vez).
            if (padre.esFinal) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_HERENCIA_CLASE_FINAL", nombre.getLinea(), nombre.getLexema()));
            } else if (padre.sealed) {
                tieneSupertipoSellado = true;
                // Chequeo de UN SOLO NIVEL: si el padre es sealed, el nombre
                // de esta clase tiene que estar en su 'permits'. Lo que pase
                // más abajo (si esta clase a su vez es nonsealed y cualquiera
                // la extiende) se resuelve solo, en la próxima llamada a
                // consolidarHerencia de ese nieto, contra ESTA clase — no
                // contra 'padre'.
                if (!padre.permiteComoSubclase(nombre.getLexema())) {
                    tablaSimbolos.agregarError(new ErrorSemantico(
                            "ERR_HERENCIA_NO_PERMITIDA", nombre.getLinea(), nombre.getLexema()));
                } else if (!esFinal && !sealed && !nonSealed) {
                    // Exhaustividad de Java real: toda subclase directa
                    // PERMITIDA por una sealed debe declararse final, sealed
                    // o nonsealed -- una 'class' lisa, aunque esté en la
                    // lista de permits, ya no alcanza.
                    tablaSimbolos.agregarError(new ErrorSemantico(
                            "ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR", nombre.getLinea(), nombre.getLexema()));
                }
            }
            padreEfectivo = padre;
            tipoSuperclase = herencia != null ? herencia : padre.comoTipo();
            // Antes de consolidarConPadre(): la redefinición compara con esta
            // vista. El padre ya está consolidado, así que su mapa está
            // completo.
            instanciacion = Tipo.componerInstanciacion(padre.instanciacion, padre, argumentoDelPadre);
            consolidarConPadre(padre);
        }
        consolidado = true;
        camino.remove(this);
    }

    // 'nonsealed' solo es valido si esta clase tiene AL MENOS UN supertipo
    // directo sealed real -- por 'extends' o por 'implements', da igual
    // (Java real: "modifier 'non-sealed' not allowed here" si no hay
    // ninguno). Se llama una sola vez, al final de consolidar(), después de
    // que tanto consolidarHerencia() como consolidarInterfaces() tuvieron
    // la oportunidad de marcar tieneSupertipoSellado.
    private void validarNonSealed() {
        if (nonSealed && !tieneSupertipoSellado) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_NONSEALED_SIN_PADRE_SEALED", nombre.getLinea(), nombre.getLexema()));
        }
    }

    // Compara por lexema, no por identidad: Token no sobrescribe equals()/
    // hashCode() (solo tiene toString()), así que cada Token de 'permitidos'
    // es una instancia distinta del lexer a la del nombre de la subclase
    // aunque el lexema coincida; Set.contains() con un Token nuevo nunca
    // matchearía.
    private boolean permiteComoSubclase(String lexemaHijo) {
        for (Token permitido : permitidos) {
            if (permitido.getLexema().equals(lexemaHijo)) {
                return true;
            }
        }
        return false;
    }

    // Marca enCiclo=true solo en el tramo del camino que cierra el ciclo
    // (desde el nodo repetido 'padre' hasta el final, en orden de inserción) —
    // las clases anteriores en 'camino' (si las hay) dependen de este ciclo
    // pero no forman parte de él, y se marcan por su cuenta al propagarse
    // 'enCiclo' hacia atrás en la recursión.
    private void marcarCicloDesde(Clase padre, LinkedHashSet<Clase> camino)
    {
        boolean dentroDelCiclo = false;
        for (Clase c : camino)
        {
            if (c == padre)
            {
                dentroDelCiclo = true;
            }
            if (dentroDelCiclo)
             {
                c.enCiclo = true;
            }
        }
    }
}
