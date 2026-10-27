# Alcance

en esta etapa se realizará el chequeo de declaraciones.
Es decir, los bloques, vamos a analizar si fueron declaradas la variables utilizadas, si son compatibles,
si las sentencias están bien definidas.

**En esta etapa no se evalúa nada.** El chequeo de sentencias controla
tipos, declaraciones, el uso de las variables y la estructura de las
sentencias. Nunca calcula el valor de una expresión, ni siquiera de una
hecha sólo de literales (`1 > 9`, `!false`, `'a' == 'a'`). Cuando Java
necesita un valor al compilar (las expresiones constantes de JLS §15.29,
por ejemplo para decidir si un código es alcanzable), MiniJava no lo
calcula y se aparta de Java. Reconocer un literal no es evaluar, es mirar
qué token es: por eso `true` y `false` sí cuentan como condiciones
constantes (ADR 0009).


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

### For - Chequeado
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

### Super! - Chequeado

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

### Atributos Inicializados - Chequeado

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
- **Logros que cambian el resultado de casos base.** Hay dos logros que
  contradicen al documento base, y sus casos base esperan (o esperaban) el
  comportamiento base:
  - *Todos los caminos llevan al retorno* contra §12.5 (no se exige retorno
    en todos los caminos y el código inalcanzable se acepta). Resuelto en la
    spec 0004: el logro reemplaza a la regla base (ADR 0008), y
    `semIICorrecto31`, `semIICorrecto72` y `semIIError104` se mudaron al
    logro.
  - *Floats y coerción* contra §9.1 y §11.2 (sin conversiones `char`↔`int`):
    `semIIError33`, `34`, `52`, `59`, `70`. **Pendiente:** antes de
    implementarlo hay que decidir, idealmente con la cátedra, si el logro
    reemplaza a la regla base (y se ajustan esos casos) o si las dos
    conviven de algún modo.
- **Prerrequisito: `Metodo.miClase` nunca se setea.** "Solo miembros
  propios" compara `m.getMiClase() == this`, pero hoy el campo queda en
  `null`. Hay que setearlo al registrar el método, igual que la clase
  declarante de `Atributo` y `Constructor`.
- Quedan como están los nodos de inicializadores de arreglo (⚠11, ⚠12).
  Los ejemplos de atributos `static` de "Atributos inicializados" (la
  gramática sólo admite `static` en métodos, §6.1) se reemplazaron en la
  spec 0005.

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

## Decisiones de la spec 0004 (2026-10-26)

El logro *Todos los caminos llevan al retorno sin código muerto* se
planificó en `specs/0004-caminos-de-retorno.md` (4 tickets). Sus decisiones
difíciles de revertir están en dos ADR:

- **ADR 0008** (`adr/0008-caminos-de-retorno-reemplaza-base.md`): el logro
  está siempre activo en `ModuloPrincipalET4` y reemplaza la parte de §12.5
  y §13 que no controla el retorno ni el código inalcanzable. Se mantiene
  que una sentencia inalcanzable se chequea igual.
- **ADR 0009** (`adr/0009-condiciones-constantes-solo-literales.md`): sólo
  el literal `true` o `false`, solo o entre paréntesis, es una condición
  constante. No se evalúa ninguna expresión para decidir si un código es
  alcanzable: calcular valores no es tarea del chequeo semántico, que mira
  nombres, tipos y estructura. Se descartó plegar las expresiones
  constantes como javac (JLS §15.29).

### Para el informe de la Etapa 4

- El logro reemplaza la parte de §12.5 y §13 que dice que MiniJava no exige
  retorno en todos los caminos ni rechaza código inalcanzable (ADR 0008).
  Por eso CP-SII-38 de la cátedra (`semIICorrecto72`, ahora
  `semIIRetornoError20`) ya no da `[SinErrores]`: si la cátedra lo corre con
  su tester base, va a fallar.
- A diferencia de Java, sólo los literales `true` y `false` son condiciones
  constantes (ADR 0009). `int m1() { while (!false) {} }` da falta de
  retorno, y javac lo acepta (`semIIRetornoError11`); con
  `while (1 < 2) {} return 1;` MiniJava acepta el `return`, y javac lo marca
  como inalcanzable (`semIIRetornoCorrecto04`). Con el valor `false` pasa
  lo mismo: `while (1 > 9) { x = 1; }` es válido en MiniJava, y javac lo
  rechaza porque el cuerpo es inalcanzable (`semIIRetornoCorrecto04`, `m9`).
  La razón es que esta etapa no evalúa expresiones (ver "Alcance").
  Conviene confirmar con la cátedra esta diferencia con el "al igual que en
  Java" del logro.

