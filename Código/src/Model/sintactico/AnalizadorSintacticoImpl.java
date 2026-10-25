package Model.sintactico;

import Model.lexico.AnalizadorLexico;
import Model.lexico.Token;
import Model.lexico.TokenType;
import Model.semantico.Atributo;
import Model.semantico.Clase;
import Model.semantico.Constructor;
import Model.semantico.Interfaz;
import Model.semantico.Metodo;
import Model.semantico.Parametro;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;
import Model.semantico.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;


public class AnalizadorSintacticoImpl implements AnalizadorSintactico {

    private final AnalizadorLexico lexico;

    /** Único token de lookahead: el próximo sin consumir. */
    private Token tokenActual;

    private final TablaSimbolos tablaSimbolo;

    public AnalizadorSintacticoImpl(AnalizadorLexico lex, TablaSimbolos tablaSimbolo) {
        this.lexico = lex;
        this.tablaSimbolo = tablaSimbolo;
    }

    // ------------------------------------------------------------------
    // Conjuntos FIRST usados para decidir de rama (los demás no terminales
    // se deciden mirando un único terminal, sin necesidad de un conjunto).
    // ------------------------------------------------------------------

    private static final Set<TokenType> PRIMEROS_TIPO_PRIMITIVO = EnumSet.of(
            TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT);

    // FIRST(<Tipo>) = FIRST(<TipoBase>)
    private static final Set<TokenType> PRIMEROS_TIPO = EnumSet.of(
            TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT,
            TokenType.ID_CLASE, TokenType.ID_GEN);

    // FIRST(<TipoMetodo>) = FIRST(<Tipo>) ∪ { void }
    private static final Set<TokenType> PRIMEROS_TIPO_METODO = EnumSet.of(
            TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT,
            TokenType.ID_CLASE, TokenType.ID_GEN, TokenType.PR_VOID);

    // FIRST(<Miembro>) = FIRST(<Visibilidad>) ∪ FIRST(<CuerpoMiembro>)
    //                  = { public, private } ∪ { static, void, < } ∪ FIRST(<Tipo>)
    // El '<' es FIRST de la rama nueva de método de instancia genérico
    // (<GenericidadOpcional> sin 'static' antes, ver cuerpoMiembro()).
    private static final Set<TokenType> PRIMEROS_MIEMBRO = EnumSet.of(
            TokenType.PR_PUBLIC, TokenType.PR_PRIVATE, TokenType.PR_FINAL, TokenType.PR_STATIC,
            TokenType.PR_VOID, TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT,
            TokenType.ID_CLASE, TokenType.ID_GEN, TokenType.OP_MENOR);

    // FIRST(<MetodoInterfaz>) = FIRST(<GenericidadOpcional>) ∪ FIRST(<TipoMetodo>)
    //                         = { < } ∪ PRIMEROS_TIPO_METODO
    private static final Set<TokenType> PRIMEROS_METODO_INTERFAZ = EnumSet.of(
            TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT,
            TokenType.ID_CLASE, TokenType.ID_GEN, TokenType.PR_VOID, TokenType.OP_MENOR);

    private static final Set<TokenType> PRIMEROS_PRIMITIVO = EnumSet.of(
            TokenType.PR_TRUE, TokenType.PR_FALSE, TokenType.LIT_INT,
            TokenType.LIT_CHAR, TokenType.PR_NULL);

    private static final Set<TokenType> PRIMEROS_OP_UNARIO = EnumSet.of(
            TokenType.OP_MAS, TokenType.OP_MENOS, TokenType.OP_NOT);

    private static final Set<TokenType> PRIMEROS_OP_BINARIO = EnumSet.of(
            TokenType.OP_OR, TokenType.OP_AND, TokenType.OP_IGUAL, TokenType.OP_DISTINTO,
            TokenType.OP_MENOR, TokenType.OP_MAYOR, TokenType.OP_MENOR_IGUAL,
            TokenType.OP_MAYOR_IGUAL, TokenType.OP_MAS, TokenType.OP_MENOS,
            TokenType.OP_MULT, TokenType.OP_DIV, TokenType.OP_MOD);

    // FIRST(<Operando>) = FIRST(<Primitivo>) ∪ FIRST(<Referencia>)
    private static final Set<TokenType> PRIMEROS_OPERANDO = EnumSet.of(
            TokenType.PR_TRUE, TokenType.PR_FALSE, TokenType.LIT_INT,
            TokenType.LIT_CHAR, TokenType.PR_NULL,
            TokenType.PR_THIS, TokenType.LIT_STRING, TokenType.ID_MET_VAR,
            TokenType.PR_NEW, TokenType.ID_CLASE, TokenType.PAR_A);

    // FIRST(<Expresion>) = FIRST(<OperadorUnario>) ∪ FIRST(<Operando>)
    private static final Set<TokenType> PRIMEROS_EXPRESION = EnumSet.of(
            TokenType.OP_MAS, TokenType.OP_MENOS, TokenType.OP_NOT,
            TokenType.PR_TRUE, TokenType.PR_FALSE, TokenType.LIT_INT,
            TokenType.LIT_CHAR, TokenType.PR_NULL,
            TokenType.PR_THIS, TokenType.LIT_STRING, TokenType.ID_MET_VAR,
            TokenType.PR_NEW, TokenType.ID_CLASE, TokenType.PAR_A);

    // FIRST(<Sentencia>) = { ; var return if while for { } ∪ FIRST(<Tipo>) ∪ FIRST(<Expresion>)
    // FIRST(<Tipo>) suma boolean/char/int e idGen (idClase ya viene de FIRST(<Expresion>)):
    // son el arranque de la declaración de variable local clásica (REQ-AS-006).
    private static final Set<TokenType> PRIMEROS_SENTENCIA = EnumSet.of(
            TokenType.PUNTO_COMA, TokenType.PR_VAR, TokenType.PR_RETURN,
            TokenType.PR_IF, TokenType.PR_WHILE, TokenType.PR_FOR, TokenType.LLAVE_A,
            TokenType.PR_BOOLEAN, TokenType.PR_CHAR, TokenType.PR_INT, TokenType.ID_GEN,
            TokenType.OP_MAS, TokenType.OP_MENOS, TokenType.OP_NOT,
            TokenType.PR_TRUE, TokenType.PR_FALSE, TokenType.LIT_INT,
            TokenType.LIT_CHAR, TokenType.PR_NULL,
            TokenType.PR_THIS, TokenType.LIT_STRING, TokenType.ID_MET_VAR,
            TokenType.PR_NEW, TokenType.ID_CLASE, TokenType.PAR_A);

    // No es un FIRST: es el conjunto de sincronización del modo pánico (REQ-AS-008).
    // Son justo los tokens que ya usan listaSentencias()/listaMiembros()/listaClases()/
    // bloque() para decidir si siguen o cortan — ver sincronizar().
    private static final Set<TokenType> TOKENS_SINCRONIZACION = EnumSet.of(
            TokenType.PUNTO_COMA, TokenType.LLAVE_A, TokenType.LLAVE_C, TokenType.EOF);

    // Los tres modificadores de <ListaClases> (sealed/nonsealed/final) -- se
    // usa para detectar cuando aparece OTRO de estos justo donde se espera
    // "class"/"interface" (ej. "sealed sealed class X", "final sealed final
    // class X"): sin este chequeo, match(PR_CLASS) fallaba con un error
    // genérico y el modo pánico se comía toda la declaración real como
    // basura hasta el próximo "{" -- ver errorModificadorRepetido().
    private static final Set<TokenType> MODIFICADORES_CLASE = EnumSet.of(
            TokenType.PR_SEALED, TokenType.PR_NONSEALED, TokenType.PR_FINAL);

    // ------------------------------------------------------------------
    // Infraestructura: start / match / lookahead / error
    // ------------------------------------------------------------------

    private final List<ErrorSintactico> errores = new ArrayList<>();

    /**
     * true justo después de que {@link #sincronizar()} consumió un ";" como
     * parte de recuperarse de un error ya reportado. Le avisa al
     * {@code match(PUNTO_COMA)} de cierre de la producción en curso que ese
     * delimitador ya fue consumido, para que no vuelva a exigirlo -- si lo
     * hiciera, generaría un eco del mismo error contra el primer token de lo
     * que venga después (posiblemente código válido). Cualquier avance real
     * de token la limpia: solo protege el primer match(PUNTO_COMA) que se
     * encuentre inmediatamente después.
     */
    private boolean delimitadorConsumidoPorRecuperacion = false;

    @Override
    public void start() {
        tokenActual = lexico.nextToken();
        inicial();
        // REQ-AS-004: <Inicial> ya consume el eof con su match(EOF) final.
    }

    @Override
    public List<ErrorSintactico> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    /** Consume {@code tokenActual} si es del tipo esperado; si no, error sintáctico. */
    private void match(TokenType esperado) {
        if (tokenActual.getTipo() == esperado) {
            avanzar();
        } else {
            error("\"" + esperado.getNombre() + "\"");
        }
    }

    /**
     * Cierra una producción con su ";" final (RestoDeclLocal, Sentencia,
     * RestoMiembro, etc.). Si el error() de un sub-parseo inmediatamente
     * anterior ya sincronizó consumiendo exactamente ese ";" -- porque el
     * token ofensivo YA era el delimitador que esta misma producción iba a
     * pedir -- no lo vuelve a exigir: ya fue consumido, y volver a exigirlo
     * generaría un eco del mismo error contra el primer token de lo que
     * venga después (posiblemente código perfectamente válido).
     */
    private void matchCierre(TokenType esperado) {
        if (delimitadorConsumidoPorRecuperacion) {
            delimitadorConsumidoPorRecuperacion = false;
            return;
        }
        match(esperado);
    }

    /** Pide el próximo token al léxico. Al llegar a EOF sigue devolviendo EOF. */
    private void avanzar() {
        delimitadorConsumidoPorRecuperacion = false;
        tokenActual = lexico.nextToken();
    }

    private boolean actualEs(TokenType t) {
        return tokenActual.getTipo() == t;
    }

    private boolean actualEn(Set<TokenType> conjunto) {
        return conjunto.contains(tokenActual.getTipo());
    }

    /**
     * Reporta el error (lo agrega a {@link #errores}, con el {@code tokenActual}
     * de este momento, ANTES de que {@link #sincronizar()} lo mueva) y recupera en
     * modo pánico (REQ-AS-008) para poder seguir el análisis y encontrar más de
     * un error por corrida.
     */
    private void error(String esperado) {
        registrarError(esperado);
        sincronizar();
    }

