---
titulo: Logro Variables Locales Clásicas (Etapas 2 y 4)
estado: ready-for-agent
origen: entrevista de diseño (grill with docs, 2026-10-28)
fecha: 2026-10-28
---

# Logro *Variables Locales Clásicas* (Etapas 2 y 4)

## Problem Statement

El logro *Variables Locales Clásicas* de la Etapa 2 dice: "El compilador
permite declarar variables locales de la forma clásica de Java, es decir,
sin usar var e indicando su tipo (por ej `int x;`). Además permite en una
misma sentencia declarar varias variables e inicializarlas (por ejemplo
`int x,y,z = 10;`)". Su par de la Etapa 4, *Variables Locales Clásicas
Controladas*, pide que esas declaraciones "(en todas sus variantes) son
correctamente tipadas".

Hoy fallan las dos etapas:

- **Sintaxis.** La gramática admite un único inicializador al final de la
  lista (`<RestoDeclLocal> ::= idMetVar <MasIdsLocal> <InitLocalOpc> ;`).
  Así, `int x = 1, y = 2;`, que es Java válido, da un error sintáctico sobre
  la `,`. Tampoco se puede declarar una local de tipo arreglo (`int[] a;`
  da error sobre el `[`, `sintError44`).
- **Semántica.** `NodoVarLocalClasica` reporta siempre `ERR_NO_SOPORTADO`
  sobre el primer nombre (spec 0003, D11). Por eso `int x = 1;` no compila
  en `ModuloPrincipalET4`, y `for (int i = 0; …)` da `ERR_FOR_INICIALIZACION`.
- **Casos.** El logro tiene sólo un esqueleto, y su caso con error
  (`semIIVarLocalError01`) en realidad prueba `var x = null`. No está en
  `LOGROS_HABILITADOS`.