## Decisiones de la spec 0005 (2026-10-26)

El logro *Atributos inicializados* se cerró en una entrevista de diseño y se
planifica en `specs/0005-atributos-inicializados.md`. No hay ADR: nada se
aparta de Java. El detalle está en "Atributos inicializados" (sección NODOS).

- **Conformidad.** El inicializador se chequea con las reglas base
  (§9–§11) y su tipo tiene que conformar con el del atributo, igual que el
  lado derecho de una asignación simple cuyo destino es el atributo: `void`
  no conforma, `null` sólo con referencias, argumentos genéricos y
  dimensiones exactos.
- **Error de conformidad.** `ERR_ASIGNACION_INCOMPATIBLE` con lexema `=`.
  `Atributo` guarda el token `=`, que hoy el parser descarta.
- **Contexto de instancia.** No hay atributos `static` (§6.1), así que en un
  inicializador valen `this`, `this.x`, un atributo implícito declarado
  antes o heredado y las llamadas sin receptor a métodos de instancia. La
  visibilidad se controla como en cualquier cuerpo de la clase.
- **Referencia adelantada (JLS §8.3.3).** Es error que un nombre suelto
  (`NodoAccesoVar`) del inicializador resuelva a un atributo **declarado por
  la clase actual** que no está declarado estrictamente antes del que se
  inicializa, el propio incluido (`int x = x + 1;`). No cuentan los accesos
  con receptor (`this.b`, `obj.b`), lo que lea un método llamado (`m()`), ni
  los atributos heredados. El orden es el del fuente: un índice de
  declaración en `Atributo` (la línea no alcanza con dos atributos en una
  línea, y `atributos` es un `HashMap`). En cuerpos de métodos y
  constructores la regla no aplica.
- **Excepción del destino.** Como en Java, el nombre suelto que es destino
  directo de `=` no es referencia adelantada (`int a = (b = 3); int b;` es
  válido). `b++`/`b--` sí lo son.
- **Error de referencia adelantada.** Código nuevo
  `ERR_REFERENCIA_ADELANTADA`, también para la autorreferencia; lexema y
  línea del nombre usado.
- **Sintaxis de otros logros** dentro del inicializador (ternario, lambda,
  diamante, `new T[]{...}`) sigue dando `ERR_NO_SOPORTADO` en su token, sin
  un segundo error del atributo.

## Decisiones de la spec 0006 (2026-10-27)

El logro *Super!* se cerró en una entrevista de diseño y se planificó en
`specs/0006-super.md` (4 tickets). La decisión difícil de revertir está en
el **ADR 0010** (`adr/0010-super-constructor-primera-sentencia-jdk21.md`):
`super(...)` es una sentencia y no una expresión, y sólo vale como primera
sentencia del cuerpo de un constructor, como en JDK 21 y no como en JDK 25
(JEP 513).

- **Léxico y gramática.** `super` es la palabra clave `PR_SUPER`, en todas
  las etapas. Entra en los FIRST de `<Operando>`, `<Expresion>` y
  `<Sentencia>` con dos formas: el acceso con super (`super.x`,
  `super.m(...)`, con el primer eslabón `.` obligatorio) y la sentencia
  `super <ArgsActuales> ;`. `super` suelto, `super[0]`, `x = super(1);` y
  `super(1).m();` son errores sintácticos.
- **Tipo de super.** El `extends` tal como se escribió (`A<String>`), o
  `Object` si no hay `extends` (también con `implements`, ADR 0005). Desde
  ahí, el encadenado se chequea como con cualquier receptor: miembros
  heredados, sustitución genérica, visibilidad y asignabilidad. Un miembro
  que sólo declara la clase actual o una interfaz implementada no existe en
  la superclase.
- **Contexto.** El acceso con super vale donde vale `this`: en un método
  estático es `ERR_SUPER_EN_ESTATICO`. En un inicializador de atributo
  vale, y `super.b` nunca es referencia adelantada.