    private void registrarError(String esperado) {
        String encontrado = tokenActual.getTipo().getNombre()
                + " (\"" + tokenActual.getLexema() + "\")";
        errores.add(new ErrorSintactico(tokenActual.getLinea(), tokenActual.getLexema(), encontrado, esperado));
    }

    // Caso especial de error, sin sincronizar(): un segundo modificador
    // (sealed/nonsealed/final) donde se esperaba "class"/"interface" no se
    // trata como un token "basura" a saltear hasta el próximo "{" -- se
    // reporta un único mensaje claro y se deja el modificador repetido tal
    // cual, sin consumir nada. Así, cuando listaClases() recursivamente
    // vuelve a mirar el token actual, lo trata como un modificador nuevo
    // (sealed()/nonSealed()/finalClase() otra vez) y la declaración real que
    // sigue después se termina parseando bien, en vez de perderse en la
    // cascada de modo pánico.
    private void errorModificadorRepetido() {
        registrarError("\"class\" o \"interface\" (no se puede repetir ni combinar sealed/nonsealed/final)");
    }

    private void sincronizar() {
        if (!actualEn(TOKENS_SINCRONIZACION)) {
            avanzar();
            while (!actualEn(TOKENS_SINCRONIZACION)) {
                avanzar();
            }
        }
        if (actualEs(TokenType.PUNTO_COMA)) {
            avanzar();
            delimitadorConsumidoPorRecuperacion = true;
        }
    }

    // ==================================================================
    // Un método por no terminal (orden de la gramática LL(1) del documento)
    // ==================================================================

    // <Inicial> ::= <ListaClases> eof
    private void inicial() {
        listaClases();
        match(TokenType.EOF);
    }

    // <ListaClases> ::= <Sealed> <ListaClases> | <NonSealed> <ListaClases>
    //                  | <Final> <ListaClases>
    //                  | <Clase> <ListaClases> | <Interfaz> <ListaClases> | ϵ

    // <Sealed> ::= sealed class idClase <GenericidadOpcional> <HerenciaOpcional> permits <PermitidosSealed> { <ListaMiembros> }
    //            | sealed interface idClase <GenericidadOpcional> <ExtensionOpcional> permits <PermitidosSealed> { <ListaMetodosInterfaz> }
    // <PermitidosSealed>      ::= idClase <PermitidosSealedResto>
    // <PermitidosSealedResto> ::= , idClase <PermitidosSealedResto> | ϵ
    // <NonSealed> ::= nonsealed class idClase <GenericidadOpcional> <HerenciaOpcional> { <ListaMiembros> }
    //               | nonsealed interface idClase <GenericidadOpcional> <ExtensionOpcional> { <ListaMetodosInterfaz> }
    // <Final> ::= final class idClase <GenericidadOpcional> <HerenciaOpcional> { <ListaMiembros> }
    // 'final' solo aplica a clase (y método, dentro de <Miembro>) — a
    // diferencia de Java real, esta gramática no admite 'final interface':
    // no existe tal cosa en Java (las interfaces no se sellan con final), así
    // que "final interface X" cae en el else de finalClase() y termina en el
    // error sintáctico normal de clase() esperando "class" y encontrando
    // "interface" -- no hace falta un caso especial para rechazarlo.
    // 'sealed'/'nonsealed'/'final' son alternativas separadas al principio de
    // <ListaClases> (mismo mecanismo que ya separa <Clase>/<Interfaz>): como
    // cada una arranca con una palabra clave distinta, es sintácticamente
    // imposible escribir "sealed final class" — no hace falta ningún chequeo
    // semántico para prohibir esa combinación contradictoria, la gramática ya
    // la excluye por construcción.
    private void listaClases() {
        if (actualEs(TokenType.PR_SEALED)) {
            sealed();
            listaClases();
        } else if (actualEs(TokenType.PR_NONSEALED)) {
            nonSealed();
            listaClases();
        } else if (actualEs(TokenType.PR_FINAL)) {
            finalClase();
            listaClases();
        } else if (actualEs(TokenType.PR_CLASS)) {
            clase();
            listaClases();
        } else if (actualEs(TokenType.PR_INTERFACE)) {
            interfaz();
            listaClases();
        } else {
            // ϵ — no hace nada (FOLLOW = { eof })
        }
    }

    private void finalClase() {
        match(TokenType.PR_FINAL);
        if (actualEn(MODIFICADORES_CLASE)) {
            errorModificadorRepetido();
            return;
        }
        clase(true);
    }

    // <Sealed> ::= sealed <SealedClase> | sealed <SealedInterfaz>
    private void sealed() {
        match(TokenType.PR_SEALED);
        if (actualEn(MODIFICADORES_CLASE)) {
            errorModificadorRepetido();
            return;
        }
        if (actualEs(TokenType.PR_INTERFACE)) {
            sealedInterfaz();
        } else {
            sealedClase();
        }
    }

    private void sealedClase() {
        match(TokenType.PR_CLASS);
        Token nombreClase = tokenActual;
        match(TokenType.ID_CLASE);
        Clase claseActual = crearClase(nombreClase);
        claseActual.setSealed(true);
        tablaSimbolo.insertarClase(claseActual);
        claseActual.setParametrosTipo(genericidadOpcional());
        herenciaOpcional();
        permitidosSealed(claseActual);
        match(TokenType.LLAVE_A);
        listaMiembros();
        match(TokenType.LLAVE_C);
    }

    // Mismo mecanismo que sealedClase(), sobre <ExtensionOpcional> y
    // <ListaMetodosInterfaz> en vez de <HerenciaOpcional>/<ListaMiembros>.
    private void sealedInterfaz() {
        match(TokenType.PR_INTERFACE);
        Token nombreInterfaz = tokenActual;
        match(TokenType.ID_CLASE);
        Interfaz interfazActual = new Interfaz(nombreInterfaz, tablaSimbolo);
        interfazActual.setSealed(true);
        tablaSimbolo.insertarClase(interfazActual);
        interfazActual.setParametrosTipo(genericidadOpcional());
        extensionOpcional(interfazActual);
        permitidosSealedInterfaz(interfazActual);
        match(TokenType.LLAVE_A);
        listaMetodosInterfaz(interfazActual);
        match(TokenType.LLAVE_C);
    }

    private void permitidosSealed(Clase claseActual) {
        match(TokenType.PR_PERMITS);
        Token permitido = tokenActual;
        match(TokenType.ID_CLASE);
        claseActual.agregarPermitido(permitido);
        permitidosSealedResto(claseActual);
    }