- **Documentación.** `analizador-sintactico.md` (REQ-AS-006, "Init
  compartido") habla de "un valor común al final para toda la lista", y
  `requerimientos.md` (⚠7) fija un inicializador que vale para el último
  nombre. Ninguna de las dos describe lo que hace Java.

## Solution

Una local clásica se declara como en Java:

- **Cada nombre tiene su propio inicializador opcional** (un *declarador*,
  ver `GLOSARIO.md`). `int x = 1, y = 2;` es válido, y en `int x, y, z =
  10;` sólo `z` vale 10.
- **El tipo puede ser un arreglo:** `int[] a = new int[3];`, `A1[] b;`,
  `T[] t;`.
- **El tipo se controla una vez** con las reglas de los tipos escritos en un
  cuerpo (clase o interfaz existente, sin tipos raw, parámetro de tipo
  visible).
- **Los declaradores se procesan de izquierda a derecha.** Cada
  inicializador tiene que conformar con el tipo declarado, que es su *tipo
  esperado* (ADR 0011: un ternario se tipa contra él). Después, el nombre
  se declara con las reglas de ocultamiento de siempre: no puede ser el de
  una local visible ni el de un parámetro, pero sí el de un atributo. Un
  inicializador ve los nombres anteriores pero no el suyo: `int x = 1, y =
  x;` es válido y `int x = x;` se comporta como `var x = x`.
- **No hay control de asignación definida** (ADR 0012): `int x; return x;`
  es válido.
- **Un error por sentencia**, pero todos los nombres se declaran igual, así
  que las sentencias siguientes no suman errores en cascada.
- **`for (int i = 0; …)` es válido**, como en Java.

## User Stories

1. Como programador MiniJava, quiero declarar `int x;` dentro de un método, para usar una variable con tipo explícito sin `var`.
2. Como programador MiniJava, quiero declarar `int x = 1, y = 2;`, para inicializar cada variable de una lista con su propio valor.
3. Como programador MiniJava, quiero que en `int x, y, z = 10;` sólo `z` valga 10, para que el programa signifique lo mismo que en Java.
4. Como programador MiniJava, quiero mezclar nombres con y sin inicializador en cualquier orden (`int x, y = 2, z;`), como en Java.
5. Como programador MiniJava, quiero que `int x = 1, y = x;` sea válido, para inicializar una variable con otra declarada antes en la misma sentencia.
6. Como programador MiniJava, quiero declarar locales de tipo clase, interfaz, genérico instanciado o parámetro de tipo (`A1 a;`, `Caja<A1> c = new Caja<A1>();`, `T t;`), igual que un atributo.
7. Como programador MiniJava, quiero declarar locales de tipo arreglo (`int[] a = new int[3];`, `A1[] b;`, `T[] t;`), igual que un atributo.
8. Como programador MiniJava, quiero que `for (int i = 0; i < 10; i = i + 1)` compile, como en Java.
9. Como programador MiniJava, quiero que una local clásica pueda ocultar un atributo de la clase, como `var` y como en Java.
10. Como programador MiniJava, quiero usar el mismo nombre en dos bloques disjuntos (`{ int x; } { boolean x; }`), porque la primera ya no es visible.
11. Como programador MiniJava, quiero usar una local declarada sin inicializador sin que el compilador me lo impida, sabiendo que no se controla la asignación definida (ADR 0012).
12. Como programador MiniJava, quiero que `A1 a = c ? new B2() : new C3();` sea válido con `B2` y `C3` subclases de `A1`, porque el ternario se tipa contra el tipo declarado.
13. Como programador MiniJava, quiero un error sobre el `=` cuando el inicializador no conforma con el tipo declarado (`int x = true;`), igual que en un atributo inicializado.
14. Como programador MiniJava, quiero que, en una lista, el error vaya sobre el `=` del declarador que falla (`int x = 1, y = true;` → el segundo `=`), para encontrarlo enseguida.
15. Como programador MiniJava, quiero un error sobre el `?` cuando una rama de un ternario no conforma con el tipo declarado (`int x = c ? 1 : true;`), sin un segundo error sobre el `=`.
16. Como programador MiniJava, quiero un error sobre el segundo nombre en `int x, x;`, porque es una local duplicada.
17. Como programador MiniJava, quiero un error cuando una local clásica repite el nombre de un parámetro o de una local visible de un bloque contenedor, como con `var`.
18. Como programador MiniJava, quiero que, si el inicializador de un declarador falla y además el nombre choca, se reporte el inicializador (como en `var`), para que el orden de los errores sea predecible.
19. Como programador MiniJava, quiero un error sobre el tipo cuando no existe (`Foo x;`) o es un genérico sin argumentos (`Caja c;`), con las mismas reglas que el resto de los tipos escritos en un cuerpo.
20. Como programador MiniJava, quiero que con un tipo inválido no se chequeen los inicializadores, para no recibir errores derivados de un tipo que ya está mal.
21. Como programador MiniJava, quiero que `int x = x;` sin un atributo `x` dé "variable no declarada" sobre el `x` de la derecha, porque el inicializador no ve el nombre que se declara.
22. Como programador MiniJava, quiero que `int x = x;` con un atributo `x` sea válido y lea el atributo, igual que `var x = x`.
23. Como programador MiniJava, quiero que una local declarada como cuerpo directo de un `if`, `while` o `for` (`if (c) int x = 1;`) dé error sobre su primer nombre, como hoy.
24. Como programador MiniJava, quiero que una local clásica inalcanzable dé `ERR_SENTENCIA_INALCANZABLE` sobre su tipo, como hoy.
25. Como programador MiniJava, quiero que un error en una declaración no impida declarar sus nombres, para que las sentencias siguientes se chequeen contra los tipos declarados y no den errores en cascada.
26. Como programador MiniJava, quiero que `int x = true, y = 2;` seguido de `y = false;` dé dos errores (los dos `=`), porque `y` quedó declarada `int` (logro *Multi-detección*).
27. Como programador MiniJava, quiero un error sintáctico claro ante una coma colgante (`int x = 1, ;`), un inicializador vacío (`int x = ;`) o un `[` sin cerrar en el tipo (`int[ a;`).
28. Como programador MiniJava, quiero que el `for` siga admitiendo un único nombre sin arreglo en su inicialización, como hasta ahora.
29. Como alumno que entrega el compilador, quiero que la documentación de la Etapa 2 describa la gramática nueva y deje de hablar de "un valor común", para que el informe coincida con el código.
30. Como alumno que entrega el compilador, quiero que `requerimientos.md` explique las decisiones de este logro y la diferencia con Java (asignación definida), para defenderlas ante la cátedra.
31. Como mantenedor, quiero el logro en `LOGROS_HABILITADOS` con casos que cubran cada regla, para que una regresión rompa `./test.sh`.
32. Como mantenedor, quiero que los casos sintácticos y del logro *For* que fijaban el comportamiento viejo se corrijan, para que la suite describa el lenguaje actual.

## Implementation Decisions

**D1 · Alcance.** El cambio sintáctico afecta a `ModuloPrincipalET2`, `ET3` y
`ET4`, que comparten el parser. El chequeo semántico es de `ET4`
(`chequearSentencias`), siempre activo. La Etapa 3 no chequea cuerpos, así
que sus casos no cambian.

**D2 · Gramática.** En reemplazo de `<RestoDeclLocal>` y `<MasIdsLocal>`:

```
<RestoDeclLocal>   ::= <DimensionesOpcionales> <Declarador> <MasDeclaradores> ;
<Declarador>       ::= idMetVar <InitLocalOpc>
<MasDeclaradores>  ::= , <Declarador> <MasDeclaradores> | ϵ
<InitLocalOpc>     ::= = <ExpresionCompuesta> | ϵ
```

- Las tres entradas a `<RestoDeclLocal>` (tipo primitivo, `idGen`, y la rama
  tipo de `<SentIdClase>` después de `<TipoGenericoOpcional>`) no cambian.
  Las dimensiones se pueden poner al principio de `<RestoDeclLocal>` o
  después del tipo base en cada entrada; lo que importa es que se apliquen
  al tipo y que el `for` no las admita.
- Sigue siendo LL(1). Después del tipo, `{ [ }` y `{ idMetVar }` son
  disjuntos. En la rama tipo de `<SentIdClase>`, el `[` se suma a
  `{ <, idMetVar }`, y la otra rama sigue empezando con `{ . }`. En
  `<MasDeclaradores>`, `{ , }` y `FOLLOW = { ; }` son disjuntos. En
  `<InitLocalOpc>`, `{ = }` y `{ ,, ; }` también. Hay que actualizar los
  FIRST/FOLLOW del documento de la Etapa 2.
- El `for` sigue igual: `<TrasIdForTipo>` usa `<InitLocalOpc>` para un solo
  nombre y sin dimensiones. `for (int i = 0, j = 0; …)` sigue siendo un
  error sintáctico sobre la `,`.
- `<InitLocalOpc>` tiene que devolver también el token `=`, en las locales y
  en el `for`, para el lexema de D6.
- Modo pánico: los errores dentro de una declaración se recuperan como hoy
  (el `;` de cierre sigue con `matchCierre`).

**D3 · AST.** Sigue habiendo un `NodoVarLocalClasica` por sentencia (no uno
por nombre): el bloque recibe una sentencia por cada declaración del
fuente, que es lo que cuentan el flujo (spec 0004) y la *Multi-detección*.
El nodo guarda el `Tipo` (ya con sus dimensiones) y una lista ordenada de
declaradores. Cada declarador tiene el token del nombre, el token `=` (null
sin inicializador) y la expresión inicializadora (null sin inicializador).
`primerToken()` sigue siendo el token del tipo (spec 0004, D7), y
`ERR_DECLARACION_COMO_CUERPO` sigue sobre el primer nombre. El `for` arma
el mismo nodo con un solo declarador.

**D4 · Tipo.** Se resuelve una sola vez, antes de los declaradores, con la
misma resolución que los demás tipos escritos en un cuerpo (la que usa el
for-each): clase o interfaz existente, sin tipos raw (ADR 0007), parámetro
de tipo visible, arreglos de cualquiera de ellos. Si falla, el error va
sobre el token del tipo (`Foo`, `Caja`), no se chequea ningún inicializador,
y cada nombre que no choca (D5) se declara **sin tipo**
(`VariableLocal.sinTipo`), así sus usos posteriores no dan errores en
cascada.

**D5 · Declaradores.** Con el tipo válido, de izquierda a derecha, cada
declarador:

1. Si tiene inicializador, lo chequea con
   `chequearConTipoEsperado(tabla, tipoDeclarado)` (ADR 0011). Si el
   resultado no conforma con el tipo declarado, lanza
   `ERR_ASIGNACION_INCOMPATIBLE` sobre su `=`, igual que
   `Atributo.chequearInicializador`. Un error del ternario va sobre su `?`
   sin un segundo error sobre el `=`.
2. Busca el nombre. Si es una local visible (de este bloque o de uno
   contenedor, incluidas las declaradas antes en la misma sentencia) o un
   parámetro, lanza `ERR_LOCAL_DUPLICADA` sobre el nombre. Un atributo no
   choca: la local lo oculta.
3. Declara la local con el tipo declarado.

El inicializador se chequea antes de buscar el nombre, como en `var`. Así,
en `void m(int p) { int p = true; }`, el error es el del `=`.

**D6 · Un error por sentencia.** El primer error de un declarador corta el
chequeo de la sentencia y es el único que se reporta (`NodoBloque` lo
recoge, como siempre). Antes de propagarlo, el nodo declara los nombres que
quedan: el del declarador que falló, si no chocaba, y los siguientes que no
choquen, todos con el tipo declarado. Sus inicializadores no se chequean.
Así, en `int x = true, y = noExiste;` sólo sale el primer `=`, y `y` queda
`int`. Un nombre que choca no se declara, y sigue valiendo la variable
anterior.

**D7 · Asignación definida.** No se controla (ADR 0012). Una local sin
inicializador queda declarada con su tipo y se puede leer, asignar y pasar
libremente.

**D8 · For.** `NodoFor` acepta como inicialización una local clásica, además
de un `var` y una asignación. Deja de dar `ERR_FOR_INICIALIZACION` para
`int i = 0`, y la local queda en el scope propio del `for`, como el `var`.
Una llamada sigue siendo `ERR_FOR_INICIALIZACION`.

**D9 · Lo que no cambia.**

- El flujo: una local clásica completa normalmente, y si es inalcanzable el
  error va sobre su tipo (spec 0004).
- `var`: mismas reglas y mismos errores.
- La regla de referencia adelantada (spec 0005) es sólo de inicializadores
  de atributo y no aplica acá.
- Contrato de salida: `Error Semántico en línea <L>: <descripción>` y
  después `[Error:<lexema>|<línea>]`.

### Códigos de error

Todos existen. No hay códigos nuevos.

| Código | Lexema | Cuándo |
|---|---|---|
| `ERR_ASIGNACION_INCOMPATIBLE` | `=` del declarador | el inicializador no conforma con el tipo declarado |
| `ERR_TERNARIO_*` | `?` | un ternario inicializador mal tipado (spec 0007) |
| `ERR_LOCAL_DUPLICADA` | nombre | choca con una local visible o un parámetro |
| los de tipo en un cuerpo (inexistente, raw) | token del tipo | el tipo declarado no es válido |
| `ERR_VAR_NO_DECLARADA` | nombre usado | p. ej. `int x = x;` sin atributo `x` |
| `ERR_DECLARACION_COMO_CUERPO` | primer nombre | declaración como cuerpo directo de `if`/`while`/`for` |
| `ERR_SENTENCIA_INALCANZABLE` | token del tipo | declaración inalcanzable |

La tabla de convenciones de `etapa4-semantico2/casos-de-prueba.md` suma las
filas de la local clásica.

## Testing Decisions

Se observa sólo la salida del compilador (las etiquetas `[Error:…]` o
`[SinErrores]`), como en las specs 0001–0007. No hay tests unitarios. Los
puntos de prueba son los testers que ya existen:

- `TesterSintacticoDeCasos{Sin,Con}Errores` para la gramática (D2).
- `TesterLogrosSem2` sobre `resources/semantico2/logros/variablesLocalesClasicas/`
  y `logros/for/`, que exige **exactamente** las etiquetas `///[...]`
  iniciales, en orden.

Decisiones:

- **Habilitar el logro:** `variablesLocalesClasicas` se agrega a
  `LOGROS_HABILITADOS`.
- **Lexemas repetidos:** dos `=` o dos nombres iguales en la misma línea dan
  la misma etiqueta. Cuando un caso tiene que distinguir declaradores
  (Error 02 y 03), el declarador que falla va en su propia línea.
- **Casos que cambian:**
  - `sintactico/conErrores/sintError44` (local `int[] x;`) pasa a
    `sintactico/sinErrores/sintCorrecto39`, con el comentario reescrito.
  - `sintactico/conErrores/sintError50` declaraba `int[] datos;` en la línea
    7 y esperaba el error ahí. Ahora la declaración es válida y el error
    pasa a la línea 8, donde está el `[` sin cerrar que el caso quería
    probar. La etiqueta (lexema y línea) se fija corriendo el caso.
  - `logros/for/conErrores/semIIForError04` (`for (int i = 0; …)`) pasa a
    `logros/for/sinErrores/semIIForCorrecto10`, con el comentario
    reescrito. El conteo del logro *For* queda en 10 + 26.
  - `logros/variablesLocalesClasicas/conErrores/semIIVarLocalError01` (que
    probaba `var x = null`, ya cubierto en la base) se reemplaza por Error
    01 de la tabla.
- **Casos sintácticos nuevos:**
  - `sintCorrecto40`: varios declaradores mezclados (`int x, y = 2, z;`,
    `int a = 1, b = 2;`), locales de arreglo de tipo primitivo, de clase,
    de genérico instanciado y de parámetro de tipo, con una y dos
    dimensiones, y un `for` con local clásica.
  - `sintError68` (64–67 ya los usaba la spec 0008): coma colgante, `int x = 1, ;` (error sobre el `;`).
  - `sintError69`: `for (int i = 0, j = 0; …)` (error sobre la `,`).
- **Casos correctos del logro** (`semIIVarLocalCorrectoNN`):

  | # | Qué prueba |
  |---|---|
  | 01 | *(existe)* `var` y locales clásicas simples |
  | 02 | Declaradores mezclados: `int x, y = 2, z;` y `int a = 1, b = 2;`, y después se usan todas |
  | 03 | Izquierda a derecha: `int x = 1, y = x;` |
  | 04 | Tipos de referencia y genéricos: `Caja<A1> c = new Caja<A1>();`, `String s = "a", t;`, `A1 a = null;` |
  | 05 | Arreglos: `int[] a = new int[3];`, `A1[] b;` y `T[] t;` en una clase genérica |
  | 06 | Oculta un atributo: `int a1 = 2;` con atributo `a1`, e `int b1 = b1;` que lee el atributo `b1` |
  | 07 | Sin inicializador, usada libremente (ADR 0012): `int x; return x;` y `A1 a; a = new A1();` |
  | 08 | Ternario con hermanas contra el tipo declarado: `A1 a = c ? new B2() : new C3();` |
  | 09 | El mismo nombre en bloques disjuntos: `{ int x = 1; } { boolean x = true; }` |
  | 10 | `for (int i = 0; i < 10; i = i + 1)` y `for (A1 a = null; a == null; a = new A1())` |

- **Casos con error del logro** (`semIIVarLocalErrorNN`):

  | # | Qué prueba | Etiquetas, en orden |
  |---|---|---|
  | 01 | `int x = true;` | `=` |
  | 02 | `int x = 1,` / `y = true;` (el segundo declarador en otra línea) | `=` (el segundo) |
  | 03 | `int x,` / `x;` | `x` (el segundo) |
  | 04 | Choca con un parámetro: `void m(int p) { int p; }` | `p` |
  | 05 | Choca con una local de un bloque contenedor: `int x = 1;` y adentro de un `if`, `int y, x = 2;` | `x` (el interno) |
  | 06 | Tipo inexistente: `Foo x;` | `Foo` |
  | 07 | Genérico sin argumentos: `Caja c;` | `Caja` |
  | 08 | `int x = x;` sin atributo `x` | `x` (el de la derecha) |
  | 09 | Arreglo incompatible: `int[] a = new boolean[2];` | `=` |
  | 10 | Referencia incompatible: `A1 a = new Object();` | `=` |
  | 11 | Rama de ternario incompatible: `int x = c ? 1 : true;` | `?` |
  | 12 | Declaración como cuerpo: `if (c) int x = 1;` | `x` |
  | 13 | Inalcanzable: `return;` y después `int x = 1;` | `int` |
  | 14 | Inicializador antes que el choque: `void m(int p) { int p = true; }` | `=` |
  | 15 | Tipo inválido sin chequear inicializadores: `Foo x = new NoExiste(), y;` y en la línea siguiente `y = 1;` | `Foo` |
  | 16 | *Multi-detección*: `int x = true, y = noExiste;` y en la línea siguiente `y = false;` | `=`, `=` (línea siguiente) |

  Las líneas se fijan al escribir cada archivo. Un caso con error que
  dependa de otro logro no implementado no vale.

- **Base:** ningún caso base usa locales clásicas (se revisó con una
  búsqueda). Los que las usan están en logros no habilitados
  (`floatsYCoercion`, `genericidadAvanzada`), que no corren.
- **Corrida completa:** `./test.sh` en verde al final de cada ticket.
- **Antecedentes:** el `var` (`NodoVarLocal`, mismo orden inicializador →
  choque → declarar y misma recuperación sin tipo), el for-each (resolución
  del tipo escrito en un cuerpo), los atributos inicializados (spec 0005,
  error sobre el `=`) y los ternarios (spec 0007, tipo esperado).

## Out of Scope

- Asignación definida (JLS cap. 16, ADR 0012).
- Arreglos y varios nombres en la inicialización del `for`, y `var` en el
  for-each.
- Dimensiones después del nombre (`int a[];`), que MiniJava no admite en
  ningún lado.
- Inicializadores de arreglo (`int[] a = {1, 2};`): es otro logro y sigue
  dando `ERR_NO_SOPORTADO`. Cuando se implemente, el tipo declarado de la
  local es su tipo esperado.
- Coerción numérica (`float f = 1;`): es del logro *Floats y Coerción*.
- `final` en locales.
- Generar código para las locales: es de la Etapa 5.

## Further Notes

- Referencias: JLS §14.4 (declaración de variables locales, un
  inicializador por declarador), §6.3 (alcance), §6.4 (ocultamiento) y §5.2
  (contexto de asignación). En Java el alcance de una local incluye su
  propio inicializador, así que `int x = x;` es "might not have been
  initialized" (asignación definida). Acá, como no hay asignación definida,
  el inicializador no ve su nombre (D5, ADR 0012).
- Vocabulario: `GLOSARIO.md` (*variable local clásica*, *declarador*, *tipo
  esperado*), actualizado en la entrevista.
- **ADR 0012** (`adr/0012-locales-sin-asignacion-definida.md`, escrito en la
  entrevista): no se controla la asignación definida. La diferencia con
  Java conviene confirmarla con la cátedra y mencionarla en el informe.
- Al cerrar hay que actualizar:
  - en `etapa2-sintactico/analizador-sintactico.md`: REQ-AS-006 (sacar "un
    valor común al final para toda la lista"), la gramática expandida y la
    factorización de `<Sentencia>` (D2), el párrafo "Init compartido", sus
    FIRST/FOLLOW, y la nota de alcance del `for` que dice que
    `<RestoDeclLocal>` no admite arreglos;
  - en `etapa4-semantico2/requerimientos.md`: ⚠7 (un inicializador por
    declarador y la lista de declaradores), la fila de `NodoVarLocalClasica`
    en la tabla de nodos, la nota del logro *For* ("al pie de la letra":
    ahora `int i = 0` vale), la nota de la spec 0007 sobre las locales
    clásicas como contexto con tipo esperado, una sección "Decisiones de la
    spec 0009", y sacar el logro de la lista de logros con sintaxis pero
    sin chequeo;
  - `etapa4-semantico2/casos-de-prueba.md` (convenciones e índice);
  - los comentarios de `NodoVarLocalClasica` y `NodoFor`;
  - el `CLAUDE.md`: lista de logros chequeados, conteo de casos (también los
    de *For*) y la línea de logros con nodos que reportan
    `ERR_NO_SOPORTADO`.