- **Ubicación.** La decide el AST: el constructor marca su primera
  sentencia si es la llamada. Cualquier otra es `ERR_SUPER_FUERA_DE_LUGAR`,
  también en un método estático (gana a la de contexto estático).
- **Resolución.** Como `new`, sobre la superclase y con `super` en el papel
  del nombre de la clase: aridad, visibilidad y conformidad de los
  argumentos con los parámetros vistos desde el `extends`.
- **Contexto previo a la construcción** (JLS §8.8.7.1). En los argumentos
  de `super(...)` no se puede usar el objeto actual: `this`, un acceso con
  super, un atributo por su nombre suelto o una llamada sin receptor a un
  método de instancia dan `ERR_REFERENCIA_ANTES_DE_SUPER`, sobre el token
  del uso.
- **§6.3 se mantiene.** Un constructor sin `super(...)` no llama a ningún
  constructor de la superclase, ni se le exige a ésta uno sin parámetros.

### Para el informe de la Etapa 4

- `super(...)` sigue la regla de JDK 21 (ADR 0010): una sentencia antes,
  aunque no use el objeto actual, es error, y en JDK 25 no lo es.
- Como MiniJava no agrega la llamada implícita a `super()` (§6.3), una
  subclase cuya superclase sólo tiene constructores con parámetros compila
  sin escribir `super(...)` (`semIISuperCorrecto04`), y javac la rechaza.

## Estado de la implementación (2026-10-27)

Las specs 0003, 0004, 0005 y 0006 están cerradas. Quedó implementado:

- **Lo base de §9–§12.** Todos los casos de `resources/semantico2/{sinErrores,conErrores}`
  pasan con `ModuloPrincipalET4`, que es el main del jar. La tercera pasada es `TablaSimbolos.chequearSentencias()`, y sólo
  corre si no hubo errores léxicos, sintácticos ni de declaraciones.
  `ModuloPrincipalET3` no cambió: sobre los casos de `semantico/` da la
  misma salida que ET4.
- **Logros chequeados:** *Multi-detección Errores Semánticos en
  Sentencias* (cada sentencia se chequea aparte, y un error no corta las
  siguientes ni las de otros métodos) y *Postfijos*. Los dos están en
  `LOGROS_HABILITADOS` de `TesterLogrosSem2`.
- **Logro *For*, for clásico** (2026-10-25, después del cierre). `NodoFor`
  abre un scope propio para la variable de la inicialización. Siguiendo el
  logro al pie de la letra, la inicialización es una asignación o un `var`
  (`int i = 0` y una llamada son `ERR_FOR_INICIALIZACION`), la condición es
  boolean y la actualización es una asignación (`i++` es
  `ERR_FOR_ACTUALIZACION`). Las cláusulas vacías valen (`for (;;)`). Los
  errores propios del for van sobre `for`. El parser admite `var` en la
  inicialización. `for` está en `LOGROS_HABILITADOS`. Casos:
  `semIIForCorrecto01`–`04` y `semIIForError01`–`09`.
- **Logro *For*, for-each** (2026-10-25). Con esto el logro *For* queda
  completo. `MiniIterable<T>` es una interfaz
  predefinida (`void start(); T next(); boolean hasNext();`), registrada en
  `TablaSimbolos.registrarMiniIterable()` junto a Object, String y System
  (ADR 0004). `MiniIterable.java` en `Model.semantico` sólo documenta su
  forma. Un programa la implementa o la extiende como cualquier interfaz
  (con el control de contrato de la Etapa 3), y redeclararla es
  `ERR_CLASE_DUPLICADA`. `NodoForEach` chequea, en este orden: el tipo de la
  variable (mismas reglas y códigos que en el resto del cuerpo, raw
  incluido), el ocultamiento (`ERR_LOCAL_DUPLICADA` sobre el nombre) y la
  expresión. Después mira el tipo de la expresión como `MiniIterable` con
  `vistaComoAncestro()`. Si no lo es (un arreglo tampoco, a diferencia de
  Java), da `ERR_FOREACH_NO_ITERABLE`. Si el argumento que ve no conforma
  con el tipo de la variable, da `ERR_FOREACH_TIPO_INCOMPATIBLE`. Los dos van
  sobre `for`. La variable no se ve en la expresión, y vive en un scope
  propio que sólo cubre el cuerpo. El for-each con `var` no se admite (lo
  rechaza el parser). Como la conformidad es la de D9, un `MiniIterable<T>`
  se recorre como `T` pero no como `Object` (`semIIForError17`). Casos:
  `semIIForCorrecto05`–`09` y `semIIForError10`–`27`.
