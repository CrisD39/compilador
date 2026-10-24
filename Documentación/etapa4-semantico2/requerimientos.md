# Alcance

en esta etapa se realizará el chequeo de declaraciones.
Es decir, los bloques, vamos a analizar si fueron declaradas la variables utilizadas, si son compatibles,
si las sentencias están bien definidas.


# Chequeo de tipos.

El chequeo de tipos va a ser estático.

Por lo tanto para realizar las tareas de manera estática el chequeo
de tipos requeire que los tipos de los elementos:

- Estén declarados, o 
- Sean inferibles estaticamente.

## Logros.

### Minilambdas
El compilador controla que las expresiones Lambda sean
correctamente utilizadas y tipadas, como en Java. A diferencia de
Java, los parámetros no pueden declarar tipos explícitamente y el
cuerpo debe estar formado por una única expresión, sin llaves. No se
permite la captura de variables locales o parámetros externos, ni el uso
de this dentro de una Lambda.

### For
El compilador permite estructuras de repetición for y for de iteradores
controlando su uso correcto. A diferencia de Java el de iteradores
requiere la estructura conforme con `MiniIterable<T>`, una interfaz
predefinida (como indica abajo). En el for standard se espera que el
primer componente sea una asignacion o una declaracion var, el
segundo una expresion booleana y el tercero una asignación.

### Todos los Caminos llevan al Retorno sin codigo muerto

El compilador al igual que en Java controla que cada camino de
ejecución en el cuerpo de un método llegue a una sentencia de retorno
y que no haya porciones inalcanzables de código

### Floats y Coerción

El compilador controla que se usen adecuadamente los literales de tipo
Float y, ademas, permite coerción. En particular, permite coerción de de
ints a floats, ints a chars, chars a ints, chars a Strings y de ints a
Strings. La coerción se aplica en los mismos contextos que en Java

### Ternarios

El compilador controla que las expresiones ternarias son correctamente
tipadas


### Generecidad Avanzada

El compilador controla el uso de tipos genéricos anidados en
expresiones y sentencias, y permite inferir argumentos de tipo
mediante la notación diamante, como en Java. A diferencia de Java,
cada clase o interfaz continúa limitada a un único parámetro de tipo.
No se admiten tipos raw, comodines ni cotas. Se mantienen las reglas
de conformidad de MiniJava.


### Instanceof

El compilador permite utilizar el operador instanceof y controla su uso
correcto, como en Java. A diferencia de Java, el tipo indicado a la
derecha del operador debe ser una clase o interfaz no genérica. No se
admiten arreglos como tipos de destino ni la declaración de una
variable asociada a la prueba de tipo.

### Visbilidad. chequeada
El compilador controla que los accesos sobre atributos, métodos y
constructores respetan los modificadores de visibilidad como en Java

### Super!

El compilador permite utilizar super como en Java para acceder a
métodos o atributos de un ancestro, o para invocar a su constructor

### Métodos Genéricos E4

El compilador controla que los métodos genéricos sean correctamente
utilizados y que sus cuerpos estén correctamente tipados, como en
Java. A diferencia de Java, los parámetros de tipo no tienen cotas, ni
siquiera una cota implícita Object. Se mantienen las reglas de
conformidad de MiniJava.

### Variables Locales Clásicas Controladas
El compilador controla las sentencias de definición de variables
clásicas (en todas sus variantes) son correctamente tipadas

### Multi-Detección Errores Semanticos en Sentencias

El compilador no finaliza la ejecución ante el primer error semántico en
una sentencia o expresión (se recupera) y es capaz de reportar otros
errores que tenga el programa fuente en una corrida. Como mínimo se
espera que el compilador pueda continuar con el análisis de la próxima
sentencia

### Atributos Inicializados – Chequeado

El compilador controla que los atributos inicializados son
correctamente tipados (los controles son los mismos que en Java)

### Postfijos - Chequeado

El compilador controla que los operadores posfijos de incremento (++)
y decremento (--) sean correctamente utilizados y tipados, como en
Java. A diferencia de Java, solo se admiten operandos de tipo int. Se
mantienen las restricciones de MiniJava sobre destinos asignables.

# Decisiones de diseño

Se cerraron en una entrevista de diseño (2026-10-17). Si un boceto de más
abajo dice otra cosa, vale lo que dice esta sección. Las secciones de abajo
ya están alineadas con estas decisiones; cada decisión indica la sección
donde está el detalle.

## Pipeline y wiring

- **Main propio de la etapa.** `View/ModuloPrincipalET4` y
  `Controller/AnalizadorSemantico2HandlerImpl` (con su interfaz) corren el
  pipeline de ET3 y después `tablaSimbolos.chequearSentencias()`. El jar
  (`build.sh`) y `Tester{Sin,Con}ErroresSem2` pasan a usar ET4.
  `ModuloPrincipalET3` queda congelado, solo con declaraciones, para que los
  casos de `semantico/` no fallen por errores en cuerpos que esos casos no
  cuidan.
- **Sin chequeo en cascada.** `chequearSentencias()` corre solo si no hubo
  errores léxicos, sintácticos ni de declaraciones. Con tipos sin resolver o
  herencia cíclica, los chequeos de cuerpos producirían errores sin sentido,
  y el primer error reportado tiene que ser el real.
- **Predefinidas.** `Object`, `String` y `System` (§7) se registran en el
  constructor de `TablaSimbolos`, antes de que el parser agregue las clases
  del usuario, con los métodos del enunciado. Quedan marcadas como
  predefinidas: no tienen cuerpos para chequear, y redeclararlas da
  `ERR_CLASE_DUPLICADA`. Esto se adelantó a Etapa 3 y el diseño completo
  está en `etapa3-semantico/analizador-semantico.md`, en "Decisiones: tipos
  predefinidos, clave y redefinición".