    private void permitidosSealedResto(Clase claseActual) {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            Token permitido = tokenActual;
            match(TokenType.ID_CLASE);
            claseActual.agregarPermitido(permitido);
            permitidosSealedResto(claseActual);
        }
        // ϵ
    }

    // Mismas dos producciones que permitidosSealed()/permitidosSealedResto(),
    // pero acumulando sobre Interfaz en vez de Clase (ver esas para el
    // comentario de gramática).
    private void permitidosSealedInterfaz(Interfaz interfazActual) {
        match(TokenType.PR_PERMITS);
        Token permitido = tokenActual;
        match(TokenType.ID_CLASE);
        interfazActual.agregarPermitido(permitido);
        permitidosSealedRestoInterfaz(interfazActual);
    }

    private void permitidosSealedRestoInterfaz(Interfaz interfazActual) {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            Token permitido = tokenActual;
            match(TokenType.ID_CLASE);
            interfazActual.agregarPermitido(permitido);
            permitidosSealedRestoInterfaz(interfazActual);
        }
        // ϵ
    }

    // <NonSealed> ::= nonsealed <NonSealedClase> | nonsealed <NonSealedInterfaz>
    private void nonSealed() {
        match(TokenType.PR_NONSEALED);
        if (actualEn(MODIFICADORES_CLASE)) {
            errorModificadorRepetido();
            return;
        }
        if (actualEs(TokenType.PR_INTERFACE)) {
            nonSealedInterfaz();
        } else {
            nonSealedClase();
        }
    }

    private void nonSealedClase() {
        match(TokenType.PR_CLASS);
        Token nombreClase = tokenActual;
        match(TokenType.ID_CLASE);
        Clase claseActual = crearClase(nombreClase);
        claseActual.setNonSealed(true);
        tablaSimbolo.insertarClase(claseActual);
        claseActual.setParametrosTipo(genericidadOpcional());
        herenciaOpcional();
        match(TokenType.LLAVE_A);
        listaMiembros();
        match(TokenType.LLAVE_C);
    }

    private void nonSealedInterfaz() {
        match(TokenType.PR_INTERFACE);
        Token nombreInterfaz = tokenActual;
        match(TokenType.ID_CLASE);
        Interfaz interfazActual = new Interfaz(nombreInterfaz, tablaSimbolo);
        interfazActual.setNonSealed(true);
        tablaSimbolo.insertarClase(interfazActual);
        interfazActual.setParametrosTipo(genericidadOpcional());
        extensionOpcional(interfazActual);
        match(TokenType.LLAVE_A);
        listaMetodosInterfaz(interfazActual);
        match(TokenType.LLAVE_C);
    }
    // <Clase> ::= class idClase <GenericidadOpcional> <HerenciaOpcional> { <ListaMiembros> }
    // Pasada 1 del EDT (ver "Acciones semánticas sobre la gramática original"
    // en analizador-semantico.md): apenas se reconoce el nombre, se crea la
    // Clase (vacía) y se registra en la tabla. Herencia/miembros quedan para
    // la Pasada 2 (fuera de alcance de este pase).
    private void clase() {
        clase(false);
    }

    private void clase(boolean esFinal) {
        match(TokenType.PR_CLASS);
        Token nombreClase = tokenActual;
        match(TokenType.ID_CLASE);
        Clase claseActual = crearClase(nombreClase);
        claseActual.setFinal(esFinal);
        tablaSimbolo.insertarClase(claseActual);
        claseActual.setParametrosTipo(genericidadOpcional());
        herenciaOpcional();
        match(TokenType.LLAVE_A);
        listaMiembros();
        match(TokenType.LLAVE_C);
    }

    // <Interfaz> ::= interface idClase <GenericidadOpcional> <ExtensionOpcional> { <ListaMetodosInterfaz> }
    // Mismo mecanismo de Pasada 1 que <Clase>: registrar el nombre apenas se
    // conoce, comparte el mismo espacio de nombres que las clases.
    private void interfaz() {
        match(TokenType.PR_INTERFACE);
        Token nombreInterfaz = tokenActual;
        match(TokenType.ID_CLASE);
        Interfaz interfazActual = new Interfaz(nombreInterfaz, tablaSimbolo);
        tablaSimbolo.insertarClase(interfazActual);
        interfazActual.setParametrosTipo(genericidadOpcional());
        extensionOpcional(interfazActual);
        match(TokenType.LLAVE_A);
        listaMetodosInterfaz(interfazActual);
        match(TokenType.LLAVE_C);
    }

    // Construye la Clase y la deja como "clase actual" de la tabla (Estrategia A,
    // ver "Tabla de símbolos: por qué no usar Singleton" en analizador-semantico.md).
    // La inserción en la tabla queda a cargo de clase(), que es quien decide cómo
    // reportar un nombre repetido.
    private Clase crearClase(Token nombre) {
        Clase clase = new Clase(nombre, tablaSimbolo);
        tablaSimbolo.setClaseActual(clase);
        return clase;
    }

    // Construye el Metodo con placeholders (tipoRetorno=null, sin parámetros).
    // NO lo agrega todavía a la clase actual -- eso hay que hacerlo recién
    // después de parchear los parámetros reales (setParametrosAMetodoActual),
    // vía tablaSimbolo.agregarMetodoActualAClaseActual() en cada call site.
    // Antes se agregaba acá mismo, con el placeholder todavía en params=[]:
    // Clase.agregarMetodo() calcula getClave() (nombre/aridad) en ESE
    // momento para la key del mapa y el chequeo de ERR_METODO_DUPLICADO,
    // así que quedaba fijada como "nombre/0" para siempre -- mutar
    // metodo.parametros después no reindexa el HashMap. Resultado: dos
    // sobrecargas legítimas por aridad (ej. mostrar() y mostrar(int n))
    // colisionaban como si fueran el mismo método.
    // Devuelve el método para que quien lo crea le enganche el cuerpo al
    // terminar de parsear el <Bloque>.
    private Metodo crearMetodo(Token nombre, boolean estatico, boolean esFinal) {
        Metodo metodo = new Metodo(nombre, null, estatico, new ArrayList<>());
        metodo.setFinal(esFinal);
        tablaSimbolo.setMetodoActual(metodo);
        return metodo;
    }

    // <GenericidadOpcional> ::= < idGen > | ϵ
    // Un único parámetro de tipo (REQ-AS-010), compartida por clase,
    // interfaz y método. Devuelve lista (0 o 1 elemento) para que el modelo
    // trate igual "sin genericidad" y "con un parámetro".
    private List<Token> genericidadOpcional() {
        List<Token> parametrosTipo = new ArrayList<>();
        if (actualEs(TokenType.OP_MENOR)) {
            match(TokenType.OP_MENOR);
            parametrosTipo.add(tokenActual);
            match(TokenType.ID_GEN);
            match(TokenType.OP_MAYOR);
        }
        return parametrosTipo;
    }

    // <HerenciaOpcional> ::= extends <TipoReferencia> | implements <TipoReferencia> | ϵ
    // Una clase extiende como máximo una clase o implementa como máximo una
    // interfaz, y las dos relaciones son mutuamente excluyentes (ver
    // sintError47.java). La herencia múltiple vale sólo entre interfaces, en
    // <ExtensionOpcional> (ADR 0005): en "class X implements A, B" la coma
    // queda como token inesperado donde se esperaba '{' o 'permits'
    // (sintError52.java).
    private void herenciaOpcional() {
        if (actualEs(TokenType.PR_EXTENDS)) {
            match(TokenType.PR_EXTENDS);
            // El supertipo se guarda como Tipo completo, con su argumento
            // (D2): el semántico lo valida como cualquier tipo referencia.
            tablaSimbolo.getClaseActual().setHerencia(tipoReferencia());
        } else if (actualEs(TokenType.PR_IMPLEMENTS)) {
            match(TokenType.PR_IMPLEMENTS);
            tablaSimbolo.getClaseActual().setInterfaz(tipoReferencia());
        } else {
            // ϵ — no hace nada
        }
    }

    // <ExtensionOpcional>    ::= extends <ListaInterfaces> | ϵ
    // <ListaInterfaces>      ::= <TipoReferencia> <ListaInterfacesResto>
    // <ListaInterfacesResto> ::= , <TipoReferencia> <ListaInterfacesResto> | ϵ
    // Herencia múltiple de interfaces (Logro 2: "interface X extends Y, Z").
    // Es el único lugar de la gramática que admite una lista de supertipos
    // (ADR 0005). Cada entrada se acumula con Interfaz.agregarHerencia(),
    // que reporta la interfaz repetida (semError33/34).
    private void extensionOpcional(Interfaz interfazActual) {
        if (actualEs(TokenType.PR_EXTENDS)) {
            match(TokenType.PR_EXTENDS);
            listaInterfacesExtendidas(interfazActual);
        } else {
            // ϵ — no hace nada
        }
    }

    private void listaInterfacesExtendidas(Interfaz interfazActual) {
        interfazActual.agregarHerencia(tipoReferencia());
        listaInterfacesExtendidasResto(interfazActual);
    }

    private void listaInterfacesExtendidasResto(Interfaz interfazActual) {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            interfazActual.agregarHerencia(tipoReferencia());
            listaInterfacesExtendidasResto(interfazActual);
        }
        // ϵ
    }

    // <ListaMiembros> ::= <Miembro> <ListaMiembros> | ϵ
    private void listaMiembros() {
        if (actualEn(PRIMEROS_MIEMBRO)) {
            miembro();
            listaMiembros();
        } else {
            // ϵ — no hace nada
        }
    }

    // <ListaMetodosInterfaz> ::= <MetodoInterfaz> <ListaMetodosInterfaz> | ϵ
    private void listaMetodosInterfaz(Interfaz interfazActual) {
        if (actualEn(PRIMEROS_METODO_INTERFAZ)) {
            metodoInterfaz(interfazActual);
            listaMetodosInterfaz(interfazActual);
        } else {
            // ϵ — no hace nada
        }
    }

    // <Miembro> ::= <Visibilidad> <FinalOpcional> <CuerpoMiembro>
    private void miembro() {
        visibilidad();
        boolean esFinal = finalOpcional();
        cuerpoMiembro(esFinal);
    }

    // <Visibilidad> ::= public | private | ϵ
    private void visibilidad() {
        if (actualEs(TokenType.PR_PUBLIC)) {
            match(TokenType.PR_PUBLIC);
        } else if (actualEs(TokenType.PR_PRIVATE)) {
            match(TokenType.PR_PRIVATE);
        } else {
            // ϵ — no hace nada
        }
    }

    // <FinalOpcional> ::= final | ϵ
    private boolean finalOpcional() {
        if (actualEs(TokenType.PR_FINAL)) {
            match(TokenType.PR_FINAL);
            return true;
        }
        return false;
    }

    // <CuerpoMiembro> ::= static <GenericidadOpcional> <TipoMetodo> idMetVar <ArgsFormales> <Bloque>
    //                 |  <GenericidadOpcional> <TipoMetodo> idMetVar <ArgsFormales> <Bloque>   (sin 'static', dispara con '<')
    //                 |  void idMetVar <ArgsFormales> <Bloque>
    //                 |  <TipoPrimitivo> <DimensionesOpcionales> idMetVar <RestoMiembro>
    //                 |  idGen <DimensionesOpcionales> idMetVar <RestoMiembro>
    //                 |  idClase <TrasIdClaseMiembro>
    // El "public" que antes marcaba al constructor pasó a ser parte de
    // <Visibilidad>; el constructor (idClase <ArgsFormales> <Bloque>) queda con
    // el mismo prefijo "idClase" que un atributo/método de tipo clase, así que
    // se factoriza en profundidad en trasIdClaseMiembro() (ver "Factorización
    // de <Miembro>" en el documento).
    // esFinal viaja desde <FinalOpcional> (ya consumido en miembro()) hasta el
    // punto donde se sabe si esto termina siendo un método (se guarda en el
    // Metodo real) o un atributo/constructor (no hay dónde guardarlo todavía:
    // 'final' ahí es error sintáctico, ver restoMiembro()/trasIdClaseMiembro()).
    private void cuerpoMiembro(boolean esFinal) {
        if (actualEs(TokenType.PR_STATIC)) {
            match(TokenType.PR_STATIC);
            List<Token> parametrosTipoMetodo = genericidadOpcional();
            metodoConGenericidadPropia(true, esFinal, parametrosTipoMetodo);
        } else if (actualEs(TokenType.OP_MENOR)) {
            // Método de instancia con parámetro(s) de tipo propio(s):
            // <T> TipoMetodo idMetVar(...) {...}. Dispara directo con '<':
            // nunca puede terminar siendo atributo ni constructor, así que no
            // pasa por restoMiembro()/trasIdClaseMiembro().
            List<Token> parametrosTipoMetodo = genericidadOpcional();
            metodoConGenericidadPropia(false, esFinal, parametrosTipoMetodo);
        } else if (actualEs(TokenType.PR_VOID)) {
            match(TokenType.PR_VOID);
            Token nombreMetodo = tokenActual;
            match(TokenType.ID_MET_VAR);
            Metodo metodo = crearMetodo(nombreMetodo, false, esFinal);
            List<Parametro> params = argsFormales();
            tablaSimbolo.setParametrosAMetodoActual(params);
            tablaSimbolo.agregarMetodoActualAClaseActual();
            metodo.setCuerpo(bloque());
        } else if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
            Token tipoTok = tipoPrimitivo();
            int dimensiones = dimensionesOpcionales();
            Token nombreMiembro = tokenActual;
            match(TokenType.ID_MET_VAR);
            restoMiembro(nombreMiembro, new Tipo(tipoTok, null, dimensiones), esFinal);
        } else if (actualEs(TokenType.ID_GEN)) {
            Token tipoTok = tokenActual;
            match(TokenType.ID_GEN);
            int dimensiones = dimensionesOpcionales();
            Token nombreMiembro = tokenActual;
            match(TokenType.ID_MET_VAR);
            restoMiembro(nombreMiembro, new Tipo(tipoTok, null, dimensiones), esFinal);
        } else if (actualEs(TokenType.ID_CLASE)) {
            Token idClaseTok = tokenActual;
            match(TokenType.ID_CLASE);
            trasIdClaseMiembro(idClaseTok, esFinal);
        } else {
            error("un miembro de clase (\"static\", \"void\", \"<\" o un tipo)");
        }
    }

    // Cuerpo común a "static <GenericidadOpcional> <TipoMetodo> ..." y
    // "<GenericidadOpcional> <TipoMetodo> ..." (sin 'static') -- ambas ramas
    // solo difieren en 'estatico' y en si matchearon 'static' antes.
    private void metodoConGenericidadPropia(boolean estatico, boolean esFinal, List<Token> parametrosTipoMetodo) {
        Tipo tipoRetorno = tipoMetodo();
        Token nombreMetodo = tokenActual;
        match(TokenType.ID_MET_VAR);
        Metodo metodo = crearMetodo(nombreMetodo, estatico, esFinal);
        List<Parametro> params = argsFormales();
        tablaSimbolo.setTipoRetornoAMetodoActual(tipoRetorno);
        tablaSimbolo.setParametrosAMetodoActual(params);
        tablaSimbolo.setParametrosTipoAMetodoActual(parametrosTipoMetodo);
        tablaSimbolo.agregarMetodoActualAClaseActual();
        metodo.setCuerpo(bloque());
    }

    // <TrasIdClaseMiembro> ::= <ArgsFormales> <Bloque>
    //                       |  <TipoGenericoOpcional> <DimensionesOpcionales> idMetVar <RestoMiembro>
    // Tras "idClase", "(" => era el constructor (idClase <ArgsFormales> <Bloque>);
    // cualquier otra cosa => era un atributo o método cuyo tipo es esa clase
    // (Foo bar; | Foo<X> bar; | Foo bar(...) {...}), con <TipoGenericoOpcional>
    // y <DimensionesOpcionales> anulables hasta idMetVar.
    private void trasIdClaseMiembro(Token idClassToken, boolean esFinal) {
        if (actualEs(TokenType.PAR_A)) {
            // "(" pegado, es el constructor. 'final' no aplica a constructores
            // (no son métodos ni se redefinen) — igual que en atributo, error
            // sintáctico en vez de dejarlo pasar sin guardarlo en ningún lado.
            if (esFinal) {
                error("un método (\"final\" no se puede aplicar a un constructor)");
                return;
            }
            List<Parametro> params = argsFormales();
            Constructor constructor = new Constructor(idClassToken, params);
            constructor.setCuerpo(bloque());
            // D3: el nombre se controla al registrarlo, mientras se parsea
            // (Clase.agregarConstructor()); uno con otro nombre no se registra.
            tablaSimbolo.getClaseActual().agregarConstructor(constructor);
        } else {
            Tipo argumento = tipoGenericoOpcional();
            Tipo tipo = new Tipo(idClassToken, argumento, dimensionesOpcionales());
            Token nombreMiembro = tokenActual;
            match(TokenType.ID_MET_VAR);
            restoMiembro(nombreMiembro, tipo, esFinal);
        }
    }

    // <RestoMiembro> ::= ;
    //                |  <ArgsFormales> <Bloque>
    //                |  <OperadorAsignacion> <ExpresionCompuesta> ;
    // La tercera rama (REQ-AS-011) es un atributo con inicializador
    // (int x = 5;). FIRST disjuntos con las otras dos ({ ; } / { ( } / { = }):
    // no hace falta factorizar nada, "=" ya alcanza para decidir.
    // nombreMiembro/tipo llegan por parámetro (heredado) desde
    // cuerpoMiembro()/trasIdClaseMiembro(): son el tipo y el idMetVar ya
    // consumidos antes de saber si es atributo o método — recién acá se
    // construye el objeto (Estrategia B, ver analizador-semantico.md).
    private void restoMiembro(Token nombreMiembro, Tipo tipo, boolean esFinal) {
        if (actualEs(TokenType.PUNTO_COMA)) {
            // 'final' no aplica a atributos (solo clase/interfaz/método, ver
            // Logro 1 en analizador-semantico.md) — error sintáctico en vez de
            // dejarlo pasar sin guardarlo (Atributo no tiene campo 'final').
            if (esFinal) {
                error("un método (\"final\" no se puede aplicar a un atributo)");
                return;
            }
            match(TokenType.PUNTO_COMA);
            tablaSimbolo.getClaseActual().agregarAtributo(new Atributo(nombreMiembro, tipo));
        } else if (actualEs(TokenType.PAR_A)) {
            Metodo metodo = crearMetodo(nombreMiembro, false, esFinal);
            List<Parametro> params = argsFormales();
            tablaSimbolo.setParametrosAMetodoActual(params);
            tablaSimbolo.setTipoRetornoAMetodoActual(tipo);
            tablaSimbolo.agregarMetodoActualAClaseActual();
            metodo.setCuerpo(bloque());
        } else if (actualEs(TokenType.OP_ASIGN)) {
            if (esFinal) {
                error("un método (\"final\" no se puede aplicar a un atributo)");
                return;
            }
            operadorAsignacion();
            NodoExpresion inicializador = expresionCompuesta();
            matchCierre(TokenType.PUNTO_COMA);
            Atributo atributo = new Atributo(nombreMiembro, tipo);
            atributo.setInicializador(inicializador);
            tablaSimbolo.getClaseActual().agregarAtributo(atributo);
        } else {
            error("\";\", \"=\" (atributo) o \"(\" (método)");
        }
    }

    // <MetodoInterfaz> ::= <GenericidadOpcional> <TipoMetodo> idMetVar <ArgsFormales> ;
    // A diferencia de un método de clase (crearMetodo() + parcheo en dos
    // pasadas vía TablaSimbolos, porque ahí falta parsear el <Bloque>), acá
    // no hay cuerpo -- tipoRetorno/params ya están completos antes de
    // construir el Metodo, Estrategia B pura (mismo criterio que
    // Atributo/Parametro/Constructor).
    private void metodoInterfaz(Interfaz interfazActual) {
        List<Token> parametrosTipoMetodo = genericidadOpcional();
        Tipo tipoRetorno = tipoMetodo();
        Token nombreMetodo = tokenActual;
        match(TokenType.ID_MET_VAR);
        List<Parametro> params = argsFormales();
        matchCierre(TokenType.PUNTO_COMA);
        Metodo metodo = new Metodo(nombreMetodo, tipoRetorno, false, params);
        metodo.setParametrosTipoPropios(parametrosTipoMetodo);
        interfazActual.agregarMetodo(metodo);
    }

    // <TipoMetodo> ::= <Tipo> | void
    // null representa "void" (ver Metodo.tipoRetorno) -- no es un Tipo de valor.
    private Tipo tipoMetodo() {
        if (actualEs(TokenType.PR_VOID)) {
            match(TokenType.PR_VOID);
            return null;
        } else if (actualEn(PRIMEROS_TIPO)) {
            return tipo();
        } else {
            error("un tipo o \"void\"");
            return null;
        }
    }

    // <Tipo> ::= <TipoBase> <DimensionesOpcionales>
    // tipoBase() devuelve null tras un error sintáctico; las dimensiones se
    // consumen igual, para no cambiar la recuperación.
    private Tipo tipo() {
        Tipo t = tipoBase();
        int dimensiones = dimensionesOpcionales();
        return t == null ? null : t.conDimensiones(dimensiones);
    }

    // <TipoBase> ::= <TipoPrimitivo> | <TipoReferencia> | idGen
    private Tipo tipoBase() {
        if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
            return new Tipo(tipoPrimitivo());
        } else if (actualEs(TokenType.ID_CLASE)) {
            return tipoReferencia();
        } else if (actualEs(TokenType.ID_GEN)) {
            Token t = tokenActual;
            match(TokenType.ID_GEN);
            return new Tipo(t);
        } else {
            error("un tipo (primitivo, idClase o idGen)");
            return null;
        }
    }

    // <DimensionesOpcionales> ::= [ ] <DimensionesOpcionales> | ϵ
    // Devuelve cuántos "[]" consumió, para que Tipo distinga int[] de int.
    private int dimensionesOpcionales() {
        if (actualEs(TokenType.CORCHETE_A)) {
            match(TokenType.CORCHETE_A);
            match(TokenType.CORCHETE_C);
            return 1 + dimensionesOpcionales();
        } else {
            // ϵ
            return 0;
        }
    }

    // <TipoReferencia> ::= idClase <TipoGenericoOpcional>
    private Tipo tipoReferencia() {
        Token t = tokenActual;
        match(TokenType.ID_CLASE);
        return new Tipo(t, tipoGenericoOpcional());
    }

    // <TipoPrimitivo> ::= boolean | char | int
    private Token tipoPrimitivo() {
        Token t = tokenActual;
        if (actualEs(TokenType.PR_BOOLEAN)) {
            match(TokenType.PR_BOOLEAN);
        } else if (actualEs(TokenType.PR_CHAR)) {
            match(TokenType.PR_CHAR);
        } else if (actualEs(TokenType.PR_INT)) {
            match(TokenType.PR_INT);
        } else {
            error("\"boolean\", \"char\" o \"int\"");
        }
        return t;
    }

    // <TipoGenericoOpcional> ::= < <InstanciadoOParametrico> > | ϵ
    // Devuelve el argumento genérico, o null si no hay "<...>".
    private Tipo tipoGenericoOpcional() {
        if (actualEs(TokenType.OP_MENOR)) {
            match(TokenType.OP_MENOR);
            Tipo argumento = instanciadoOParametrico();
            match(TokenType.OP_MAYOR);
            return argumento;
        }
        return null;
    }

    // <TipoGenericoOpcionalNew> ::= < <DiamanteOTipo> > | ϵ
    // Variante de <TipoGenericoOpcional> sólo para <RestoNew>: acá sí se
    // admite el interior vacío (REQ-AS-010, notación diamante "<>"), porque
    // el diamante sólo es válido al instanciar (new Foo<>()), nunca en una
    // declaración de tipo — esas siguen usando tipoGenericoOpcional() sin
    // tocar, así que "Foo<> x;" sigue siendo error sintáctico.
    // Devuelve el argumento, o null sin "<...>" y con diamante: restoNew()
    // los distingue mirando si había "<".
    private Tipo tipoGenericoOpcionalNew() {
        if (actualEs(TokenType.OP_MENOR)) {
            match(TokenType.OP_MENOR);
            Tipo argumento = diamanteOTipo();
            match(TokenType.OP_MAYOR);
            return argumento;
        }
        return null;
    }

    // <DiamanteOTipo> ::= <InstanciadoOParametrico> | ϵ      (ϵ = "<>")
    private Tipo diamanteOTipo() {
        if (actualEs(TokenType.ID_GEN) || actualEs(TokenType.ID_CLASE)) {
            return instanciadoOParametrico();
        }
        // ϵ (notación diamante: new Foo<>())
        return null;
    }

    // <InstanciadoOParametrico> ::= idGen | idClase <TipoGenericoOpcional>
    // El idGen no anida (un parámetro de tipo no puede llevar su propio
    // argumento); el idClase sí, vía <TipoGenericoOpcional> (REQ-AS-010:
    // genéricos anidados, p. ej. Caja<Lista<Item>>). El cierre "Item>>" tokeniza
    // como dos OP_MAYOR sueltos (estadoMayor() sólo combina con "="), así que
    // cada nivel de anidamiento consume el suyo sin tratamiento especial.
    // Devuelve el argumento como Tipo (recursivo) para que el semántico pueda
    // validar cada nivel (Logro 5).
    private Tipo instanciadoOParametrico() {
        Token t = tokenActual;
        if (actualEs(TokenType.ID_GEN)) {
            match(TokenType.ID_GEN);
            return new Tipo(t);
        } else if (actualEs(TokenType.ID_CLASE)) {
            match(TokenType.ID_CLASE);
            return new Tipo(t, tipoGenericoOpcional());
        } else {
            error("idGen o idClase");
            return null;
        }
    }

    // <ArgsFormales> ::= ( <ListaArgsFormalesOpcional> )
    private List<Parametro> argsFormales() {
        match(TokenType.PAR_A);
        List<Parametro> parametros = listaArgsFormalesOpcional();
        match(TokenType.PAR_C);
        return parametros;
    }

    // <ListaArgsFormalesOpcional> ::= <ListaArgsFormales> | ϵ
    private List<Parametro> listaArgsFormalesOpcional() {
        if (actualEn(PRIMEROS_TIPO)) {
            return listaArgsFormales();
        }
        return new ArrayList<>();
    }

    // <ListaArgsFormales> ::= <ArgFormal> <ListaArgsFormalesResto>
    // A <ListaArgsFormalesResto> se le eliminó la recursión a izquierda: el
    // acumulador se arrastra a derecha y se cierra en el caso base ϵ (ver "De
    // la gramática original a la LL(1)" en analizador-semantico.md).
    private List<Parametro> listaArgsFormales() {
        Parametro primero = argFormal();
        List<Parametro> resto = listaArgsFormalesResto();
        resto.add(0, primero);
        return resto;
    }

    // <ListaArgsFormalesResto> ::= , <ArgFormal> <ListaArgsFormalesResto> | ϵ
    private List<Parametro> listaArgsFormalesResto() {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            Parametro p = argFormal();
            List<Parametro> resto = listaArgsFormalesResto();
            resto.add(0, p);
            return resto;
        }
        return new ArrayList<>();
    }

    // <ArgFormal> ::= <Tipo> idMetVar
    // Tras un error sintáctico en <Tipo>, el Parametro queda con tipo null
    // (tipo() devuelve null); Parametro.estaBienDeclarado() lo saltea.
    private Parametro argFormal() {
        Tipo tipo = tipo();
        Token nombre = tokenActual;
        match(TokenType.ID_MET_VAR);
        return new Parametro(nombre, tipo);
    }

    // <Bloque> ::= { <ListaSentencias> }
    // Cada método de cuerpos devuelve su nodo a quien lo contiene (estrategia
    // B, un único punto de attach): el NodoBloque de un método o constructor
    // lo engancha quien llamó a bloque(), y uno anidado queda como sentencia.
    private NodoBloque bloque() {
        match(TokenType.LLAVE_A);
        NodoBloque nodo = new NodoBloque();
        listaSentencias(nodo);
        match(TokenType.LLAVE_C);
        return nodo;
    }

    // <ListaSentencias> ::= <Sentencia> <ListaSentencias> | ϵ
    // Una sentencia con error sintáctico no tiene nodo (sentencia() devuelve
    // null) y no se agrega: con errores sintácticos el AST no se chequea (D8).
    private void listaSentencias(NodoBloque bloque) {
        if (actualEn(PRIMEROS_SENTENCIA)) {
            NodoSentencia sentencia = sentencia();
            if (sentencia != null) {
                bloque.agregarSentencia(sentencia);
            }
            listaSentencias(bloque);
        } else {
            // ϵ — no hace nada
        }
    }

    // <Sentencia> ::= ;
    //             |  <VarLocal> ;                        (var idMetVar = ...)
    //             |  <TipoPrimitivo> <RestoDeclLocal>    \
    //             |  idGen <RestoDeclLocal>              |  variable local clásica (REQ-AS-006)
    //             |  idClase <SentIdClase>              /
    //             |  <Return> ;
    //             |  <If>
    //             |  <While>
    //             |  <For>
    //             |  <Bloque>
    //             |  <Expresion> ;                       (sólo si el lookahead ∈ FIRST(<Expresion>) \ { idClase })
    private NodoSentencia sentencia() {
        if (actualEs(TokenType.PUNTO_COMA)) {
            match(TokenType.PUNTO_COMA);
            return new NodoSentenciaVacia();
        } else if (actualEs(TokenType.PR_VAR)) {
            NodoVarLocal nodo = varLocal();
            matchCierre(TokenType.PUNTO_COMA);
            return nodo;
        } else if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
            return restoDeclLocal(new Tipo(tipoPrimitivo()));
        } else if (actualEs(TokenType.ID_GEN)) {
            Token tipoTok = tokenActual;
            match(TokenType.ID_GEN);
            return restoDeclLocal(new Tipo(tipoTok));
        } else if (actualEs(TokenType.ID_CLASE)) {
            // idClase se intercepta acá: puede abrir una declaración local
            // (<Tipo> idMetVar ...) o una expresión (idClase . idMetVar (...)).
            // Un token más lo decide, en sentIdClase().
            Token idClase = tokenActual;
            match(TokenType.ID_CLASE);
            return sentIdClase(idClase);
        } else if (actualEs(TokenType.PR_RETURN)) {
            NodoReturn nodo = sentenciaReturn();
            matchCierre(TokenType.PUNTO_COMA);
            return nodo;
        } else if (actualEs(TokenType.PR_IF)) {
            return sentenciaIf();
        } else if (actualEs(TokenType.PR_WHILE)) {
            return sentenciaWhile();
        } else if (actualEs(TokenType.PR_FOR)) {
            return sentenciaFor();
        } else if (actualEs(TokenType.LLAVE_A)) {
            return bloque();
        } else if (actualEn(PRIMEROS_EXPRESION)) {
            // idClase ya quedó tomado por la rama de arriba; acá sólo entra el
            // resto de FIRST(<Expresion>).
            return cerrarSentenciaExpresion(expresion());
        } else {
            error("una sentencia");
            return null;
        }
    }

    // El ";" que cierra una <Expresion> usada como sentencia (en <Sentencia>,
    // en la rama "." de <SentIdClase> y en la inicialización del for). Se
    // guarda porque es el lexema de ERR_SENTENCIA_INVALIDA.
    private NodoSentenciaExpresion cerrarSentenciaExpresion(NodoExpresion expresion) {
        Token puntoYComa = tokenActual;
        matchCierre(TokenType.PUNTO_COMA);
        return new NodoSentenciaExpresion(expresion, puntoYComa);
    }

    // <VarLocal> ::= var idMetVar = <ExpresionCompuesta>
    private NodoVarLocal varLocal() {
        Token var = tokenActual;
        match(TokenType.PR_VAR);
        Token nombre = tokenActual;
        match(TokenType.ID_MET_VAR);
        match(TokenType.OP_ASIGN);
        return new NodoVarLocal(var, nombre, expresionCompuesta());
    }

    // <SentIdClase> ::= <TipoGenericoOpcional> <RestoDeclLocal>
    //               |  . idMetVar <ArgsActuales> <ReferenciaResto> <PostfijoOpcional> <ExpresionCompuestaResto>
    //                  <TernarioOpcional> <RestoAsignacion> ;
    // El "idClase" ya fue consumido por sentencia(). Si sigue ".", era una
    // llamada a método estático (expresión); si no, es una declaración local
    // con tipo clase (`Foo x;`, `Foo<Bar> x = ...;`). La rama "." reconstruye
    // la cola de <Expresion> que arrancaba en idClase . idMetVar <ArgsActuales>,
    // con el postfijo y el ternario incluidos (⚠13): tiene que dar el mismo
    // árbol que el camino normal, y sin ellos A1.m().x++; era error sintáctico.
    private NodoSentencia sentIdClase(Token idClase) {
        if (actualEs(TokenType.PUNTO)) {
            match(TokenType.PUNTO);
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            NodoLlamadaEstatica llamada = new NodoLlamadaEstatica(idClase, nombre, argsActuales());
            return cerrarSentenciaExpresion(colaDeExpresion(conReferenciaResto(llamada)));
        } else {
            return restoDeclLocal(new Tipo(idClase, tipoGenericoOpcional()));
        }
    }

    // Cola de <Expresion> después de un operando ya armado:
    // <PostfijoOpcional> <ExpresionCompuestaResto> <TernarioOpcional> <RestoAsignacion>.
    // La usan los caminos factorizados que consumieron el prefijo para
    // decidir (<SentIdClase>, <TrasIdClaseFor>, <TrasParenId>), en el mismo
    // orden que expresion() -> expresionCompuesta() -> expresionBasica().
    private NodoExpresion colaDeExpresion(NodoExpresion operando) {
        NodoExpresion expresion = postfijoOpcional(operando);
        expresion = expresionCompuestaResto(expresion);
        expresion = ternarioOpcional(expresion);
        return restoAsignacion(expresion);
    }

    // <RestoDeclLocal> ::= idMetVar <MasIdsLocal> <InitLocalOpc> ;
    // Cola de una declaración local clásica, después del tipo base. Un único
    // "= <ExpresionCompuesta>" al final vale sólo para el último nombre, como
    // en Java (`int x, y, z = 10;`, ⚠7).
    private NodoVarLocalClasica restoDeclLocal(Tipo tipo) {
        Token primero = tokenActual;
        match(TokenType.ID_MET_VAR);
        List<Token> nombres = masIdsLocal();
        nombres.add(0, primero);
        NodoExpresion inicializador = initLocalOpc();
        matchCierre(TokenType.PUNTO_COMA);
        return new NodoVarLocalClasica(tipo, nombres, inicializador);
    }

    // <MasIdsLocal> ::= , idMetVar <MasIdsLocal> | ϵ
    private List<Token> masIdsLocal() {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            List<Token> resto = masIdsLocal();
            resto.add(0, nombre);
            return resto;
        }
        return new ArrayList<>();
    }

    // <InitLocalOpc> ::= = <ExpresionCompuesta> | ϵ
    // null si no hay inicializador.
    private NodoExpresion initLocalOpc() {
        if (actualEs(TokenType.OP_ASIGN)) {
            match(TokenType.OP_ASIGN);
            return expresionCompuesta();
        }
        return null;
    }

    // <Return> ::= return <ExpresionOpcional>
    private NodoReturn sentenciaReturn() {
        Token tokenReturn = tokenActual;
        match(TokenType.PR_RETURN);
        return new NodoReturn(tokenReturn, expresionOpcional());
    }

    // <ExpresionOpcional> ::= <Expresion> | ϵ
    // null en "return;".
    private NodoExpresion expresionOpcional() {
        if (actualEn(PRIMEROS_EXPRESION)) {
            return expresion();
        }
        return null;
    }

    // <If> ::= if ( <Expresion> ) <Sentencia> <ElseOpcional>
    private NodoIf sentenciaIf() {
        Token tokenIf = tokenActual;
        match(TokenType.PR_IF);
        match(TokenType.PAR_A);
        NodoExpresion condicion = expresion();
        match(TokenType.PAR_C);
        NodoSentencia entonces = sentencia();
        return new NodoIf(tokenIf, condicion, entonces, elseOpcional());
    }

    // <ElseOpcional> ::= else <Sentencia> | ϵ
    // Conflicto FIRST/FOLLOW deliberado sobre "else" (else colgante): con lookahead
    // "else" se toma siempre la rama else -> el else liga con el if más cercano.
    // null sin else (⚠8).
    private NodoSentencia elseOpcional() {
        if (actualEs(TokenType.PR_ELSE)) {
            match(TokenType.PR_ELSE);
            return sentencia();
        }
        // ϵ (reduce por ϵ; el "else" liga con el if más cercano)
        return null;
    }

    // <While> ::= while ( <Expresion> ) <Sentencia>
    private NodoWhile sentenciaWhile() {
        Token tokenWhile = tokenActual;
        match(TokenType.PR_WHILE);
        match(TokenType.PAR_A);
        NodoExpresion condicion = expresion();
        match(TokenType.PAR_C);
        return new NodoWhile(tokenWhile, condicion, sentencia());
    }

    // <For> ::= for ( <ClausulasFor> ) <Sentencia>
    // El cuerpo es <Sentencia> (no <Bloque>), igual que <If>/<While>: admite
    // tanto una sentencia simple sin llaves como un bloque.
    // <ClausulasFor> decide si es un for clásico o un for-each, así que el
    // nodo sale de ahí y el cuerpo se le setea después de leerlo.
    private NodoSentencia sentenciaFor() {
        Token tokenFor = tokenActual;
        match(TokenType.PR_FOR);
        match(TokenType.PAR_A);
        NodoSentencia nodo = clausulasFor(tokenFor);
        match(TokenType.PAR_C);
        NodoSentencia cuerpo = sentencia();
        if (nodo instanceof NodoFor nodoFor) {
            nodoFor.setCuerpo(cuerpo);
        } else if (nodo instanceof NodoForEach nodoForEach) {
            nodoForEach.setCuerpo(cuerpo);
        }
        return nodo;
    }

    // <ClausulasFor> ::= ; <CondFor> ; <ActFor>
    //                 |  <VarLocal> ; <CondFor> ; <ActFor>
    //                 |  <TipoPrimitivo> idMetVar <TrasIdForTipo>
    //                 |  idGen idMetVar <TrasIdForTipo>
    //                 |  idClase <TrasIdClaseFor>
    //                 |  <Expresion> ; <CondFor> ; <ActFor>
    // idClase se intercepta antes que el resto de <Expresion> por la misma
    // razón que en sentencia()/<SentIdClase>: tras "idClase" puede seguir un
    // tipo (declaración clásica o for-each) o el resto de una llamada estática
    // (expresión de inicialización, p. ej. Fabrica.reset()); un token más lo
    // decide en trasIdClaseFor().
    // La rama var es la declaración que pide el logro For para el for
    // clásico; var no está en ningún otro FIRST, así que sigue LL(1). El
    // for-each con var no se admite (sólo tipo explícito).
    // Devuelve un NodoFor o un NodoForEach todavía sin cuerpo.
    private NodoSentencia clausulasFor(Token tokenFor) {
        if (actualEs(TokenType.PUNTO_COMA)) {
            match(TokenType.PUNTO_COMA);
            return restoForClasico(tokenFor, null);
        } else if (actualEs(TokenType.PR_VAR)) {
            NodoVarLocal declaracion = varLocal();
            matchCierre(TokenType.PUNTO_COMA);
            return restoForClasico(tokenFor, declaracion);
        } else if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
            Tipo tipo = new Tipo(tipoPrimitivo());
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            return trasIdForTipo(tokenFor, tipo, nombre);
        } else if (actualEs(TokenType.ID_GEN)) {
            Tipo tipo = new Tipo(tokenActual);
            match(TokenType.ID_GEN);
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            return trasIdForTipo(tokenFor, tipo, nombre);
        } else if (actualEs(TokenType.ID_CLASE)) {
            Token idClase = tokenActual;
            match(TokenType.ID_CLASE);
            return trasIdClaseFor(tokenFor, idClase);
        } else if (actualEn(PRIMEROS_EXPRESION)) {
            return restoForClasico(tokenFor, cerrarSentenciaExpresion(expresion()));
        } else {
            error("\";\", var, un tipo o una expresión (inicialización del \"for\")");
            return null;
        }
    }

    // <CondFor> ; <ActFor>: lo que sigue a la inicialización (ya cerrada con
    // su ";") en todas las formas del for clásico.
    private NodoFor restoForClasico(Token tokenFor, NodoSentencia inicializacion) {
        NodoExpresion condicion = condFor();
        matchCierre(TokenType.PUNTO_COMA);
        return new NodoFor(tokenFor, inicializacion, condicion, actFor());
    }

    // <TrasIdForTipo> ::= : <Expresion>                          (for-each)
    //                  |  <InitLocalOpc> ; <CondFor> ; <ActFor>  (for clásico con declaración)
    // La declaración del for clásico es una local clásica de un solo nombre
    // (⚠9).
    private NodoSentencia trasIdForTipo(Token tokenFor, Tipo tipo, Token nombre) {
        if (actualEs(TokenType.DOS_PUNTOS)) {
            match(TokenType.DOS_PUNTOS);
            return new NodoForEach(tokenFor, tipo, nombre, expresion());
        } else {
            NodoExpresion inicializador = initLocalOpc();
            matchCierre(TokenType.PUNTO_COMA);
            NodoVarLocalClasica declaracion = new NodoVarLocalClasica(tipo, List.of(nombre), inicializador);
            return restoForClasico(tokenFor, declaracion);
        }
    }

    // <TrasIdClaseFor> ::= <TipoGenericoOpcional> idMetVar <TrasIdForTipo>
    //                   |  . idMetVar <ArgsActuales> <ReferenciaResto> <PostfijoOpcional> <ExpresionCompuestaResto>
    //                      <TernarioOpcional> <RestoAsignacion> ; <CondFor> ; <ActFor>
    // "." => era una llamada estática como expresión de inicialización; misma
    // reconstrucción de cola de <Expresion> que usa <SentIdClase> (⚠13).
    // Cualquier otra cosa => era un tipo clase (declaración o for-each).
    private NodoSentencia trasIdClaseFor(Token tokenFor, Token idClase) {
        if (actualEs(TokenType.PUNTO)) {
            match(TokenType.PUNTO);
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            NodoLlamadaEstatica llamada = new NodoLlamadaEstatica(idClase, nombre, argsActuales());
            NodoSentencia inicializacion = cerrarSentenciaExpresion(colaDeExpresion(conReferenciaResto(llamada)));
            return restoForClasico(tokenFor, inicializacion);
        } else {
            Tipo tipo = new Tipo(idClase, tipoGenericoOpcional());
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            return trasIdForTipo(tokenFor, tipo, nombre);
        }
    }

    // <CondFor> ::= <Expresion> | ϵ
    // null en la cláusula vacía: for (init;;act).
    private NodoExpresion condFor() {
        if (actualEn(PRIMEROS_EXPRESION)) {
            return expresion();
        }
        return null;
    }

    // <ActFor> ::= <Expresion> | ϵ
    // null en la cláusula vacía: for (init;cond;).
    private NodoExpresion actFor() {
        if (actualEn(PRIMEROS_EXPRESION)) {
            return expresion();
        }
        return null;
    }

    // <Expresion> ::= <ExpresionCompuesta> <RestoAsignacion>
    private NodoExpresion expresion() {
        return restoAsignacion(expresionCompuesta());
    }

    // <RestoAsignacion> ::= <OperadorAsignacion> <ExpresionCompuesta> | ϵ
    // Recibe el lado izquierdo ya parseado: con "=" arma la asignación, y si
    // no, la expresión queda como estaba.
    private NodoExpresion restoAsignacion(NodoExpresion ladoIzq) {
        if (actualEs(TokenType.OP_ASIGN)) {
            Token operador = operadorAsignacion();
            return new NodoExpAsignacion(ladoIzq, operador, expresionCompuesta());
        }
        return ladoIzq;
    }

    // <OperadorAsignacion> ::= =
    private Token operadorAsignacion() {
        Token operador = tokenActual;
        match(TokenType.OP_ASIGN);
        return operador;
    }

    // <ExpresionCompuesta> ::= <ExpresionBasica> <ExpresionCompuestaResto> <TernarioOpcional>
    private NodoExpresion expresionCompuesta() {
        return ternarioOpcional(expresionCompuestaResto(expresionBasica()));
    }

    // <ExpresionCompuestaResto> ::= <OperadorBinario> <ExpresionBasica> <ExpresionCompuestaResto> | ϵ
    // Arrastra a derecha lo ya armado como lado izquierdo, así la cadena
    // queda asociativa a izquierda y sin precedencia (política de Etapa 2):
    // 2+4*b es (2+4)*b.
    private NodoExpresion expresionCompuestaResto(NodoExpresion ladoIzq) {
        if (actualEn(PRIMEROS_OP_BINARIO)) {
            Token operador = operadorBinario();
            NodoExpBinaria binaria = new NodoExpBinaria(ladoIzq, operador, expresionBasica());
            return expresionCompuestaResto(binaria);
        }
        return ladoIzq;
    }

    // <OperadorBinario> ::= || | && | == | != | < | > | <= | >= | + | - | * | / | %
    private Token operadorBinario() {
        Token operador = tokenActual;
        if (actualEn(PRIMEROS_OP_BINARIO)) {
            avanzar();
        } else {
            error("un operador binario");
        }
        return operador;
    }

    // <TernarioOpcional> ::= ? <ExpresionCompuesta> : <ExpresionCompuesta> | ϵ
    // REQ-AS-013. Se engancha en <ExpresionCompuesta> (no en <ExpresionBasica>) para que la
    // condición sea la cadena binaria completa que ya se armó en expresionBasica()+expresionCompuestaResto(),
    // dejando al ternario con menos precedencia que cualquier operador binario (como en Java).
    // El <valorFalse> es <ExpresionCompuesta>, que ya termina en su propio ternarioOpcional(): un
    // ternario anidado ahí (a ? b : c ? d : e) se resuelve solo, sin repetir la llamada acá, y da
    // asociatividad a derecha (a ? b : (c ? d : e)).
    private NodoExpresion ternarioOpcional(NodoExpresion condicion) {
        if (actualEs(TokenType.INTERROGACION)) {
            Token operador = tokenActual;
            match(TokenType.INTERROGACION);
            NodoExpresion ladoIzq = expresionCompuesta();
            match(TokenType.DOS_PUNTOS);
            return new NodoExpTernaria(condicion, operador, ladoIzq, expresionCompuesta());
        }
        return condicion;
    }

    // <ExpresionBasica> ::= <OperadorUnario> <Operando> <PostfijoOpcional> | <Operando> <PostfijoOpcional>
    // El postfijo liga primero al operando (⚠5): -a++ es Unaria(-, Postfija(a, ++)).
    private NodoExpresion expresionBasica() {
        if (actualEn(PRIMEROS_OP_UNARIO)) {
            Token operador = operadorUnario();
            return new NodoExpUnaria(operador, postfijoOpcional(operando()));
        } else if (actualEn(PRIMEROS_OPERANDO)) {
            return postfijoOpcional(operando());
        } else {
            error("una expresión");
            return null;
        }
    }

    // <PostfijoOpcional> ::= ++ <PostfijoOpcional> | -- <PostfijoOpcional> | ϵ
    // REQ-AS-014. Va pegado a <Operando> dentro de <ExpresionBasica> (no como sufijo de
    // <ExpresionCompuesta>) para que se aplique a CADA término de la cadena binaria, no sólo al
    // primero: expresionBasica() se llama una vez por término (acá y desde
    // expresionCompuestaResto()), así que "a + x++" también queda cubierto sin duplicar el chequeo.
    // Es recursiva (no un solo ++/--) para admitir encadenados como "a++--": la gramática real de
    // Java también es recursiva ahí (PostIncrementExpression/PostDecrementExpression toman como
    // operando otro PostfixExpression) y sólo lo rechaza en el chequeo de tipos ("a++" da un valor,
    // no una variable) — acá, igual que con "1++" o "1 = 2;", esa distinción queda para semántica.
    // Cada operador envuelve lo anterior: a++-- es Postfija(Postfija(a, ++), --).
    private NodoExpresion postfijoOpcional(NodoExpresion ladoIzq) {
        if (actualEs(TokenType.OP_INCREMENTO) || actualEs(TokenType.OP_DECREMENTO)) {
            Token operador = tokenActual;
            avanzar();
            return postfijoOpcional(new NodoExpPostfija(ladoIzq, operador));
        }
        return ladoIzq;
    }

    // <OperadorUnario> ::= + | - | !
    private Token operadorUnario() {
        Token operador = tokenActual;
        if (actualEn(PRIMEROS_OP_UNARIO)) {
            avanzar();
        } else {
            error("\"+\", \"-\" o \"!\"");
        }
        return operador;
    }

    // <Operando> ::= <Primitivo>
    //            |  this <ReferenciaResto>
    //            |  stringLiteral <ReferenciaResto>
    //            |  new <RestoNew> <ReferenciaResto>
    //            |  <LlamadaMetodoEstatico> <ReferenciaResto>
    //            |  idMetVar <TrasId>
    //            |  ( <TrasParen>
    // Forma factorizada de "Gramática Expandida (Logros)": inlinea <Referencia>,
    // <Primario> y <ExpresionParentizada> para poder repartir los prefijos "("
    // (expresión parentizada vs. lambda) e "idMetVar" (variable vs. lambda de 1
    // parámetro sin paréntesis). Decisión con un solo token; ARROW no está en
    // ningún FIRST, es siempre marcador infijo de lambda.
    private NodoExpresion operando() {
        if (actualEn(PRIMEROS_PRIMITIVO)) {
            return primitivo();
        } else if (actualEs(TokenType.PR_THIS)) {
            NodoThis nodo = new NodoThis(tokenActual);
            match(TokenType.PR_THIS);
            return conReferenciaResto(nodo);
        } else if (actualEs(TokenType.LIT_STRING)) {
            NodoLiteralString nodo = new NodoLiteralString(tokenActual);
            match(TokenType.LIT_STRING);
            return conReferenciaResto(nodo);
        } else if (actualEs(TokenType.PR_NEW)) {
            Token tokenNew = tokenActual;
            match(TokenType.PR_NEW);
            return conReferenciaResto(restoNew(tokenNew));
        } else if (actualEs(TokenType.ID_CLASE)) {
            return conReferenciaResto(llamadaMetodoEstatico());
        } else if (actualEs(TokenType.ID_MET_VAR)) {
            Token id = tokenActual;
            match(TokenType.ID_MET_VAR);
            return trasId(id);
        } else if (actualEs(TokenType.PAR_A)) {
            match(TokenType.PAR_A);
            return trasParen();
        } else {
            error("un operando");
            return null;
        }
    }

    // Parsea <ReferenciaResto> y lo engancha como encadenado del primario.
    // El primario es null si su producción tuvo un error sintáctico: igual
    // se parsea el resto, para no cambiar la recuperación (D8).
    private NodoPrimario conReferenciaResto(NodoPrimario primario) {
        NodoEncadenado encadenado = referenciaResto();
        if (primario != null) {
            primario.setEncadenado(encadenado);
        }
        return primario;
    }

    // <TrasId> ::= -> <Expresion>                    (lambda de 1 parámetro: x -> e)
    //          |  <ArgsActualesOpcional> <ReferenciaResto>   (variable o llamada: x | x(a) | x.f ...)
    private NodoExpresion trasId(Token id) {
        if (actualEs(TokenType.ARROW)) {
            Token flecha = tokenActual;
            match(TokenType.ARROW);
            return new NodoLambda(List.of(id), flecha, expresion());
        } else {
            List<NodoExpresion> argumentos = argsActualesOpcional();
            NodoPrimario primario = argumentos == null
                    ? new NodoAccesoVar(id)
                    : new NodoLlamadaMetodo(id, argumentos);
            return conReferenciaResto(primario);
        }
    }

    // <TrasParen> ::= ) -> <Expresion>              (lambda de 0 parámetros: () -> e)
    //             |  idMetVar <TrasParenId>         (posible parámetro: hay que ver qué sigue)
    //             |  <Expresion> ) <ReferenciaResto> (cualquier otro arranque: nunca es lambda,
    //                                                  un parámetro siempre es "idMetVar" o "()")
    private NodoExpresion trasParen() {
        if (actualEs(TokenType.PAR_C)) {
            match(TokenType.PAR_C);
            Token flecha = tokenActual;
            match(TokenType.ARROW);
            return new NodoLambda(List.of(), flecha, expresion());
        } else if (actualEs(TokenType.ID_MET_VAR)) {
            Token id = tokenActual;
            match(TokenType.ID_MET_VAR);
            return trasParenId(id);
        } else if (actualEn(PRIMEROS_EXPRESION)) {
            NodoExpresion expresion = expresion();
            match(TokenType.PAR_C);
            return conReferenciaResto(new NodoExpParentizada(expresion));
        } else {
            error("\")\" (lambda sin parámetros) o una expresión");
            return null;
        }
    }

    // <TrasParenId> ::= , <ListaIdLambda> ) -> <Expresion>   (lambda de >=2 parámetros)
    //               |  ) <DecidirTrasCierre>                  (un solo idMetVar: caso ambiguo real)
    //               |  <TrasId> <PostfijoOpcional> <ExpresionCompuestaResto> <TernarioOpcional>
    //                  <RestoAsignacion> ) <ReferenciaResto>
    //                  (el idMetVar era el comienzo de una expresión más grande —operador, ".",
    //                  "[", "(", "=", o incluso un "->" de lambda anidada vía <TrasId>—: se sigue
    //                  como expresión normal y, al cerrar, ya no se vuelve a ofrecer "->").
    // Reusa <TrasId> y la cola de <Expresion> (colaDeExpresion()) para no
    // duplicar la jerarquía de precedencia de <Expresion>.
    private NodoExpresion trasParenId(Token id) {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            List<Token> parametros = listaIdLambda();
            parametros.add(0, id);
            match(TokenType.PAR_C);
            Token flecha = tokenActual;
            match(TokenType.ARROW);
            return new NodoLambda(parametros, flecha, expresion());
        } else if (actualEs(TokenType.PAR_C)) {
            match(TokenType.PAR_C);
            return decidirTrasCierre(id);
        } else {
            NodoExpresion expresion = colaDeExpresion(trasId(id));
            match(TokenType.PAR_C);
            return conReferenciaResto(new NodoExpParentizada(expresion));
        }
    }

    // <DecidirTrasCierre> ::= -> <Expresion>        (era ( x ) -> e : lambda de 1 parámetro)
    //                      |  <ReferenciaResto>      (era ( x ) : expresión parentizada)
    private NodoExpresion decidirTrasCierre(Token id) {
        if (actualEs(TokenType.ARROW)) {
            Token flecha = tokenActual;
            match(TokenType.ARROW);
            return new NodoLambda(List.of(id), flecha, expresion());
        } else {
            return conReferenciaResto(new NodoExpParentizada(new NodoAccesoVar(id)));
        }
    }

    // <ListaIdLambda> ::= idMetVar <RestoListaIdLambda>
    private List<Token> listaIdLambda() {
        Token primero = tokenActual;
        match(TokenType.ID_MET_VAR);
        List<Token> resto = restoListaIdLambda();
        resto.add(0, primero);
        return resto;
    }

    // <RestoListaIdLambda> ::= , idMetVar <RestoListaIdLambda> | ϵ
    private List<Token> restoListaIdLambda() {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            Token parametro = tokenActual;
            match(TokenType.ID_MET_VAR);
            List<Token> resto = restoListaIdLambda();
            resto.add(0, parametro);
            return resto;
        }
        return new ArrayList<>();
    }

    // <Primitivo> ::= true | false | intLiteral | charLiteral | null
    // Un nodo por tipo de literal (⚠4).
    private NodoLiteral primitivo() {
        Token literal = tokenActual;
        if (actualEn(PRIMEROS_PRIMITIVO)) {
            avanzar();
            return switch (literal.getTipo()) {
                case LIT_INT -> new NodoLiteralEntero(literal);
                case LIT_CHAR -> new NodoLiteralCaracter(literal);
                case PR_NULL -> new NodoLiteralNull(literal);
                default -> new NodoLiteralBooleano(literal);
            };
        }
        error("un literal (true, false, intLiteral, charLiteral, null)");
        return null;
    }

    // <ReferenciaResto> ::= . idMetVar <ArgsActualesOpcional> <ReferenciaResto>
    //                    |  [ <Expresion> ] <ReferenciaResto>
    //                    |  ϵ
    // Devuelve el primer eslabón de la lista enlazada (⚠2), o null si no hay.
    private NodoEncadenado referenciaResto() {
        if (actualEs(TokenType.PUNTO)) {
            match(TokenType.PUNTO);
            Token nombre = tokenActual;
            match(TokenType.ID_MET_VAR);
            List<NodoExpresion> argumentos = argsActualesOpcional();
            NodoEncadenado siguiente = referenciaResto();
            return argumentos == null
                    ? new NodoVarEncadenada(nombre, siguiente)
                    : new NodoLlamadaEncadenada(nombre, argumentos, siguiente);
        } else if (actualEs(TokenType.CORCHETE_A)) {
            Token corchete = tokenActual;
            match(TokenType.CORCHETE_A);
            NodoExpresion indice = expresion();
            match(TokenType.CORCHETE_C);
            return new NodoAccesoArregloEncadenado(corchete, indice, referenciaResto());
        }
        // ϵ
        return null;
    }

    // <ArgsActualesOpcional> ::= <ArgsActuales> | ϵ
    // null sin paréntesis: distingue x de x() (variable o llamada).
    private List<NodoExpresion> argsActualesOpcional() {
        if (actualEs(TokenType.PAR_A)) {
            return argsActuales();
        }
        return null;
    }

    // <RestoNew> ::= <TipoPrimitivo> <DimensionesNew>
    //            |  idGen <DimensionesNew>
    //            |  idClase <TipoGenericoOpcionalNew> <RestoNewIdClase>
    // "<" seguido de un argumento vacío es el diamante (new C<>()).
    private NodoPrimario restoNew(Token tokenNew) {
        if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
            Tipo tipoBase = new Tipo(tipoPrimitivo());
            return dimensionesNew(tokenNew, tipoBase, false);
        } else if (actualEs(TokenType.ID_GEN)) {
            Tipo tipoBase = new Tipo(tokenActual);
            match(TokenType.ID_GEN);
            return dimensionesNew(tokenNew, tipoBase, false);
        } else if (actualEs(TokenType.ID_CLASE)) {
            Token idClase = tokenActual;
            match(TokenType.ID_CLASE);
            boolean conAngulos = actualEs(TokenType.OP_MENOR);
            Tipo argumento = tipoGenericoOpcionalNew();
            return restoNewIdClase(tokenNew, new Tipo(idClase, argumento), conAngulos && argumento == null);
        } else {
            error("un tipo después de \"new\"");
            return null;
        }
    }

    // <RestoNewIdClase> ::= <DimensionesNew> | <ArgsActuales>
    // El diamante también llega a la forma de arreglo: si no, new C<>[3]
    // quedaría como un new C[3] válido y se aceptaría en silencio.
    private NodoPrimario restoNewIdClase(Token tokenNew, Tipo tipo, boolean esDiamante) {
        if (actualEs(TokenType.CORCHETE_A)) {
            return dimensionesNew(tokenNew, tipo, esDiamante);
        } else if (actualEs(TokenType.PAR_A)) {
            return new NodoCreacionObjeto(tipo, esDiamante, argsActuales());
        } else {
            error("\"[\" (arreglo) o \"(\" (constructor)");
            return null;
        }
    }

    // <LlamadaMetodoEstatico> ::= idClase . idMetVar <ArgsActuales>
    private NodoLlamadaEstatica llamadaMetodoEstatico() {
        Token idClase = tokenActual;
        match(TokenType.ID_CLASE);
        match(TokenType.PUNTO);
        Token nombre = tokenActual;
        match(TokenType.ID_MET_VAR);
        return new NodoLlamadaEstatica(idClase, nombre, argsActuales());
    }

    // <DimensionesNew> ::= [ <TrasCorcheteNew>
    private NodoPrimario dimensionesNew(Token tokenNew, Tipo tipoBase, boolean esDiamante) {
        Token corchete = tokenActual;
        match(TokenType.CORCHETE_A);
        return trasCorcheteNew(tokenNew, tipoBase, corchete, esDiamante);
    }

    // <TrasCorcheteNew> ::= ] <MasCorchetesVaciosNew> <InicializadorArreglo>
    //                   |  <Expresion> ] <DimensionesConTamanioOpc>
    // "]" inmediato (REQ-AS-012): todos los corchetes de esta creación van
    // vacíos y viene un inicializador obligatorio ({1,2,3}) — no se puede
    // mezclar tamaño e inicializador, igual que en Java. Cualquier otra cosa:
    // sigue la forma de tamaño de siempre, sin cambios. Cada forma tiene su
    // nodo (⚠11).
    private NodoPrimario trasCorcheteNew(Token tokenNew, Tipo tipoBase, Token corchete, boolean esDiamante) {
        if (actualEs(TokenType.CORCHETE_C)) {
            match(TokenType.CORCHETE_C);
            int dimensiones = 1 + masCorchetesVaciosNew();
            return new NodoCreacionArregloConInicializador(tokenNew, tipoBase, dimensiones, inicializadorArreglo());
        } else {
            List<Token> corchetes = new ArrayList<>();
            List<NodoExpresion> tamanios = new ArrayList<>();
            corchetes.add(corchete);
            tamanios.add(expresion());
            match(TokenType.CORCHETE_C);
            dimensionesConTamanioOpc(corchetes, tamanios);
            return new NodoCreacionArregloConTamanio(tipoBase, esDiamante, corchetes, tamanios);
        }
    }

    // <MasCorchetesVaciosNew> ::= [ ] <MasCorchetesVaciosNew> | ϵ
    // Devuelve cuántos "[]" consumió.
    private int masCorchetesVaciosNew() {
        if (actualEs(TokenType.CORCHETE_A)) {
            match(TokenType.CORCHETE_A);
            match(TokenType.CORCHETE_C);
            return 1 + masCorchetesVaciosNew();
        }
        // ϵ
        return 0;
    }

    // <InicializadorArreglo> ::= { <ListaValoresArregloOpcional> }
    private NodoInicializadorArreglo inicializadorArreglo() {
        match(TokenType.LLAVE_A);
        List<ValorArreglo> valores = listaValoresArregloOpcional();
        match(TokenType.LLAVE_C);
        return new NodoInicializadorArreglo(valores);
    }

    // <ListaValoresArregloOpcional> ::= <ListaValoresArreglo> | ϵ
    private List<ValorArreglo> listaValoresArregloOpcional() {
        if (actualEs(TokenType.LLAVE_A) || actualEn(PRIMEROS_EXPRESION)) {
            return listaValoresArreglo();
        }
        // ϵ ("{ }" vacío también es válido)
        return new ArrayList<>();
    }

    // <ListaValoresArreglo> ::= <ValorArreglo> <RestoValoresArreglo>
    private List<ValorArreglo> listaValoresArreglo() {
        ValorArreglo primero = valorArreglo();
        List<ValorArreglo> resto = restoValoresArreglo();
        resto.add(0, primero);
        return resto;
    }

    // <RestoValoresArreglo> ::= , <ValorArreglo> <RestoValoresArreglo> | ϵ
    private List<ValorArreglo> restoValoresArreglo() {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            ValorArreglo valor = valorArreglo();
            List<ValorArreglo> resto = restoValoresArreglo();
            resto.add(0, valor);
            return resto;
        }
        return new ArrayList<>();
    }

    // <ValorArreglo> ::= <ExpresionCompuesta> | <InicializadorArreglo>
    // Anidado habilita arreglos multidimensionales con inicializador
    // (new int[][]{{1,2},{3,4}}) sin costo de factorización adicional.
    private ValorArreglo valorArreglo() {
        if (actualEs(TokenType.LLAVE_A)) {
            return inicializadorArreglo();
        } else if (actualEn(PRIMEROS_EXPRESION)) {
            return expresionCompuesta();
        } else {
            error("un valor (expresión o \"{ ... }\" anidado)");
            return null;
        }
    }

    // <DimensionesConTamanioOpc> ::= [ <Expresion> ] <DimensionesConTamanioOpc> | ϵ
    // Conflicto FIRST/FOLLOW deliberado sobre "[": con lookahead "[" se siguen
    // consumiendo dimensiones (new int[2][3] = arreglo 2D, no (new int[2])[3]).
    // El "[" de acceso a arreglo recién se toma en <ReferenciaResto>.
    // Acumula cada tamaño con su "[" (lexema de ERR_INDICE_NO_ENTERO).
    private void dimensionesConTamanioOpc(List<Token> corchetes, List<NodoExpresion> tamanios) {
        if (actualEs(TokenType.CORCHETE_A)) {
            corchetes.add(tokenActual);
            match(TokenType.CORCHETE_A);
            tamanios.add(expresion());
            match(TokenType.CORCHETE_C);
            dimensionesConTamanioOpc(corchetes, tamanios);
        } else {
            // ϵ — no hace nada (reduce por ϵ; el "[" restante lo toma <ReferenciaResto>)
        }
    }

    // <ArgsActuales> ::= ( <ListaExpsOpcional> )
    private List<NodoExpresion> argsActuales() {
        match(TokenType.PAR_A);
        List<NodoExpresion> argumentos = listaExpsOpcional();
        match(TokenType.PAR_C);
        return argumentos;
    }

    // <ListaExpsOpcional> ::= <ListaExps> | ϵ
    private List<NodoExpresion> listaExpsOpcional() {
        if (actualEn(PRIMEROS_EXPRESION)) {
            return listaExps();
        }
        return new ArrayList<>();
    }

    // <ListaExps> ::= <Expresion> <RestoListaExps>
    private List<NodoExpresion> listaExps() {
        NodoExpresion primera = expresion();
        List<NodoExpresion> resto = restoListaExps();
        resto.add(0, primera);
        return resto;
    }

    // <RestoListaExps> ::= , <Expresion> <RestoListaExps> | ϵ
    private List<NodoExpresion> restoListaExps() {
        if (actualEs(TokenType.COMA)) {
            match(TokenType.COMA);
            NodoExpresion expresion = expresion();
            List<NodoExpresion> resto = restoListaExps();
            resto.add(0, expresion);
            return resto;
        }
        return new ArrayList<>();
    }

}