- **Logro *Instanceof*** (2026-10-25). `instanceof` es palabra clave
  (`PR_INSTANCEOF`) y entra en `<ExpresionCompuestaResto>` como otro
  eslabón de la cadena sin precedencia, con un `<TipoReferencia>` a la
  derecha: `x == y instanceof B` es `(x == y) instanceof B`. Un `T`, un
  arreglo o la variable asociada (`a instanceof B b`) a la derecha son
  errores sintácticos. `NodoExpInstanceof` (hereda de `NodoExpCompuesta`)
  da `boolean` y chequea, en este orden: que el operando sea una
  referencia o `null` (`ERR_OPERADOR_INCOMPATIBLE`), que el destino esté
  declarado (`ERR_TIPO_NO_DECLARADO`, sobre el nombre), que no sea genérico,
  ni con argumento ni raw (`ERR_INSTANCEOF_GENERICO`), y que los dos tipos
  estén relacionados como en el cast de Java (`ERR_OPERADOR_INCOMPATIBLE`).
  No hace falta que sean iguales: vale subir y bajar por la jerarquía, un
  `null` o un `T` contra cualquier clase y un arreglo contra `Object`. Como
  en Java, una interfaz y una clase no `final` (o dos interfaces) siempre
  se aceptan, porque pueden tener un subtipo común. Con una clase `final`,
  `String` incluido, vale sólo la conformidad. No se sigue la regla de
  Java 17 que usa sealed/permits para probar que dos tipos son disjuntos.
  Los errores propios van sobre `instanceof`. `instanceOf` está en
  `LOGROS_HABILITADOS`. Casos: `semIIInstanceOfCorrecto01`–`04` y
  `semIIInstanceOfError01`–`15`.
- **Logro *Visibilidad chequeada*** (2026-10-25). El parser guarda
  `private` en `Atributo`, `Metodo` y `Constructor` (`isPrivado()`). Sin
  modificador o con `public` el miembro es público (Parte I §6.1), igual
  que los métodos de interfaz, los predefinidos y el constructor por
  defecto. Los seis accesos (`x`, `m()`, `e.x`, `e.m()`, `C.m()` y
  `new C()`) llaman a `ChequeoVisibilidad.exigirAcceso()` apenas resuelven
  el miembro, antes que static y argumentos. Un private sólo se usa desde
  el código de su clase declarante y a través de un receptor de esa clase,
  como en Java: desde una subclase, implícito o con `this`, es error, y
  dentro de `A1` también lo es `b.x` con `b` de tipo `B2`
  (`ERR_ACCESO_PRIVADO`, sobre el nombre del miembro o, en `new`, de la
  clase). La Etapa 3 no cambia: los private se siguen copiando a las
  subclases y cuentan en redefiniciones y contratos, así que un método
  igual a uno private del padre se controla como redefinición, un private
  puede cumplir un método de interfaz, y reducir la visibilidad al
  redefinir no es error (en Java, las dos últimas sí lo son). Un
  constructor private no molesta a una subclase que no escribe
  `super(...)`, porque MiniJava no tiene la llamada implícita a `super()`
  (Parte I §6.3). Si la subclase lo llama con `super(...)`, es
  `ERR_ACCESO_PRIVADO` sobre `super` (spec 0006). `visibilidadChequeada`
  está en `LOGROS_HABILITADOS`. Casos: `semIIVisibilidadCorrecto01`–`04` y
  `semIIVisibilidadError01`–`11`.