- **Solo miembros propios.** `consolidar()` copia a cada subclase los
  **mismos** objetos `Metodo` y `Atributo` del padre (`Clase.consolidarConPadre`).
  Por eso cada clase chequea solo lo que declaró ella: un método es propio si
  `Metodo.miClase == this`, y `Atributo` y `Constructor` llevan una referencia
  a la clase que los declara. Si no, un cuerpo heredado se chequearía una vez
  por subclase, con errores repetidos y la clase actual equivocada (§14: "cada
  cuerpo se chequea en su clase y unidad de origen"). Orden: atributos
  inicializados, constructores, métodos. No se chequean interfaces ni
  predefinidas. Ver "Chequeos de nodos".
- **Alcance.** Se construyen nodos para **todo** lo que el parser acepta,
  incluida la sintaxis de logros: si no, se pierden pedazos de los cuerpos.
  En la primera iteración solo se chequea lo base (§9–§12). Los nodos de
  logros que todavía no se chequean lanzan un error explícito de "no
  soportado" en vez de aceptar en silencio. Los logros entran después, de a
  uno, con sus casos en `resources/semantico2/logros/`.

## Contexto y errores

- **El estado del chequeo vive en `TablaSimbolos`.** La tabla guarda la pila
  de scopes locales y la clase y la unidad actuales. La firma es
  `chequear(TablaSimbolos tabla)` en sentencias y expresiones, y
  `chequear(TablaSimbolos tabla, Tipo tipoIzquierdo)` en los encadenados.
  **Todo nodo que abre un scope lo cierra en un `finally`** (`NodoBloque`,
  `NodoFor`, `NodoForEach`, `NodoLambda`). Si no, una excepción a mitad de
  camino deja la pila desbalanceada y una local queda visible fuera de su
  bloque. Ver "Scope local".
- **Unidad actual: tres campos.** `metodoActual` (se reusa el del parser),
  `constructorActual` y `atributoActual`. Al chequear una unidad se setea su
  campo y los otros dos quedan en `null`. `chequearSentencias()` resetea los
  tres al empezar, porque el parser deja `metodoActual` apuntando al último
  método parseado.
- **Ocultamiento (lo fija §10.1/§12.3).** Una local no puede repetirse en el
  mismo bloque, ni tener el nombre de un parámetro de la unidad, ni ocultar
  una local de un bloque contenedor que siga visible. Sí puede ocultar un
  atributo, y `this.x` accede al atributo.
- **Errores: excepción y captura por sentencia.** Las expresiones lanzan
  `ExcepcionSemantica(token, "ERR_*")` (nueva, en `Model.semantico`).
  `NodoBloque` la atrapa en cada sentencia, la agrega a la tabla con
  `tabla.agregarError(new ErrorSemantico(...))` y sigue con la próxima. Así
  se cumple el piso del logro *Multi-detección* sin propagar un "tipo error"
  por todo el árbol. Los `ERR_*` nuevos llevan su descripción en
  `ErrorSemantico.getDescripcion()`, como en Etapa 3, y el lexema sigue la
  convención de `casos-de-prueba.md`.

## Nodos

- **Paquete `Model.semantico.ast`.** `NodoSentencia` se mueve ahí.
  `ExcepcionSemantica` queda en `Model.semantico`.
- **Genéricos vistos desde el receptor (§5.1).**
  `Tipo.sustituir(mapa)` (la firma real es `Map<OrigenParametroTipo, Tipo>`,
  con el mapa que arma `TablaSimbolos.instanciacionDe(receptor)`; spec 0003,
  D10) se aplica en `NodoVarEncadenada` y `NodoLlamadaEncadenada` sobre el
  tipo del atributo y sobre los parámetros y el retorno del método. Es recursivo en arreglos y genéricos anidados. Como
  cada clase tiene un único parámetro de tipo, cada paso de la cadena de
  herencia es un reemplazo simple, y las sustituciones se componen.
- Las ambigüedades ⚠1–⚠13 de la sección NODOS están resueltas en
  "Decisiones sobre los nodos".

## Revisión contra el enunciado completo (2026-10-23)

Se comparó este diseño con el enunciado completo
(`MiniJava-Semantica-Completa-2026.pdf`). Lo que toca la Etapa 3 se corrige
en la spec `specs/0002-correccion-etapa3-enunciado-completo.md`. Lo de
abajo es de la Etapa 4: queda decidido, pero no se implementa hasta
encarar esta etapa.

- **Receptor de tipo interfaz.** Una interfaz contiene implícitamente los
  métodos de instancia de `Object`, así que `i.toString()` es válido
  (§10.2, Java). Desde la spec 0002 la consolidación de la Etapa 3 ya los
  copia al mapa de métodos de la interfaz, así que `NodoLlamadaEncadenada`
  los encuentra sin un caso especial.
- **Receptor arreglo.** Además de `length` (⚠10), un arreglo tiene los
  métodos de instancia de `Object`: sólo `toString()`, sin parámetros y con
  retorno `String` (§11.8, §13). `datos.clone()` u otro método da error
  (`semIIError74`). `NodoLlamadaEncadenada` busca el método en `Object`
  cuando `tipoIzquierdo` es un arreglo, y descarta los estáticos. Con tipo
  estático `Object`, un arreglo no permite indexar ni `length`
  (`semIIError72`, `73`).
- **Receptor parámetro de tipo.** Un receptor de tipo `T` sin instanciar no
  permite seleccionar atributos ni métodos, tampoco `toString()`: los
  parámetros de tipo no tienen cotas (§10.1, §10.2, §11.8). En Java sí se
  puede. Si el receptor es `Caja<T>`, o una vista donde `T` ya se sustituyó
  por un tipo concreto, se resuelve normalmente.
- **Tipos raw** (ADR 0007). Se rechazan también en las declaraciones de
  locales clásicas, en los tipos de `for`/`foreach` y en `new Caja()`
  (`semIIError24`), con el mismo `ERR_TIPO_SIN_ARGUMENTO` sobre el nombre
  del tipo.
- **Pendiente: logros que cambian el resultado de casos base.** Hay dos
  logros que contradicen al documento base, y los casos base esperan el
  comportamiento base:
  - *Todos los caminos llevan al retorno* contra §12.5 (no se exige retorno
    en todos los caminos y el código inalcanzable se acepta):
    `semIICorrecto31`, `semIIError104`.
  - *Floats y coerción* contra §9.1 y §11.2 (sin conversiones `char`↔`int`):
    `semIIError33`, `34`, `52`, `59`, `70`.

  Antes de implementarlos hay que decidir, idealmente con la cátedra, si el
  logro reemplaza a la regla base (y se ajustan esos casos) o si las dos
  conviven de algún modo.
- **Prerrequisito: `Metodo.miClase` nunca se setea.** "Solo miembros
  propios" compara `m.getMiClase() == this`, pero hoy el campo queda en
  `null`. Hay que setearlo al registrar el método, igual que la clase
  declarante de `Atributo` y `Constructor`.
- Quedan como están los ejemplos de atributos `static` de "Atributos
  inicializados" (la gramática sólo admite `static` en métodos, §6.1) y
  los nodos de inicializadores de arreglo (⚠11, ⚠12).

## Decisiones de la spec 0003 (2026-10-24)

La implementación de la base de la etapa se planificó en
`specs/0003-chequeo-sentencias-base.md`, con 7 tickets. Esa spec cierra lo
que este documento dejaba abierto, y si algo de acá la contradice, vale la
spec:

- Alcance: sólo lo base (§9–§12), más *Multi-detección* y *Postfijos*
  (`semIICorrecto03` usa `a1++`).
- `Tipo` sigue siendo una sola clase, con `conformaCon`, `Tipo.NULL` y
  `Tipo.VOID`. `Metodo` mantiene `null` = `void`.
- Variables: interfaz `Variable` (`Parametro`, `Atributo`, `VariableLocal`)
  y `buscarVariable` en `TablaSimbolos`. Reemplaza `EntradaVarLocal` y
  `EntradaVariable` de los bocetos de abajo.
- Las reglas de conformidad (D9), la vista desde el receptor (D10), los
  tokens de "no soportado" (D11) y la tabla de códigos `ERR_*`.

# Cadena de llamadas

## Orden de chequeo de semantica

```java
// 1. El parser recorre el código, llena la TS y arma los ASTs de cada método
parser.parse();

// 2. Primera pasada semántica: Verifica que los tipos declarados existan,
// que no haya herencia circular, e incorpora métodos/atributos heredados.
tablaSimbolos.estaBienDeclarado();
tablaSimbolos.consolidar();

// 3. Segunda pasada semántica: Recién AHORA que todas las clases están
// completas y consolidadas, mandamos a chequear los cuerpos (los AST).
// Sólo si no hubo errores léxicos, sintácticos ni de declaraciones
// (ver "Decisiones de diseño": sin chequeo en cascada).
tablaSimbolos.chequearSentencias();
```

## Chequeos de nodos

Cada clase chequea sólo sus miembros **propios** (ver "Decisiones de diseño"):
`consolidar()` comparte los objetos heredados entre clases, así que recorrer
el mapa consolidado chequearía un cuerpo heredado una vez por subclase.

```java
// En TablaSimbolos:
public void chequearSentencias() {
    // El parser deja metodoActual apuntando al último método parseado.
    metodoActual = null;
    constructorActual = null;
    atributoActual = null;
    for (EntidadDeclarada e : this.clases.values()) {
        if (e instanceof Clase c && !c.esPredefinida()) { // interfaces y predefinidas no tienen cuerpos
            this.claseActual = c; // contexto para saber qué es 'this'
            c.chequearSentencias(this);
        }
    }
}

// En Clase (orden: atributos inicializados, constructores, métodos):
public void chequearSentencias(TablaSimbolos tabla) {
    for (Atributo a : atributos.values()) {
        if (a.getClaseDeclarante() == this && a.getInicializador() != null) {
            tabla.setUnidadActual(null, null, a);
            a.chequearSentencias(tabla);
        }
    }
    for (Constructor c : constructores) {
        tabla.setUnidadActual(null, c, null);
        c.getBloquePrincipal().chequear(tabla);
    }
    for (Metodo m : metodos.values()) {
        if (m.getMiClase() == this) {
            tabla.setUnidadActual(m, null, null);
            m.getBloquePrincipal().chequear(tabla); // acá arranca el chequeo del AST
        }
    }
}
```

`setUnidadActual(metodo, constructor, atributo)` setea los tres campos de una
sola vez, así nunca queda más de uno distinto de `null`.

## Sentencias vs Expresiones. 

En el AST vas a tener dos grandes familias de nodos, y el método `chequear()` tiene una firma distinta en cada una:

### Nodo Sentencias 

**`NodoBloque.chequear(tabla)`:**

- Abre un nuevo scope local (apila un bloque en la TS).
- Hace un for recorriendo su lista de `NodoSentencia` y llama a `sentencia.chequear(tabla)` en cada una.
  Cada llamada va en su propio try/catch: si salta una `ExcepcionSemantica`, se agrega como
  `ErrorSemantico` a la tabla y se sigue con la próxima sentencia (multi-detección).
- Cierra el scope local en un `finally` (elimina las variables locales declaradas adentro de ese
  bloque para que no sean visibles afuera, aunque algo haya fallado en el medio).

**`NodoIf.chequear(tabla)`:**

- Llama a `Tipo t = this.condicion.chequear(tabla)`.
- Verifica que `t` sea de tipo boolean (si no lo es, lanza excepción semántica con la línea del token).
- Llama a `this.sentenciaThen.chequear(tabla)`.
- Si tiene bloque else, llama a `this.sentenciaElse.chequear(tabla)`.

**`NodoReturn.chequear(tabla)`:**

- Le pregunta a la Tabla de Símbolos cuál es la unidad actual y qué tipo de retorno espera.
  Si es `constructorActual`, sólo se admite `return;` sin expresión (§12.5).
- Si hay una expresión de retorno, llama a `Tipo tipoExpr = this.expresion.chequear(tabla)`.
- Verifica que `tipoExpr` sea subtipo (conforme) con el tipo de retorno del método.

### Nodo de expresión 

Representan cálculos que producen un valor y tienen un tipo (`2 + 3`, `a && b`, `x`, `this.foo()`). Por lo tanto, su método devuelve un objeto Tipo: `public abstract Tipo chequear(TablaSimbolos tabla);`
Además tienen `esAsignable()` y `esLlamable()` (ver "Nodos asignables y llamables").

**TODO!** Falta ver tipos de todo expresión 

## Scope local.

Al momento de iniciar un bloque hay que avisar a la tabla de simbolos que cree un scope de variables locales.

En la TablaSimbolos, además de `claseActual` y los tres campos de unidad actual (`metodoActual`,
`constructorActual`, `atributoActual`; ver "Decisiones de diseño"), hay una pila de mapas (cada mapa
representa un bloque abierto actualmente):

```java
// En TablaSimbolos:
private Stack<Map<String, EntradaVarLocal>> pilaBloques = new Stack<>();
```

¿Cómo se usa durante el `.chequear(tabla)` de `NodoBloque`?

Cuando empieza `NodoBloque.chequear(tabla)`:
Le avisa a la Tabla de Símbolos que abrió un nuevo ámbito, y lo cierra en un `finally`:

```java
tabla.abrirBloque(); // Hace pilaBloques.push(new HashMap<>())
try {
    for (NodoSentencia s : this.sentencias) {
        try {
            s.chequear(tabla);
        } catch (ExcepcionSemantica e) {
            tabla.agregarError(e.comoError()); // se sigue con la próxima sentencia
        }
    }
} finally {
    tabla.cerrarBloque(); // Hace pilaBloques.pop(); obligatorio aunque algo haya fallado
}
```

El mismo try/finally va en todo nodo que abre un scope: `NodoFor` (la variable de la inicialización),
`NodoForEach` y `NodoLambda`. Sin el `finally`, una excepción a mitad de camino deja la pila
desbalanceada y una local queda visible en las sentencias de afuera.

Cuando se chequea una declaración (`NodoVarLocal.chequear()`):

Primero verificás que el nombre no esté repetido. Según las reglas típicas de lenguajes tipo Java (y la mayoría de los 
lenguajes académicos basados en él), una variable local no puede llamarse igual que un parámetro del método ni igual que otra variable local 
de un bloque contenedor que siga activo. (En cambio, sí puede llamarse igual que un atributo de la clase, ocultándolo o haciéndole shadowing).

Si el nombre es válido, se inserta en el mapa que está en el tope de la pila (`pilaBloques.peek().put(nombre, this)`).

Cuando se busca un identificador (`NodoAccesoVar.chequear()`):
Hacés un método en la TablaSimbolos llamado `buscarVariable(String nombre)` que busca exactamente en este orden de prioridad:

1. **Paso 1 (Variables locales):** Recorrés `pilaBloques` desde el tope hacia la base. Si aparece en algún bloque abierto, devolvés esa variable.
2. **Paso 2 (Parámetros):** Si no estaba en los bloques, buscás en los parámetros de la unidad actual
   (`metodoActual` o `constructorActual`; un atributo inicializado no tiene parámetros).
3. **Paso 3 (Atributos de instancia):** Si tampoco es parámetro, buscás en `claseActual.getAtributos()` (que como ya consolidaste antes, ya incluye los heredados).
4. **Paso 4:** Si no apareció en ningún lado → Error semántico ("Identificador no declarado").

## Nodos encadenados

Ejemplo `v.x` , `v.m()`

Decisión ⚠2: los encadenados son una **lista enlazada** (ver "Decisiones sobre los nodos").

**`NodoPrimario`** (hereda de `NodoExpresion`; en las trazas de abajo aparece como `NodoVar`):
Representa el primer elemento de la cadena (`v`, `this`, `new Persona()`, `metodo()`, etc.).

- Tiene un atributo opcional: `protected NodoEncadenado encadenado;` (que puede ser null si es solo `v`, o tener un objeto si es `v.x`).
- Su método recibe la tabla: `public Tipo chequear(TablaSimbolos tabla)`.

**`NodoEncadenado`** (clase abstracta aparte):
Representa lo que viene después de un punto o un corchete (`.x`, `.m()` o `[i]`).

- Sus subclases son `NodoVarEncadenada` (`.x`), `NodoLlamadaEncadenada` (`.m()`) y
  `NodoAccesoArregloEncadenado` (`[i]`).
- También tiene su propio atributo opcional: `protected NodoEncadenado siguienteEncadenado;` (una lista enlazada de encadenados).
- Su método recibe la tabla y el tipo de lo que estaba a su izquierda:
  `public abstract Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo)`.

Dos responsabilidades más de los encadenados:

- Sustitución genérica (§5.1): al encontrar el miembro, `NodoVarEncadenada` y `NodoLlamadaEncadenada`
  ven su tipo desde el receptor con `Tipo.sustituir(tabla.instanciacionDe(tipoIzquierdo))` (spec 0003, D10). Si `tipoIzquierdo` es
  `Caja<String>`, el atributo `T` se ve como `String`, y lo mismo pasa con los parámetros y el retorno
  del método. En una cadena de herencia (`Final extends Intermedia<String>`,
  `Intermedia<U> extends Base<U>`) las sustituciones se componen paso por paso.
- `length` (⚠10, §11.8): lo resuelve `NodoVarEncadenada`. Si `tipoIzquierdo` es un arreglo y el nombre
  es `length`, el tipo es `int` y `esAsignable()` da `false`. No hay un nodo `NodoLength` porque el parser
  no conoce los tipos y no puede saber que el receptor es un arreglo.

La traza paso a paso: `v.x.m()`

Imaginate que tenés `v.x.m()`, donde:

- `v` es una variable local de tipo `Persona`.
- `x` es un atributo de `Persona` de tipo `Perro`.
- `m()` es un método de `Perro` que retorna `int`.

Así quedan conectados los objetos cuando el parser termina:

```
NodoVar "v"]
└── encadenado ──> [NodoVarEncadenada "x"]
└── siguienteEncadenado ──> [NodoLlamadaEncadenada "m()"]
└── siguienteEncadenado = null
```

### Traza La traza paso a paso: v.x.m()

El ejemplo es `v.x.m()`, donde:

- `v` es una variable local de tipo `Persona`.
- `x` es un atributo de `Persona` de tipo `Perro`.
- `m()` es un método de `Perro` que retorna `int`.

#### Paso 1 En NodoVar (v):

```java
public Tipo chequear(TablaSimbolos tabla) {
    // 1. Busco "v" en scope local -> parámetros -> atributos de claseActual
    EntradaVariable entrada = tabla.buscarVariable(this.nombre);
    if (entrada == null) {
        throw new ExcepcionSemantica(token, "La variable " + nombre + " no existe");
    }

    Tipo miTipo = entrada.getTipo(); // miTipo es TipoClase("Persona")

    // 2. ¿Tengo algo encadenado a mi derecha?
    if (this.encadenado != null) {
        // Le paso MI tipo al encadenado para que él resuelva y devuelva el tipo final
        return this.encadenado.chequear(tabla, miTipo);
    } else {
        // Si no hay encadenado (era solo "v"), el tipo de toda la expresión es miTipo
        return miTipo;
    }
}
```

#### Paso 2 En NodoVarEncadenada (.x)

Recibe `TipoClase("Persona")` como `tipoIzquierdo`:

```java
public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
    // 1. Verifico que el tipo de la izquierda sea una Clase (no puedo hacer 5.x ni true.x)
    if (!tipoIzquierdo.esTipoClase()) {
        throw new ExcepcionSemantica(token, "No se puede acceder a un atributo de un tipo primitivo");
    }

    // 2. Busco la clase "Persona" en la Tabla de Símbolos
    Clase claseIzq = tabla.getClase(tipoIzquierdo.getNombreClase());

    // 3. Busco el atributo "x" ADENTRO de "Persona"
    Atributo attr = claseIzq.getAtributo(this.nombre);
    if (attr == null) {
        throw new ExcepcionSemantica(token, "El atributo " + nombre + " no existe en " + claseIzq.getNombre());
    }
    // (Opcional: acá también chequeás visibilidad, ej. si 'x' es privado o público)

    Tipo miTipo = attr.getTipo(); // miTipo es TipoClase("Perro")

    // 4. ¿Sigue la cadena a la derecha?
    if (this.siguienteEncadenado != null) {
        return this.siguienteEncadenado.chequear(tabla, miTipo); // Le pasa "Perro" a .m()
    } else {
        return miTipo;
    }
}
```

#### Paso 3: En NodoLlamadoEncadenada (.m())

Recibe `TipoClase("Perro")` como `tipoIzquierdo`:

```java
public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
    // 1. Verifico que sea un tipo válido para llamar métodos (Clase o Interfaz)
    ClaseOInterfaz entidad = tabla.getClaseOInterfaz(tipoIzquierdo.getNombre());

    // 2. Busco el método "m" adentro de "Perro"
    Metodo met = entidad.getMetodo(this.nombre);
    if (met == null) {
        throw new ExcepcionSemantica(token, "El método " + nombre + " no existe");
    }

    // 3. Chequeo que los argumentos actuales coincidan en cantidad y conformen en tipos 
    // con los parámetros formales de 'met'
    chequearArgumentos(tabla, met);

    Tipo miTipo = met.getTipoRetorno(); // miTipo es TipoEntero ("int")

    // 4. ¿Sigue la cadena?
    if (this.siguienteEncadenado != null) {
        return this.siguienteEncadenado.chequear(tabla, miTipo);
    } else {
        return miTipo; // Como es el último, retorna "int" hasta arriba de todo
    }
}
```

### Un detalle importante: Asignaciones (v.x = 10 vs v.m() = 10)

Cuando estés chequeando un `NodoAsignacion` (`ladoIzq = ladoDer`), no cualquier acceso puede estar del lado izquierdo del `=`.

- `v = 10;` → Válido (termina en variable).
- `v.m().x = 10;` → Válido (en el medio hay una llamada, pero el último eslabón de la cadena es un atributo `.x`).
- `v.x.m() = 10;` → Inválido (no le podés asignar un valor al resultado de un método).

## Nodos asignables y llamables

La idea es que los métodos tengan un método `esAsignable()` y `esLlamable()` que sean métodos boolean.

El método `esLlamable()` sirve para validar las sentencias simples (o sentencias de expresión): aquellas líneas de código que consisten 
únicamente en un acceso seguido de punto y coma (`;`), sin una asignación (`=`) de por medio.

por ejemplo: poder llamar a `m();` directamente

```java
void miMetodo() {
    v.m();          // 1. VÁLIDO: Llamada a método encadenada
    m();            // 2. VÁLIDO: Llamada a método directa
    v.m().x.foo();  // 3. VÁLIDO: Cadena que TERMINA en llamada a método
    new Persona();  // 4. VÁLIDO: Llamada a constructor

    v;              // 5. INVÁLIDO: Variable suelta (no hace nada)
    v.x;            // 6. INVÁLIDO: Acceso a atributo suelto
    v.m().x;        // 7. INVÁLIDO: Aunque llama a m() en el medio, TERMINA en un atributo .x
}
```

Desde el punto de vista de la gramática, muchas veces la regla de una sentencia simple es algo como:

`SentenciaSimple -> Acceso ;` (o `Expresion ;`).

El parser te va a dejar pasar `v.m().x;` porque sintácticamente es un Acceso seguido de `;`. Es en el análisis semántico 
donde tenés que detectar que `v.m().x;` no es una sentencia válida porque el último eslabón de la cadena es 
una variable (`.x`) y no una llamada (`()`).

### Cómo funciona esLlamable() en los nodos

Al igual que `esAsignable()`, lo que importa siempre es el último eslabón de la cadena. Si el nodo actual tiene un
encadenado a su derecha, le pasa la pregunta al siguiente; si es el último (`encadenado == null`), responde según lo que él sea:

**1. En los Accesos Primarios (NodoVar)**

`NodoVar` (`v`) o `NodoThis` (`this`):
Si no tiene encadenado, una variable suelta `v;` o `this;` no es llamable.

```java
@Override
public boolean esLlamable() {
    if (this.encadenado != null) {
        return this.encadenado.esLlamable();
    }
    return false; // "v;" suelto no es válido como sentencia
}
```

`NodoLlamadaMetodo` (`m()`) o `NodoLlamadaEstatica` (`Clase.m()`):
Si termina acá, sí es llamable. Si sigue la cadena (ej. `m().x`), decide el encadenado.

```java
@Override
public boolean esLlamable() {
    if (this.encadenado != null) {
        return this.encadenado.esLlamable();
    }
    return true; // "m();" suelto es válido
}
```

**En los Nodos Encadenados (NodoEncadenado)**

`NodoVarEncadenada` (`.x`):

```java
@Override
public boolean esLlamable() {
    if (this.siguienteEncadenado != null) {
        return this.siguienteEncadenado.esLlamable();
    }
    return false; // Termina en ".x", no se puede usar como sentencia suelta
}
```

`NodoLlamadaEncadenada` (`.m()`):

```java
@Override
public boolean esLlamable() {
    if (this.siguienteEncadenado != null) {
        return this.siguienteEncadenado.esLlamable();
    }
    return true; // Termina en ".m()", es una llamada válida
}
```

### ¿Dónde se invoca esLlamable()?

Lo llamás adentro del `.chequear(tabla)` de `NodoSentenciaExpresion`, el único nodo de sentencia
que envuelve una expresión con `;` al final (decisión ⚠1). Valida §12.2: la expresión principal
es una asignación (sin paréntesis) o termina en una llamada o en un `new` de objeto.

```java
public class NodoSentenciaExpresion extends NodoSentencia {
    private NodoExpresion expresion;
    private Token puntoYComa; // lexema del error, según la convención de casos-de-prueba.md

    @Override
    public void chequear(TablaSimbolos tabla) {
        // 1. Primero chequeo los tipos de toda la cadena para que valide que todo exista
        this.expresion.chequear(tabla);

        // 2. Verifico que sea una asignación o que la expresión completa termine en algo invocable
        if (!(this.expresion instanceof NodoExpAsignacion) && !this.expresion.esLlamable()) {
            throw new ExcepcionSemantica(puntoYComa, "ERR_SENTENCIA_INVALIDA");
        }
    }
}
```

### Comparativa rápida: esAsignable() vs esLlamable()

Para cualquier cadena de accesos, el último eslabón determina ambos comportamientos (y suelen ser mutuamente excluyentes):

| Expresión | Último eslabón | `esAsignable()` (lado izq de `=`) | `esLlamable()` (sentencia suelta con `;`) |
|---|---|---|---|
| `v` | `NodoAccesoVar` | true | false |
| `m()` | `NodoLlamadaMetodo` | false | true |
| `new A()` | `NodoCreacionObjeto` | false | true |
| `v.m().x` | `NodoVarEncadenada` | true | false |
| `v.x.m()` | `NodoLlamadaEncadenada` | false | true |
| `a.length` | `NodoVarEncadenada` (resuelto como `length`, ⚠10) | false | false |
| `a[0]` | `NodoAccesoArregloEncadenado` | true | false |
| `(v.x)` (expresión parentizada) | `NodoExpParentizada` (⚠3: corta, no delega) | false | false |
| `(o).m()` | `NodoExpParentizada` con encadenado | false | false (§11.4: no se basa una sentencia en un primario parentizado) |

# NODOS

Diseño de los nodos del AST de los cuerpos (métodos, constructores e
inicializadores de atributos). El enunciado (`MiniJava-Semantica-Completa-2026.pdf`
§14) lo pide así: "durante el análisis sintáctico se construyen AST de métodos y
constructores, junto con la tabla de símbolos. Los AST conservan tokens para
localizar errores". El modelo es `enunciados/AST.png`
(`ExpresiónBinaria{Lado Izq, Lado Der, Token}`, `Entero{Token}`, `Variable{Token}`).

**Referencia gramatical: la gramática del código**, es decir los comentarios de
producción y los cuerpos de los métodos de
`Código/src/Model/sintactico/AnalizadorSintacticoImpl.java`. No es la de
`etapa2-sintactico/analizador-sintactico.md`. Los no terminales que se nombran
abajo son los de ese archivo.

## Criterios

- **Un nodo por regla con significado semántico.** Los no terminales que
  existen sólo para la **factorización LL(1)** no generan nodo, pero cada uno
  arma alguno de los nodos de la tabla: `<TrasId>`, `<TrasParen>`,
  `<TrasParenId>`, `<DecidirTrasCierre>`, `<SentIdClase>`, `<TrasIdForTipo>`,
  `<TrasIdClaseFor>`, `<RestoNew>`, `<RestoNewIdClase>`, `<TrasCorcheteNew>`,
  `<…Resto>` y `<…Opcional>`. Las listas tampoco generan nodo: son una
  `List<…>` dentro del nodo padre. En el nivel cuerpos son `<ListaSentencias>`,
  `<ArgsActuales>`, `<ListaExpsOpcional>`, `<ListaExps>`, `<RestoListaExps>`,
  `<ListaIdLambda>`, `<RestoListaIdLambda>`, `<ListaValoresArregloOpcional>`,
  `<ListaValoresArreglo>`, `<RestoValoresArreglo>`, `<MasIdsLocal>`,
  `<MasCorchetesVaciosNew>` y `<DimensionesConTamanioOpc>`.
- **Tokens sólo donde representan algo.** Un nodo guarda un `Token` cuando ese
  token tiene significado propio: el operador (`operador`), un nombre
  (`idMetVar`, `idClase`), un literal o la palabra clave de la sentencia
  (`if`, `while`, `return`…), que además sirve para reportar
  `[Error:<lexema>|<línea>]` según la convención de `casos-de-prueba.md`. Los
  delimitadores (`{`, `(`, `;`) no se guardan: por ejemplo, `NodoBloque` no
  tiene token.
- **Convención de campos (la de `AST.png`).** Todo nodo con operador tiene
  `ladoIzq`, `ladoDer` y `operador` (el `Token` del operador), en la medida en
  que correspondan:
  - binarios (`NodoExpBinaria`, `NodoExpAsignacion`): `ladoIzq`, `ladoDer`, `operador`;
  - unario prefijo (`NodoExpUnaria`): `operador` + `ladoDer` (el operando está a la derecha del operador);
  - postfijo (`NodoExpPostfija`): `ladoIzq` + `operador` (el operando está a la izquierda);
  - ternario (`NodoExpTernaria`): `condicion` + `ladoIzq` (rama antes de `:`) + `ladoDer` (rama después de `:`) + `operador` (`?`);
  - nodos sin operador (literales, `NodoAccesoVar`, `NodoThis`, …): sólo `token`.
- **Los tipos escritos en el fuente reusan la jerarquía `Tipo`** que ya existe
  en `Model/semantico`. No hay `NodoTipo`.
- **Contrato de toda expresión** (§11: "el análisis distingue tipo resultante,
  condición de destino asignable y forma admisible como sentencia de llamada"):
  `chequear(...) → Tipo`, `esAsignable()` y `esLlamable()`. Las sentencias sólo
  tienen `chequear(...)`, porque no tienen tipo (§12).
- **Binarios sin precedencia.** `<ExpresionCompuestaResto>` arma una sola cadena
  asociativa a izquierda, sin niveles de precedencia (política de Etapa 2, que
  §11 obliga a respetar): `2+4*b` es `(2+4)*b`, no `2+(4*b)`. El AST no la
  reordena.

## Nivel declaraciones: no son nodos del AST

`<Inicial>`, `<ListaClases>`, `<Sealed>`, `<NonSealed>`, `<Final>`, `<Clase>`,
`<Interfaz>`, `<PermitidosSealed>`, `<GenericidadOpcional>`,
`<HerenciaOpcional>`, `<ListaInterfaces>`, `<ExtensionOpcional>`,
`<ListaMiembros>`, `<ListaMetodosInterfaz>`, `<Miembro>`, `<Visibilidad>`,
`<FinalOpcional>`, `<CuerpoMiembro>`, `<TrasIdClaseMiembro>`,
`<MetodoInterfaz>`, `<TipoMetodo>`, `<Tipo>`, `<TipoBase>`, `<TipoReferencia>`,
`<TipoPrimitivo>`, `<TipoGenericoOpcional>`, `<InstanciadoOParametrico>`,
`<DimensionesOpcionales>`, `<ListaInterfacesResto>`, `<PermitidosSealedResto>`,
`<ArgsFormales>`, `<ListaArgsFormalesOpcional>`, `<ListaArgsFormales>`,
`<ListaArgsFormalesResto>` y `<ArgFormal>` ya están representados
por la tabla de símbolos (`Clase`, `Interfaz`, `Metodo`, `Constructor`,
`Atributo`, `Parametro`, `Tipo`). El AST se engancha en esos objetos en tres
lugares:

- el `<Bloque>` de un método (`<CuerpoMiembro>`, `<RestoMiembro> ::= <ArgsFormales> <Bloque>`) → `NodoBloque` en `Metodo`;
- el `<Bloque>` de un constructor (`<TrasIdClaseMiembro> ::= <ArgsFormales> <Bloque>`) → `NodoBloque` en `Constructor`;
- `<RestoMiembro> ::= <OperadorAsignacion> <ExpresionCompuesta> ;` (atributo inicializado) → `NodoExpresion` en `Atributo`.

## Atributos inicializados

Las clase Atributo debería un atributo inicilizador de tipo NodoExpresión.
El cual debería determinar si el atributo está inicilizado o no.

Lo más limpio es reusar el mismo mapa o lista de atributos (`this.atributos`) que ya tenés en la Clase, y que cada objeto Atributo tenga adentro un campo opcional 
`private NodoExpresion inicializador;` (que será null si el atributo se declaró sin `= ...`).

```java
public class Atributo {
    private Token token;
    private Tipo tipo;
    private boolean esEstatico;
    // ... visibilidad, etc.

    // Nuevo campo para la Etapa 4 (es null si hicieron "int x;" sin asignar)
    private NodoExpresion inicializador; 

    // Clase que lo declaró: consolidar() comparte este mismo objeto con las
    // subclases, así que hace falta para chequearlo sólo en su clase de origen.
    private Clase claseDeclarante;

    public void chequearSentencias(TablaSimbolos tabla) {
        if (this.inicializador != null) {
            // 1. Chequeo la expresión del lado derecho del '='
            Tipo tipoExpr = this.inicializador.chequear(tabla);
            
            // 2. Verifico que el tipo de la expresión conforme con el tipo del atributo
            // (o aplico las reglas de coerción si corresponden)
            if (!tipoExpr.conformaCon(this.tipo)) {
                throw new ExcepcionSemantica(this.token, 
                    "El tipo del inicializador no conforma con el tipo del atributo " + token.getLexema());
            }
        }
    }
}
```

### Cómo queda Clase.chequearSentencias()

La versión vigente está en "Chequeos de nodos". Primero se recorren los atributos inicializados,
después los constructores y al final los métodos. Siempre se chequean **sólo los propios**
(`attr.getClaseDeclarante() == this`, `m.getMiClase() == this`). No es sólo prolijidad: el
atributo heredado es el mismo objeto, y chequearlo desde la subclase repetiría sus errores con
la clase actual equivocada.

l concepto de `atributoActual` se usa únicamente durante el Paso 2 del bloque de código anterior: el instante en que el compilador está chequeando 
el AST de la expresión que está a la derecha del `=` de un atributo (`int edad = ...`)

**1. Cuando se chequea NodoThis (this)**

Imaginate que tenés:

```java
class A {
    int x = 10;
    static int a = this.x; // ¡ERROR! 'a' es static, no puede usar 'this'
    int b = this.x;        // VÁLIDO: 'b' no es static
}
```

Cuando se ejecuta `NodoThis.chequear()`: Si `metodoActual` es null y no tenés `atributoActual`, ¿cómo sabe `NodoThis` si está adentro de `static int a` (donde `this` es ilegal) o adentro de `int b` (donde `this` es válido)? No tiene forma de saberlo. En cambio, si guardaste `atributoActual`, `NodoThis` pregunta: "¿`atributoActual` es static? Entonces tiro error semántico".

**2. Cuando se chequea NodoLlamadaMetodo (m()) sin receptor explícito**

```java
class A {
    int m() { return 1; }
    static int a = m(); // ¡ERROR! No podés llamar a un método dinámico desde un atributo static
    int b = m();        // VÁLIDO
}
```

Igual que antes: cuando `NodoLlamadaMetodo.chequear()` ve que `m()` es un método dinámico (no estático), necesita verificar que no lo estén llamando desde un contexto estático.
Si `metodoActual` es null, mira a `atributoActual.esEstatic`

**3. Cuando se chequea NodoAccesoVar (x)**

```java
class A {
    int x = x + 1; // ¡ERROR en Java! Un atributo no puede inicializarse usando su propio valor
}
```

Cuando `NodoAccesoVar.chequear()` busca `"x"` en la tabla de símbolos, lo va a encontrar en `claseActual.getAtributos()`. Para darse cuenta de que están intentando hacer `x = x + 1` (o sea, leyéndose a sí mismo antes de terminar de declararse), 
`NodoAccesoVar` compara:

```java
if (atributoEncontrado == tablaSimbolos.getAtributoActual()) {
    throw new ExcepcionSemantica(token, "No se puede referenciar al atributo en su propia inicialización");
}
```

```java
// En Clase.chequearSentencias(tabla), sólo para los atributos propios con inicializador:
tabla.setUnidadActual(null, null, a); // <--- Acá empieza a existir atributoActual (metodo/constructor en null)
a.chequearSentencias(tabla);          // <--- Acá adentro se chequea el AST del '='
// La próxima unidad (otro atributo, un constructor o un método) vuelve a setear los tres campos.
```

## Mapeo no terminal → nodo

### Sentencias

| No terminal(es) del código | Nodo | Campos |
|---|---|---|
| `<Bloque> ::= { <ListaSentencias> }` | `NodoBloque` | `List<NodoSentencia> sentencias` |
| `<Sentencia> ::= ;` | `NodoSentenciaVacia` | — |
| `<Sentencia> ::= <VarLocal> ;`, `<VarLocal> ::= var idMetVar = <ExpresionCompuesta>` | `NodoVarLocal` | `Token nombre`, `NodoExpresion inicializador` |
| `<Sentencia> ::= <TipoPrimitivo> <RestoDeclLocal>` / `idGen <RestoDeclLocal>` / `idClase <SentIdClase>` (rama tipo) + `<RestoDeclLocal>`, `<MasIdsLocal>`, `<InitLocalOpc>` | `NodoVarLocalClasica` ⚠7 | `Tipo tipo`, `List<Token> nombres`, `NodoExpresion inicializador` (puede ser null) |
| `<Return> ::= return <ExpresionOpcional>` | `NodoReturn` | `Token return`, `NodoExpresion expresion` (null si `return;`) |
| `<If> ::= if ( <Expresion> ) <Sentencia> <ElseOpcional>` | `NodoIf` ⚠8 | `Token if`, `condicion`, `entonces`, `sino` (null sin `else`) |
| `<While> ::= while ( <Expresion> ) <Sentencia>` | `NodoWhile` | `Token while`, `condicion`, `cuerpo` |
| `<For>` + `<ClausulasFor>` en su forma clásica (`; <CondFor> ; <ActFor>`, `<Expresion> ; …`, o declaración vía `<TrasIdForTipo> ::= <InitLocalOpc> ; …` / rama `.` de `<TrasIdClaseFor>`) | `NodoFor` ⚠9 | `NodoSentencia inicializacion`, `NodoExpresion condicion`, `NodoExpresion actualizacion` (las tres pueden ser null), `NodoSentencia cuerpo` |
| `<For>` + `<TrasIdForTipo> ::= : <Expresion>` | `NodoForEach` | `Tipo tipo`, `Token nombre`, `NodoExpresion iterable`, `NodoSentencia cuerpo` |
| `<Sentencia> ::= <Expresion> ;` y rama `.` de `<SentIdClase>` | `NodoSentenciaExpresion` ⚠1 | `NodoExpresion expresion` |

### Expresiones

| No terminal(es) del código | Nodo | Campos |
|---|---|---|
| `<Expresion> ::= <ExpresionCompuesta> <RestoAsignacion>` con `<RestoAsignacion>` ≠ ϵ (`<OperadorAsignacion> ::= =`) | `NodoExpAsignacion` | `ladoIzq`, `ladoDer`, `operador` |
| `<ExpresionCompuestaResto> ::= <OperadorBinario> <ExpresionBasica> <ExpresionCompuestaResto>` | `NodoExpBinaria` | `ladoIzq`, `ladoDer`, `operador` |
| `<TernarioOpcional> ::= ? <ExpresionCompuesta> : <ExpresionCompuesta>` | `NodoExpTernaria` | `condicion`, `ladoIzq`, `ladoDer`, `operador` |
| `<ExpresionBasica> ::= <OperadorUnario> <Operando> <PostfijoOpcional>` | `NodoExpUnaria` ⚠5 | `operador`, `ladoDer` |
| `<PostfijoOpcional> ::= ++ … \| -- …` | `NodoExpPostfija` ⚠5 | `ladoIzq`, `operador` |

### Operandos y primarios

| No terminal(es) del código | Nodo | Campos |
|---|---|---|
| `<Primitivo> ::= intLiteral` | `NodoLiteralEntero` ⚠4 | `Token literal` |
| `<Primitivo> ::= charLiteral` | `NodoLiteralCaracter` ⚠4 | `Token literal` |
| `<Primitivo> ::= true \| false` | `NodoLiteralBooleano` ⚠4 | `Token literal` |
| `<Primitivo> ::= null` | `NodoLiteralNull` ⚠4 | `Token literal` |
| `<Operando> ::= stringLiteral <ReferenciaResto>` | `NodoLiteralString` ⚠4 | `Token literal` |
| `<Operando> ::= this <ReferenciaResto>` | `NodoThis` | `Token this` |
| `<Operando> ::= idMetVar <TrasId>`, `<TrasId> ::= <ArgsActualesOpcional> <ReferenciaResto>` con `<ArgsActualesOpcional>` = ϵ | `NodoAccesoVar` | `Token nombre` |
| ídem, con `<ArgsActuales>` | `NodoLlamadaMetodo` ⚠6 | `Token nombre`, `List<NodoExpresion> argumentos` (receptor implícito) |
| `<Operando> ::= <LlamadaMetodoEstatico> <ReferenciaResto>`, `<LlamadaMetodoEstatico> ::= idClase . idMetVar <ArgsActuales>` | `NodoLlamadaEstatica` ⚠6 | `Token clase`, `Token nombre`, `List<NodoExpresion> argumentos` |
| `<TrasId> ::= -> <Expresion>`; `<TrasParen> ::= ) -> <Expresion>`; `<TrasParenId> ::= , <ListaIdLambda> ) -> <Expresion>`; `<DecidirTrasCierre> ::= -> <Expresion>` | `NodoLambda` | `List<Token> parametros`, `NodoExpresion cuerpo` |
| `<TrasParen> ::= <Expresion> ) <ReferenciaResto>`; `<DecidirTrasCierre> ::= <ReferenciaResto>`; tercera rama de `<TrasParenId>` | `NodoExpParentizada` ⚠3 | `NodoExpresion expresion` |
| `<ReferenciaResto> ::= . idMetVar <ArgsActualesOpcional> …` con `<ArgsActualesOpcional>` = ϵ | `NodoVarEncadenada` ⚠2 ⚠10 | `Token nombre`, `NodoEncadenado siguienteEncadenado` |
| ídem, con `<ArgsActuales>` | `NodoLlamadaEncadenada` ⚠2 | `Token nombre`, `List<NodoExpresion> argumentos`, `NodoEncadenado siguienteEncadenado` |
| `<ReferenciaResto> ::= [ <Expresion> ] <ReferenciaResto>` | `NodoAccesoArregloEncadenado` ⚠2 | `Token corchete` (`[`, para el error), `NodoExpresion indice`, `NodoEncadenado siguienteEncadenado` |
| `<Operando> ::= new <RestoNew> …`, `<RestoNew> ::= idClase <TipoGenericoOpcionalNew> <RestoNewIdClase>`, `<RestoNewIdClase> ::= <ArgsActuales>` | `NodoCreacionObjeto` | `Tipo tipo`, `boolean esDiamante` (`<DiamanteOTipo>` = ϵ), `List<NodoExpresion> argumentos` |
| `<RestoNew>` (`<TipoPrimitivo>` / `idGen` / `idClase …`) + `<DimensionesNew> ::= [ <TrasCorcheteNew>`, `<TrasCorcheteNew> ::= <Expresion> ] <DimensionesConTamanioOpc>` | `NodoCreacionArregloConTamanio` ⚠11 | `Tipo tipoBase`, `List<NodoExpresion> tamanios` |
| `<TrasCorcheteNew> ::= ] <MasCorchetesVaciosNew> <InicializadorArreglo>` | `NodoCreacionArregloConInicializador` ⚠11 | `Tipo tipoBase`, `int dimensiones`, `NodoInicializadorArreglo inicializador` |
| `<InicializadorArreglo> ::= { <ListaValoresArregloOpcional> }`, `<ValorArreglo> ::= <ExpresionCompuesta> \| <InicializadorArreglo>` | `NodoInicializadorArreglo` ⚠12 | `List<ValorArreglo> valores` (expresión o inicializador anidado) |

### Pendientes de gramática

Algunos logros de este documento usan construcciones que **el parser todavía no
reconoce**. Sus nodos se reservan, pero la forma final queda abierta hasta
extender la gramática:

| Logro | Nodo reservado | Hereda de |
|---|---|---|
| Instanceof | `NodoExpInstanceof` (`ladoIzq`, `operador` = `instanceof`, tipo destino) | `NodoExpCompuesta` |
| Super! (acceso / llamada) | `NodoSuper` | `NodoPrimario` |
| Super! (`super(...)` en constructor) | `NodoLlamadaSuperConstructor` | `NodoSentencia` |
| Floats y Coerción | `NodoLiteralFloat` (el léxico ya produce `LIT_FLOAT`) | `NodoLiteral` |

## Árbol de herencia

`[pendiente]` marca los nodos que dependen de una extensión de la gramática.
`(abstracta)` marca las clases que no se instancian.

```
NodoSentencia (abstracta)
├── NodoSentenciaVacia
├── NodoBloque
├── NodoSentenciaExpresion          ⚠1
├── NodoVarLocal
├── NodoVarLocalClasica             ⚠7
├── NodoReturn
├── NodoIf                          ⚠8
├── NodoWhile
├── NodoFor                         ⚠9
├── NodoForEach
└── NodoLlamadaSuperConstructor     [pendiente]

NodoExpresion (abstracta)
├── NodoExpAsignacion               ladoIzq, ladoDer, operador
└── NodoExpCompuesta (abstracta)
    ├── NodoExpBinaria              ladoIzq, ladoDer, operador
    ├── NodoExpTernaria             condicion, ladoIzq, ladoDer, operador
    ├── NodoExpUnaria               operador, ladoDer           ⚠5
    ├── NodoExpPostfija             ladoIzq, operador           ⚠5
    ├── NodoExpInstanceof           [pendiente]
    └── NodoOperando (abstracta)
        ├── NodoLiteral (abstracta)                             ⚠4
        │   ├── NodoLiteralEntero
        │   ├── NodoLiteralCaracter
        │   ├── NodoLiteralBooleano
        │   ├── NodoLiteralNull
        │   └── NodoLiteralFloat    [pendiente]
        ├── NodoLambda
        └── NodoPrimario (abstracta)        encadenado          ⚠2
            ├── NodoThis
            ├── NodoLiteralString                               ⚠4
            ├── NodoAccesoVar
            ├── NodoLlamadaMetodo                               ⚠6
            ├── NodoLlamadaEstatica                             ⚠6
            ├── NodoCreacionObjeto
            ├── NodoCreacionArregloConTamanio                   ⚠11
            ├── NodoCreacionArregloConInicializador             ⚠11
            ├── NodoExpParentizada                              ⚠3
            └── NodoSuper           [pendiente]

NodoEncadenado (abstracta)          siguienteEncadenado         ⚠2
├── NodoVarEncadenada                                           ⚠10
├── NodoLlamadaEncadenada                                       ⚠6
└── NodoAccesoArregloEncadenado

ValorArreglo (interfaz)                                         ⚠12
├── NodoExpresion
└── NodoInicializadorArreglo
```

Todos los nodos van en el paquete `Model.semantico.ast`.

## Ejemplo: el árbol de `AST.png` con estos nodos

Para `a = 4*(b+2);`:

```
NodoSentenciaExpresion
└── NodoExpAsignacion                 operador: =
    ├── ladoIzq: NodoAccesoVar        token: a
    └── ladoDer: NodoExpBinaria       operador: *
        ├── ladoIzq: NodoLiteralEntero  token: 4
        └── ladoDer: NodoExpParentizada
            └── NodoExpBinaria        operador: +
                ├── ladoIzq: NodoAccesoVar     token: b
                └── ladoDer: NodoLiteralEntero token: 2
```

Coincide con `AST.png` salvo dos agregados. `NodoSentenciaExpresion` y
`NodoExpAsignacion` envuelven lo que la imagen deja fuera, y
`NodoExpParentizada` (⚠3) es un nodo explícito.

## Decisiones sobre los nodos

Las ambigüedades ⚠1–⚠13 se resolvieron en la entrevista de diseño del
2026-10-17. Cada una conserva la opción descartada, para saber qué se evaluó.

### ⚠1 · Asignación y llamada: ¿sentencia o expresión?
**Decisión: son expresiones.** La asignación es `NodoExpAsignacion` y la
llamada es `NodoLlamadaMetodo`, y las dos son `NodoExpresion`. Un único
`NodoSentenciaExpresion` envuelve `<Expresion> ;` y valida §12.2: la expresión
tiene que ser una asignación o terminar en una llamada o en un `new` de
objeto. Si no, error con lexema `;`.
- *Por qué:* en la gramática del código la asignación sale de
  `<Expresion> ::= <ExpresionCompuesta> <RestoAsignacion>`, y §11.9 la admite
  en contexto de expresión (`return x = 3;`, `var z = (x = 3);`, como
  condición).
- *Descartada:* que `NodoAsignacion` y `NodoLlamada` hereden de
  `NodoSentencia`. Obliga a tener cada construcción duplicada, una versión
  sentencia y otra versión expresión.

### ⚠2 · Encadenados (`<ReferenciaResto>`)
**Decisión: lista enlazada.** Cada `NodoPrimario` tiene un campo `encadenado`
de tipo `NodoEncadenado` (abstracta), con subclases `NodoVarEncadenada`,
`NodoLlamadaEncadenada` y `NodoAccesoArregloEncadenado`. Cada una tiene a su
vez su `siguienteEncadenado`. Calca `<ReferenciaResto>` 1 a 1. El último
eslabón decide `esAsignable()` y `esLlamable()` (§11.5: "forma completa y
último acceso"). Ver "Nodos encadenados".
- *Por qué:* es el modelo de la cátedra, y las trazas y la tabla de
  `esAsignable`/`esLlamable` de este documento ya estaban escritas sobre él.
  Recorrer la lista hasta el final es trivial.
- *Descartada:* árbol anidado a izquierda (`NodoAccesoAtributo`,
  `NodoLlamadaMetodo` y `NodoAccesoArreglo` con un `receptor`; `a.b[0].m()`
  como `Llamada(m, receptor = Arreglo(Atributo(b, receptor = a), 0))`).

### ⚠3 · Expresión parentizada
**Decisión (la exige §11.4): `NodoExpParentizada` se mantiene.** `AST.png` no
tiene nodo para los paréntesis, pero hace falta uno: conserva el tipo de la
expresión interna y corta `esAsignable()` y `esLlamable()` (`(x) = 3;`,
`(x = 3);` y `(metodo());` son errores). Sin el nodo, esa información se
pierde.

### ⚠4 · Literales
**Decisión: una subclase por tipo de literal.** `NodoLiteralEntero`,
`NodoLiteralCaracter`, `NodoLiteralBooleano` y `NodoLiteralNull` van bajo
`NodoLiteral` y devuelven su tipo fijo. `NodoLiteralString` va bajo
`NodoPrimario`: `stringLiteral` no está en `<Primitivo>`, cuelga de
`<Operando> ::= stringLiteral <ReferenciaResto>` y admite encadenado
(`"hola".toString()`).
- *Descartada:* un `NodoLiteral` genérico que decide el tipo según el
  `TokenType`. El String igual quedaba como caso especial por el encadenado.

### ⚠5 · Unario y postfijo
**Decisión: dos nodos.** `NodoExpUnaria(operador, ladoDer)` y
`NodoExpPostfija(ladoIzq, operador)`.
- En `<ExpresionBasica> ::= <OperadorUnario> <Operando> <PostfijoOpcional>` el
  postfijo liga primero al operando: `-a++` es `Unaria(-, Postfija(a, ++))`.
- `<PostfijoOpcional>` es recursiva (`a++--`). El parser lo acepta, y el
  chequeo de tipos lo rechaza porque `a++` no es asignable.
- *Descartada:* un solo nodo con un flag de posición.

### ⚠6 · Llamadas a método
**Decisión (sale de ⚠2): tres nodos.** `NodoLlamadaMetodo` es la llamada con
receptor implícito (`m()`), `NodoLlamadaEstatica` es `Clase.m()` y
`NodoLlamadaEncadenada` es `.m()`, el eslabón de la lista de ⚠2.
- *Descartada:* un único `NodoLlamadaMetodo` con receptor opcional. No encaja
  con la lista enlazada, donde el receptor es lo que está a la izquierda.

### ⚠7 · Variable local clásica con varios nombres
`int x, y, z = 10;`: `<RestoDeclLocal> ::= idMetVar <MasIdsLocal> <InitLocalOpc> ;`
tiene **un solo** inicializador al final para toda la lista.
**Decisión: como en Java, con un solo nodo.** `NodoVarLocalClasica` tiene
`Tipo tipo`, `List<Token> nombres` y `NodoExpresion inicializador`. El
inicializador se aplica **solo al último nombre**: en el ejemplo, sólo `z`
vale 10.
- *Descartadas:* aplicar el inicializador a todas las variables (no es Java),
  y desdoblar en un nodo por variable (misma semántica, pero el bloque recibe
  varias sentencias por una sola línea del fuente).

### ⚠8 · If con y sin else
**Decisión: un solo `NodoIf`**, con `sino` en `null` cuando no hay `else`. El
único comportamiento distinto es chequear `sino` si existe.
- *Descartada:* `NodoIf` y `NodoIfElse` separados.

### ⚠9 · Cláusulas del for clásico
**Decisión:**
- La inicialización es un `NodoSentencia`: un `NodoVarLocal`, un
  `NodoVarLocalClasica` o un `NodoSentenciaExpresion`.
- La condición y la actualización vacías (`for(;;)`) se representan con
  `null`.
- §12.3 (no se declara una local como cuerpo directo de `if`/`while`)
  **también aplica al cuerpo del `for`**, como en Java.
- La variable de la inicialización vive en un scope propio del `for`, que
  `NodoFor` abre y cierra en un `finally`.
- *Descartada:* que `NodoFor` tenga campos propios para la declaración inicial
  (tipo, nombre y valor) en vez de reusar los nodos de sentencia.

### ⚠10 · `length` de arreglos
**Decisión: lo resuelve `NodoVarEncadenada`.** Si el tipo de la izquierda es
un arreglo y el nombre es `length`, el resultado es `int` y `esAsignable()` da
`false` (§11.8, miembro de sólo lectura).
- *Descartada:* un nodo propio `NodoLength`. El parser no conoce los tipos, así
  que habría que reemplazar el nodo durante el chequeo.

### ⚠11 · Creación de arreglo con tamaño o con inicializador
`new int[3][4]` y `new int[][]{{1,2},{3,4}}` salen del mismo
`<TrasCorcheteNew>`.
**Decisión: dos nodos.** `NodoCreacionArregloConTamanio(tipoBase, tamanios)` y
`NodoCreacionArregloConInicializador(tipoBase, dimensiones, inicializador)`.
- *Descartada:* un `NodoCreacionArreglo` con dos formas excluyentes
  (`tamanios` o `inicializador`, uno de los dos en `null`).

### ⚠12 · `NodoInicializadorArreglo`
`{1, 2, 3}` no es una expresión suelta: sólo aparece después de `new T[]` o
dentro de otro inicializador (`<ValorArreglo>`).
**Decisión: no es un `NodoExpresion`.** Sus valores son `List<ValorArreglo>`,
una interfaz que implementan `NodoExpresion` y `NodoInicializadorArreglo`.
Así conviven valores e inicializadores anidados en la misma lista, sin que
`{1, 2}` pueda aparecer donde se espera una expresión.
- *Descartada:* que herede de `NodoExpresion` para mezclarlo en una
  `List<NodoExpresion>`.

### ⚠13 · Construcción en los caminos factorizados
`<SentIdClase>`, `<TrasIdClaseFor>`, `<TrasParenId>` y `<DecidirTrasCierre>`
**rearman a mano** la cola de `<Expresion>`, porque el prefijo ya se consumió
para decidir. Tienen que producir **exactamente los mismos nodos** que el
camino normal. Por ejemplo, `Foo.m().x = 3;` por `<SentIdClase>` tiene que dar
el mismo árbol que `this.m().x = 3;` por `<Expresion>`.
**Decisión: se corrige el parser al agregar el AST.** En la rama `.` de
`<SentIdClase>` y de `<TrasIdClaseFor>` se agregan las llamadas que faltan a
`postfijoOpcional()` y `ternarioOpcional()` (ver "Discrepancias" abajo), con
un caso de prueba sintáctico que cubra esos caminos.
- *Descartada:* dejarlo como deuda. Esas formas seguirían dando error
  sintáctico.

## Discrepancias entre comentarios de producción y código

Para este diseño se tomó como fuente el **cuerpo** de los métodos del parser.
Estos comentarios de `AnalizadorSintacticoImpl.java` no lo reflejan:

| Producción | Comentario | Lo que hace el código |
|---|---|---|
| `<Sentencia>` | no lista `<For>` | `sentencia()` despacha `PR_FOR` a `sentenciaFor()` |
| `<ExpresionCompuesta>` | `<ExpresionBasica> <ExpresionCompuestaResto>` | además llama a `ternarioOpcional()` |
| `<TrasParenId>`, tercera rama | `<TrasId> <ExpresionCompuestaResto> <RestoAsignacion> ) <ReferenciaResto>` | además llama a `postfijoOpcional()` y `ternarioOpcional()` |
| `<SentIdClase>` / `<TrasIdClaseFor>`, rama `.` | sin postfijo ni ternario | **tampoco** los llama: es un hueco real del parser, no sólo del comentario. Se corrige al agregar el AST (decisión ⚠13) |

## Relación con lo existente

- Este diseño reemplaza al stub `Código/src/Model/semantico/NodoSentencia.java` (interfaz vacía), que se mueve a `Model.semantico.ast`, y al boceto de `etapa3-semantico/analizador-semantico.md` ("Hace falta un AST de `Sentencia`/`Bloque`"), que usaba otros nombres.
- También reemplaza al `Entorno` de ese documento (scopes encadenados pasados por parámetro). Se decidió que la pila de scopes viva en `TablaSimbolos`, con cierre en `finally` (ver "Decisiones de diseño").
- Sigue valiendo la estrategia de construcción de ese documento ("Estrategia B anidada, con un único punto de attach"). Cada método del parser **devuelve** su nodo al contenedor inmediato, y `Metodo`/`Constructor` reciben el `NodoBloque` raíz una sola vez, al final.
