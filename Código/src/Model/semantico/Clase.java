package Model.semantico;

import Model.lexico.Token;

import java.util.*;

public class Clase implements Chequeable, EntidadDeclarada {
    private Token nombre;
    private Token herencia;
    private HashMap<String, Atributo> atributos;
    private HashMap<String, Metodo> metodos;
    private List<Constructor> constructores ;
    private boolean tieneConstructor;
    // Interfaz implementada: a lo sumo una (ADR 0005, la gramática ya no
    // admite lista en 'implements'). null si la clase no implementa nada.
    private Token interfaz;
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

    public boolean isPredefinida() {
        return predefinida;
    }

    // Padre resuelto en la consolidación: el de 'extends' o, si no hay,
    // Object (ver consolidarHerencia()). null para Object, y para una clase
    // cuya herencia no se pudo resolver (padre inexistente o ciclo).
    private Clase padreEfectivo;

    public Clase getPadreEfectivo() {
        return padreEfectivo;
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
    public void setInterfaz(Token interfaz) {
        this.interfaz = interfaz;
    }

    public Token getInterfaz() {
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
        String firma = atributo.getFirma();
        if(atributos.containsKey(firma)){
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_ATRIBUTO_DUPLICADO", atributo.getNombre().getLinea(), atributo.getNombre().getLexema()));
        }
        else
        {
            atributos.put(firma,atributo);
        }
    }

    /** Devuelve false si ya había un método con la misma firma (nombre +
     *  tipos de parámetros, ver Metodo.getFirma()) — key por firma, no por
     *  nombre, para que las sobrecargas legítimas convivan en el mapa. */
    public void agregarMetodo(Metodo metodo)
    {
        String firma = metodo.getFirma();
        if (metodos.containsKey(firma))
        {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_METODO_DUPLICADO", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            return;
        }
        metodos.put(firma, metodo);
    }

    public void agregarConstructor(Constructor constructor)
    {
        String firma = constructor.getFirma();
        for (Constructor existente : constructores) {
            if (existente.getFirma().equals(firma)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_CONSTRUCTOR_DUPLICADO", constructor.getNombre().getLinea(), constructor.getNombre().getLexema()));
                return;
            }
        }
        constructores.add(constructor);
        tieneConstructor = true;
    }

    public void setHerencia(Token herencia)
    {
        this.herencia = herencia;
    }

    public Token getHerencia() {
        return herencia;
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
    public void estaBienDeclarado(TablaSimbolos tabla, Set<String> entornoGenericoExterno)
    {
        Set<String> entornoClase = new HashSet<>();
        for (Token parametroTipo : parametrosTipo) {
            entornoClase.add(parametroTipo.getLexema());
        }
        for(Metodo metodo: metodos.values())
        {
           metodo.estaBienDeclarado(tabla, entornoClase);
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
            if (metodos.containsKey(metodoHeredado.getFirma()))
            {
                // Esta clase ya declaró un método con la misma firma: lo está
                // redefiniendo. Si el heredado era 'final', eso es justo lo
                // que 'final' prohíbe -- chequeo de firmas, nada de cuerpo/
                // sentencias (esa etapa sigue sin implementar y no hace falta
                // para esto).
                if (metodoHeredado.isFinal()) {
                    Metodo propio = metodos.get(metodoHeredado.getFirma());
                    tablaSimbolos.agregarError(new ErrorSemantico(
                            "ERR_METODO_REDEFINE_FINAL", propio.getNombre().getLinea(), propio.getNombre().getLexema()));
                }
            }
            else
            {
                metodos.put(metodoHeredado.getFirma(), metodoHeredado);
            }
        }
        for (Atributo atributo : padre.atributos.values())
        {
            if (atributos.containsKey(atributo.getFirma()))
            {
                Atributo propio = atributos.get(atributo.getFirma());
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_ATRIBUTO_DUPLICADO", propio.getNombre().getLinea(), propio.getNombre().getLexema()));
            }
            else
            {
                atributos.put(atributo.getFirma(), atributo);
            }
        }
    }

    // Contrato con la interfaz implementada (a lo sumo una, ADR 0005). La
    // interfaz ya trae, tras su consolidar(), los métodos que hereda de sus
    // superinterfaces, y los conflictos entre ellos los reporta ella misma
    // (Interfaz.consolidarHerencia()). Acá sólo se compara la clase -- sus
    // métodos propios y los heredados de su padre, ya copiados por
    // consolidarHerencia() -- contra cada método de la interfaz: si falta la
    // firma, el método no está implementado; si está con otro retorno, es
    // el conflicto "override-equivalente" de Java (misma firma, retorno
    // incompatible).
    private void consolidarInterfaces(){
        if (interfaz == null) {
            return;
        }
        EntidadDeclarada interfaceActual = tablaSimbolos.buscarInterfaz(interfaz.getLexema());
        if (interfaceActual == null) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_INTERFAZ_NO_DECLARADA", interfaz.getLinea(), interfaz.getLexema()));
            return;
        }
        if (!(interfaceActual instanceof Interfaz)) {
            // 'implements' apuntando a algo que no es interfaz: fuera de
            // alcance por ahora (mismo criterio que el 'extends' de
            // Interfaz), ver casos-de-prueba.md 3.6.
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
        for (Metodo metodo : interfazResuelta.getMetodos()) {
            Metodo propio = metodos.get(metodo.getFirma());
            if (propio == null) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_METODO_INTERFAZ_NOIMPLEMENTADO", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            } else if (!Objects.equals(propio.getRetornoCanonico(), metodo.getRetornoCanonico())) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_METODO_RETORNO_INCOMPATIBLE", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            }
        }
    }

    public void consolidar() {
        consolidarHerencia(new LinkedHashSet<Clase>());
        consolidarInterfaces();
        validarNonSealed();
        validarPermitidos();
    }

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
        Token herenciaHija = ((Clase) entidad).getHerencia();
        return herenciaHija != null && herenciaHija.getLexema().equals(nombre.getLexema());
    }

    /*
        consolidarHerencia
        Camina la cadena de 'extends' de esta clase resolviendo el padre contra
        la tabla de símbolos (inyectada por constructor). 'camino' es la pila
        de recursión actual (las clases entre la raíz de este recorrido y
        'this'): si el padre ya está ahí, hay un ciclo.

        - Si el padre no existe en la tabla: no es un ciclo, es tipo no
          declarado.
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
        if (herencia != null) {
            EntidadDeclarada padreEntidad = tablaSimbolos.buscarClase(herencia.getLexema());
            if (padreEntidad == null) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_TIPO_NO_DECLARADO", herencia.getLinea(), herencia.getLexema()));
                camino.remove(this);
                return;
            }
            padre = (Clase) padreEntidad;
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