- **Logro *Todos los caminos llevan al retorno sin código muerto***
  (2026-10-26, spec 0004). Siempre activo en `ModuloPrincipalET4`, sin
  opción para apagarlo (ADR 0008). El análisis de flujo sigue JLS §14.22,
  recortado a las sentencias de MiniJava, y sus reglas son estructurales:
  no miran tipos ni la tabla de símbolos. `NodoSentencia.salida(Flujo)` da el estado después de cada
  sentencia, con los tres estados de la `Liveness` de javac (`Flujo`:
  `VIVO`, `MUERTO`, `RECUPERANDO`). Un método no void cuyo cuerpo sale
  `VIVO` da `ERR_FALTA_RETORNO` sobre su nombre (`Metodo.exigirRetorno`,
  llamado desde `Clase.chequearSentencias` antes de chequear el cuerpo, así
  el error sale antes que los del cuerpo y no depende de sus errores de
  tipos). `NodoBloque.chequear` lleva el
  estado: una sentencia que llega `MUERTO` registra
  `ERR_SENTENCIA_INALCANZABLE` sobre su primer token
  (`NodoSentencia.primerToken()`), se chequea igual (§12.5) y el bloque
  sigue con `RECUPERANDO`. Así lo que sigue a una inalcanzable no se
  reporta en cascada, salvo que ella misma no termine normalmente (como
  javac, `return 1; return 2; x = 3;` da dos errores), y un cuerpo que sale
  `RECUPERANDO` no suma una falta de retorno. Como las inalcanzables se
  reportan durante el chequeo, una sentencia que corta por un error de
  tipos antes de llegar a sus ramas (`if (1) { return; x = 2; }`) no busca
  inalcanzables adentro (spec 0004, D5). Las
  inalcanzables se buscan también en métodos void y constructores.
  `NodoWhile` y `NodoFor` reportan el cuerpo como inalcanzable cuando la
  condición es la constante `false`. Sólo el literal `true` o `false`, solo
  o entre paréntesis, es una condición constante: `valorConstante()` sólo
  da un valor en `NodoLiteralBooleano` y en `NodoExpParentizada` sin
  encadenado, y no se evalúa ninguna operación (`while (1 < 2)` y
  `while (!false)` son bucles comunes; ADR 0009). Un `for` sin condición
  cuenta como `true`. Como en Java, el `if` nunca es constante y el
  for-each siempre termina normalmente. `caminosRetornoSinCodigoMuerto`
  está en `LOGROS_HABILITADOS`. Casos: `semIIRetornoCorrecto01`–`09` y
  `semIIRetornoError01`–`21`. `semIICorrecto31`, `semIICorrecto72`
  (CP-SII-38) y `semIIError104` dejaron la base y son `semIIRetornoError19`,
  `20` y `16`.
- **Logro *Atributos inicializados*** (2026-10-26, spec 0005). Siempre
  activo en `ModuloPrincipalET4`. `Clase.chequearSentencias` encola los
  atributos propios con inicializador junto con constructores y métodos,
  por línea (y, en una misma línea, por índice de declaración), setea
  `atributoActual` y atrapa el error del inicializador.
  `Atributo.chequearInicializador` chequea la expresión y exige que su tipo
  conforme con el del atributo, con la misma conformidad que
  `NodoExpAsignacion`: si no, `ERR_ASIGNACION_INCOMPATIBLE` sobre el `=`,
  que el parser ahora guarda en `Atributo`. Un error adentro del
  inicializador es el único error del atributo, y su tipo declarado sigue
  valiendo en sus usos. El inicializador es un contexto de instancia (no hay
  atributos `static`): valen `this`, `this.x`, los atributos anteriores o
  heredados y las llamadas sin receptor a métodos de instancia, y la
  visibilidad se controla como en cualquier cuerpo. La referencia
  adelantada (JLS §8.3.3) da `ERR_REFERENCIA_ADELANTADA` sobre el nombre:
  `NodoAccesoVar`, con `atributoActual` seteado, rechaza un atributo de la
  clase actual cuyo índice de declaración (`Atributo.getIndiceDeclaracion()`,
  asignado por `Clase.agregarAtributo()` en orden del fuente) no es menor
  que el del atributo que se inicializa, el propio incluido. No cuentan
  `this.b`, `obj.b`, los heredados ni lo que lea un método llamado. El
  destino directo de un `=` se salva (`NodoExpAsignacion` llama a
  `chequearComoDestino`, que sólo `NodoAccesoVar` sin encadenado
  redefine), pero `b++`, `b[0] = 1` y `b.x = 1` no. En métodos y
  constructores la regla no aplica. La sintaxis de otros logros en el
  inicializador sigue dando `ERR_NO_SOPORTADO` sobre su propio token.
  `atributosInicializados` está en `LOGROS_HABILITADOS`. Casos:
  `semIIAtribInicCorrecto01`–`05` y `semIIAtribInicError01`–`16`.
- **Logro *Super!*** (2026-10-27, spec 0006). Siempre activo en
  `ModuloPrincipalET4`. `super` es palabra clave (`PR_SUPER`) para todas
  las etapas. El parser lo acepta en `operando()`, con `accesoSuper()`
  (primer eslabón `.` obligatorio, después el `<ReferenciaResto>` de
  siempre), y en `sentencia()`, que lo intercepta como a `idClase`:
  `sentSuper()` arma con `(` la `NodoLlamadaSuperConstructor` y con `.` el
  acceso con super seguido de la cola de `<Expresion>`. `NodoSuper` controla
  el contexto estático (`ERR_SUPER_EN_ESTATICO`) y el previo a la
  construcción, y chequea el encadenado sobre
  `Clase.getTipoSuperclase()` (el `extends` tal como se escribió, u
  `Object`), así que miembros heredados, sustitución genérica, visibilidad y
  asignabilidad salen sin código nuevo. `Constructor.setCuerpo()` marca
  como en lugar la llamada que es literalmente la primera sentencia; una
  sin marcar da `ERR_SUPER_FUERA_DE_LUGAR` antes de mirar sus argumentos.
  La resolución del constructor (aridad, visibilidad, conformidad vista
  desde el tipo) pasó a `ChequeoLlamada.chequearConstructor()`, compartida
  con `NodoCreacionObjeto`, y sus errores van sobre `super`.
  `ErrorSemantico` ajusta el texto de `ERR_CONSTRUCTOR_NO_ENCONTRADO`,
  `ERR_ARGUMENTO_INCOMPATIBLE` y `ERR_ACCESO_PRIVADO` cuando el lexema es
  `super`. Mientras chequea sus argumentos, la llamada prende
  `TablaSimbolos.setContextoPrevioAConstruccion()` (y la apaga en un
  `finally`). Con la marca prendida, `NodoThis`, `NodoSuper`,
  `NodoAccesoVar` (un atributo) y `NodoLlamadaMetodo` (un método de
  instancia) dan `ERR_REFERENCIA_ANTES_DE_SUPER` sobre su token. Un
  parámetro con el nombre de un atributo resuelve antes y vale.
  `superKeyword` está en `LOGROS_HABILITADOS`. Casos:
  `semIISuperCorrecto01`–`06` y `semIISuperError01`–`20`.

Correcciones posteriores al cierre de la spec 0003 (code review,
2026-10-24):

- `new C<>[n]` se aceptaba en silencio: el parser perdía la marca de
  diamante en la forma de arreglo. Ahora da `ERR_NO_SOPORTADO` sobre el
  nombre de la clase, como `new C<>()` (`semIIError172`).
- Con Multi-detección los errores salían agrupados por tipo de miembro
  (inicializadores, constructores, métodos) y no en el orden del programa.
  `Clase.chequearSentencias` ahora ordena todas las unidades juntas por
  línea (`semIIMultiErrorError02`).
- Una local cuyo `var` fallaba no se registraba (lo que la spec 0003 dejó
  fuera de alcance), y cada uso posterior sumaba un "no declarada" falso.
  Ahora queda declarada sin tipo y sus usos cortan la sentencia sin otro
  error (`semIIMultiErrorError03`).
- `TesterLogrosSem2` exige que las etiquetas de la salida sean exactamente
  las esperadas y en orden; antes sólo controlaba que aparecieran.

Siguen pendientes, cada uno para una spec propia. El parser ya los acepta y
arma sus nodos, pero el chequeo reporta `ERR_NO_SOPORTADO` (spec 0003, D11):

- *Minilambdas*, *Ternarios*, *Variables Locales Clásicas
  Controladas*, *Métodos Genéricos E4* y *Genericidad Avanzada*
  (diamante).
- *Floats y coerción*: además hay que decidir qué pasa con los casos base
  que lo contradicen (`semIIError33`, `34`, `52`, `59`, `70`).

Para habilitar un logro: implementar su chequeo, poner en verde sus casos de
`resources/semantico2/logros/<logro>/` y agregar el nombre a
`LOGROS_HABILITADOS`.

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

Decisiones en "Decisiones de la spec 0005". Este boceto reemplaza al
anterior, que tenía ejemplos con atributos `static` (la gramática no los
admite, §6.1) y detectaba sólo `x = x` comparando con `atributoActual`.

`Atributo` guarda el inicializador (`NodoExpresion`, null si se declaró sin
`= ...`), el token `=` y un índice de declaración que asigna
`Clase.agregarAtributo()` en orden del fuente. `Clase.chequearSentencias()`
ya encola sólo los atributos propios con inicializador, ordenados por línea
junto con constructores y métodos, setea `atributoActual` (los otros dos
campos en null) y atrapa el error: un atributo mal inicializado no corta el
resto de la clase.

```java
// Atributo
public void chequearInicializador(TablaSimbolos tabla) {
    Tipo tipoInicializador = inicializador.chequear(tabla);
    if (!tipoInicializador.conformaCon(tipo)) {
        throw new ExcepcionSemantica(asignacion, "ERR_ASIGNACION_INCOMPATIBLE");
    }
}
```

Referencia adelantada: cuando `NodoAccesoVar` resuelve su nombre a un
atributo y `atributoActual != null`, es error si el atributo encontrado es
de la clase actual (`getClaseDeclarante() == claseActual`) y su índice no es
menor que el de `atributoActual`, salvo que el nodo sea el destino directo
de un `NodoExpAsignacion`.

```java
class A extends P {      // P declara p
    int a = 1;
    int b = a + p;       // válido: a está antes, p es heredado
    int c = d;           // ERROR: referencia adelantada a d
    int d = this.e;      // válido: acceso con receptor
    int e = e + 1;       // ERROR: autorreferencia
    int f = (g = 3);     // válido: g es destino directo de =
    int g = h++;         // ERROR: ++ lee h
    int h = m();         // válido aunque m lea atributos posteriores
    int m() { return h; }
}
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
| `<Sentencia> ::= <Expresion> ;`, rama `.` de `<SentIdClase>` y rama `<AccesoSuper>` de `<SentSuper>` | `NodoSentenciaExpresion` ⚠1 | `NodoExpresion expresion` |
| `<Sentencia> ::= super <SentSuper>`, `<SentSuper> ::= <ArgsActuales> ;` (logro *Super!*) | `NodoLlamadaSuperConstructor` | `Token super`, `List<NodoExpresion> argumentos`, `boolean enLugar` (lo marca `Constructor.setCuerpo()`) |

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
| `<Operando> ::= super <AccesoSuper>`, `<AccesoSuper> ::= . idMetVar <ArgsActualesOpcional> <ReferenciaResto>` (logro *Super!*) | `NodoSuper` | `Token super` (siempre con encadenado) |
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
└── NodoLlamadaSuperConstructor

NodoExpresion (abstracta)
├── NodoExpAsignacion               ladoIzq, ladoDer, operador
└── NodoExpCompuesta (abstracta)
    ├── NodoExpBinaria              ladoIzq, ladoDer, operador
    ├── NodoExpTernaria             condicion, ladoIzq, ladoDer, operador
    ├── NodoExpUnaria               operador, ladoDer           ⚠5
    ├── NodoExpPostfija             ladoIzq, operador           ⚠5
    ├── NodoExpInstanceof           ladoIzq, operador, tipoDestino
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
            └── NodoSuper

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

Las cuatro se corrigieron al agregar el AST (spec 0003, ticket 02): los
comentarios de producción ya reflejan el código, y la rama `.` de
`<SentIdClase>` y de `<TrasIdClaseFor>` llama a `postfijoOpcional()` y
`ternarioOpcional()` (caso `sintactico/sinErrores/sintCorrecto38.java`).

## Relación con lo existente

- Este diseño reemplaza al stub `Código/src/Model/semantico/NodoSentencia.java` (interfaz vacía), que se mueve a `Model.semantico.ast`, y al boceto de `etapa3-semantico/analizador-semantico.md` ("Hace falta un AST de `Sentencia`/`Bloque`"), que usaba otros nombres.
- También reemplaza al `Entorno` de ese documento (scopes encadenados pasados por parámetro). Se decidió que la pila de scopes viva en `TablaSimbolos`, con cierre en `finally` (ver "Decisiones de diseño").
- Sigue valiendo la estrategia de construcción de ese documento ("Estrategia B anidada, con un único punto de attach"). Cada método del parser **devuelve** su nodo al contenedor inmediato, y `Metodo`/`Constructor` reciben el `NodoBloque` raíz una sola vez, al final.
