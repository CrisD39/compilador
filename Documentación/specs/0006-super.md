---
titulo: Logro Super! (Etapa 4)
estado: hecho
origen: entrevista de diseño (grill with docs, 2026-10-27)
fecha: 2026-10-27
---

# Logro *Super!* (Etapa 4)

## Problem Statement

El logro *Super!* (`etapa4-semantico2/requerimientos.md`) pide que el
compilador permita "utilizar super como en Java para acceder a métodos o
atributos de un ancestro, o para invocar a su constructor". Hoy MiniJava no
tiene `super` en ningún nivel: el léxico lo toma como un identificador
cualquiera, la gramática no tiene ninguna forma que lo use (la sección
"Pendientes de gramática" de `requerimientos.md` sólo reserva los nodos
`NodoSuper` y `NodoLlamadaSuperConstructor`), y el chequeo de sentencias no
tiene reglas para él. Así, un programa como `class B2 extends A1 { void m2()
{ super.m1(); } }` no compila. Además, sin `super(...)` una subclase no
tiene forma de inicializar la parte del objeto que declara su superclase.
El logro tiene sólo un esqueleto de casos (`semIISuperCorrecto01`,
`semIISuperError01`) y no está en `LOGROS_HABILITADOS`.

## Solution

`super` pasa a ser palabra clave y se admite en las dos formas de Java:

- **Acceso con super** (`super.x`, `super.m(...)`): selecciona el miembro
  como se ve desde la **superclase** de la clase actual, pero el objeto
  sigue siendo el actual. `super` se tipa como la superclase tal como está
  escrita en el `extends` (por ejemplo `A<String>`), o como `Object` si no
  hay `extends`. A partir de ahí, el encadenado se chequea igual que si
  `super` fuera una variable de ese tipo: sustitución genérica, miembros
  heredados de los ancestros, visibilidad, asignabilidad. Sólo es válido
  donde `this` lo es.
- **Llamada al constructor de la superclase** (`super(...);`): es una
  sentencia, y sólo puede ser la **primera sentencia** del cuerpo de un
  constructor, como en JDK 21. El constructor se elige por aridad en la
  superclase, como en `new`. Sus argumentos se chequean en el **contexto
  previo a la construcción**, donde todavía no se puede usar el objeto
  actual.

Se mantiene la Parte I §6.3: un constructor que no escribe `super(...)` no
llama a ningún constructor de la superclase. Tampoco se le exige a la
superclase un constructor sin parámetros.

La regla de la primera sentencia es la de JDK 21 y no la de JDK 25 (JEP 513,
*flexible constructor bodies*, que admite sentencias antes de `super(...)`).
El compilador usa JDK 21 y la cátedra describe el lenguaje con esa versión.
La alternativa se descartó porque complicaba la gramática y el análisis sin
que el logro la pida (ADR 0010).

## User Stories

1. Como estudiante que escribe MiniJava, quiero usar `super.m()` en un método de una subclase, para llamar a la versión del método de la superclase.
2. Como estudiante, quiero que `super.m()` llame a la versión de la superclase aunque mi clase redefina `m`, para extender un método heredado sin repetirlo.
3. Como estudiante, quiero leer un atributo heredado con `super.x`, para dejar explícito que lo declara un ancestro.
4. Como estudiante, quiero asignar `super.x = 3;`, porque `super.x` es un destino asignable, igual que `this.x`.
5. Como estudiante, quiero encadenar después de un acceso con super (`super.get().y`, `super.v[0]`), igual que después de `this`.
6. Como estudiante, quiero usar con `super` un miembro que la superclase hereda de un abuelo, porque la superclase lo tiene.
7. Como estudiante, quiero que, en `class B extends A<String>`, `super.dato` y `super.get()` tengan el tipo instanciado (`String`) cuando `A<T>` los declara con `T`.
8. Como estudiante, quiero que en una clase sin `extends` valga `super.toString()`, porque su superclase es `Object`.
9. Como estudiante, quiero que `super.equals(o)` sea un error de método inexistente, porque el `Object` de MiniJava sólo tiene `debugPrint` y `toString`.
10. Como estudiante, quiero que `super.m()` sea un error sobre `m` cuando `m` sólo lo declara mi clase, porque la superclase no lo tiene.
11. Como estudiante, quiero que `super.m()` sea un error cuando `m` sólo viene de la interfaz que implemento, porque una interfaz implementada nunca es la superclase.
12. Como estudiante, quiero que `super.noExiste()` dé el error sobre `noExiste`, igual que cualquier método inexistente.
13. Como estudiante, quiero que `super.x` sea un error de visibilidad cuando `x` es private en la superclase, porque un private sólo se usa en su clase.
14. Como estudiante, quiero llamar a un método estático con `super.mEstatico()` desde un método de instancia, igual que con cualquier receptor.
15. Como estudiante, quiero que `super.x` o `super.m()` en un método estático sea un error sobre `super` ("no se puede acceder a super desde un contexto estático"), igual que `this`.
16. Como estudiante, quiero usar `super.b` en el inicializador de un atributo, porque es un contexto de instancia.
17. Como estudiante, quiero que `super.b` en un inicializador no cuente como referencia adelantada, porque no es un nombre suelto y `b` lo declara otra clase.
18. Como estudiante, quiero que un acceso con super mal tipado (`int i = super.nombre;` con `nombre` de tipo `String`) dé el mismo error que con cualquier receptor.
19. Como estudiante, quiero escribir `super(...);` como primera sentencia de un constructor, para inicializar la parte del objeto que declara la superclase.
20. Como estudiante, quiero que `super(...)` elija el constructor de la superclase por aridad, igual que `new`.
21. Como estudiante, quiero que `super()` valga cuando la superclase no declara constructores, porque tiene el constructor por defecto.
22. Como estudiante, quiero que `super()` valga en una clase sin `extends`, porque `Object` tiene constructor por defecto, y que `super(1)` ahí sea un error.
23. Como estudiante, quiero que `super(1)` sea un error sobre `super` cuando la superclase no tiene un constructor de aridad 1.
24. Como estudiante, quiero que un argumento de `super(...)` que no conforma sea un error sobre `super`, igual que en `new`, donde va sobre el nombre de la clase.
25. Como estudiante, quiero que, en `class B extends A<String>`, el constructor `A(T t)` se vea como `A(String t)` al llamarlo con `super(...)`.
26. Como estudiante, quiero que `super()` sea un error de visibilidad cuando elige un constructor private de la superclase.
27. Como estudiante, quiero poder escribir un constructor sin `super(...)` aunque la superclase sólo tenga constructores con parámetros, porque MiniJava no agrega la llamada implícita (§6.3).
28. Como estudiante, quiero que `super(...)` en cualquier lugar que no sea la primera sentencia de un constructor sea un error sobre `super`: en un método, después de otra sentencia, dentro de un bloque anidado, dentro de un `if` o como segunda llamada.
29. Como estudiante, quiero que `super(...)` en un método estático dé el error de ubicación y no el de contexto estático, porque está fuera de lugar.
30. Como estudiante, quiero que `x = super(1);` y `super(1).m();` sean errores sintácticos, porque `super(...)` es una sentencia y no produce un valor.
31. Como estudiante, quiero que `super` suelto (`super;`, `x = super;`) sea un error sintáctico, porque `super` no es un valor.
32. Como estudiante, quiero usar en los argumentos de `super(...)` los parámetros del constructor, literales, llamadas estáticas, `new` de cualquier clase y miembros de otros objetos (`p.x`, `p.m()`).
33. Como estudiante, quiero que `this` en los argumentos de `super(...)` sea un error sobre `this`, porque el objeto todavía no está construido.
34. Como estudiante, quiero que un atributo propio o heredado por su nombre suelto en los argumentos de `super(...)` sea un error sobre su nombre.
35. Como estudiante, quiero que un parámetro del constructor con el mismo nombre que un atributo valga en los argumentos de `super(...)`, porque el nombre resuelve al parámetro.
36. Como estudiante, quiero que una llamada sin receptor a un método de instancia en los argumentos de `super(...)` sea un error sobre el nombre del método, y que a un método estático valga.
37. Como estudiante, quiero que un acceso con super en los argumentos de `super(...)` sea un error sobre `super`.
38. Como estudiante, quiero que esas prohibiciones valgan aunque el uso esté entre paréntesis o anidado (`super(new C(this))`, `super((x))`).
39. Como estudiante, quiero que una sentencia inalcanzable que empieza con `super` se marque sobre `super`, como las demás sentencias.
40. Como estudiante, quiero ver todos los errores de `super` de una corrida mezclados con los demás, en el orden del programa (*Multi-detección*).
41. Como corrector de la cátedra, quiero que el logro funcione con el jar tal como se entrega, sin opciones, para poder probarlo directamente.
42. Como desarrollador del compilador, quiero que el logro esté en `LOGROS_HABILITADOS`, para que `./test.sh` y el hook pre-commit lo protejan.
43. Como desarrollador de la Etapa 5, quiero que el AST guarde que el receptor de una llamada fue `super`, para generar un despacho estático y no virtual.

## Implementation Decisions

**D1 · Alcance.** El logro está siempre activo en `ModuloPrincipalET4`. El
léxico y el parser los comparten todas las etapas, así que
`ModuloPrincipal`, `ET2` y `ET3` también reconocen `super` como palabra
clave y aceptan la sintaxis nueva. `ET3` no chequea cuerpos, así que no
cambia su comportamiento.

**D2 · Léxico.** `super` es la palabra clave `PR_SUPER` en la tabla de
palabras clave, junto a `this`. Ningún caso existente lo usa como
identificador.

**D3 · Gramática.** `super` se agrega a los FIRST de `<Operando>`,
`<Expresion>` y `<Sentencia>`. Son dos formas:

- *Expresión* (`<Operando>`): `super` seguido de un primer eslabón
  obligatorio que tiene que ser `. idMetVar` (atributo) o
  `. idMetVar <ArgsActuales>` (método). Después sigue el
  `<ReferenciaResto>` normal, como con `this`. `super` suelto, `super[0]` y
  `super(...)` dentro de una expresión son errores sintácticos sobre el
  token que sigue a `super`.
- *Sentencia* (`<Sentencia>`): `super` se intercepta antes de la rama de
  expresión, como `idClase`, y el token siguiente decide. Si es `(`, es la
  llamada al constructor de la superclase, `super <ArgsActuales> ;`, y
  `super(1).m();` es error sintáctico sobre `.`. Si es `.`, se arma el
  acceso con super y la sentencia continúa con la cola normal de una
  expresión (`super.x = 3;`, `super.m().n();`), reusando el mismo armado que
  la rama de `(` cuando parte de un operando ya construido.

Las producciones se documentan en la gramática de Etapa 2 y en "Mapeo no
terminal → nodo" de `requerimientos.md`. La fila de *Super!* sale de
"Pendientes de gramática".

**D4 · Nodos.** Son los dos nodos reservados:

- `NodoSuper` (un `NodoPrimario`) guarda el token `super` y siempre tiene
  encadenado (D3). Es el receptor de la llamada en el AST, así que la
  Etapa 5 puede distinguirlo de `this` (D12).
- `NodoLlamadaSuperConstructor` (un `NodoSentencia`) guarda el token
  `super` y la lista de argumentos. En el flujo termina normalmente, como
  una sentencia expresión, y su primer token (para la sentencia
  inalcanzable) es `super`.

**D5 · Tipo de super.** `Clase` expone el **tipo de la superclase**: el
`Tipo` de su `extends` tal como se escribió (con su argumento), o el tipo de
`Object` si no tiene `extends`. Cuando corre el chequeo de sentencias no
hubo errores de declaración, así que ese `extends` existe, es una clase y
no está en un ciclo. Una clase con `implements` no tiene `extends` (ADR
0005), así que su superclase es `Object`.

**D6 · Acceso con super.** `NodoSuper.chequear`, en este orden:

1. En un contexto estático (`tabla.enContextoEstatico()`), lanza
   `ERR_SUPER_EN_ESTATICO` sobre `super`.
2. En el contexto previo a la construcción (D9), lanza
   `ERR_REFERENCIA_ANTES_DE_SUPER` sobre `super`.
3. Devuelve `chequearEncadenado(tabla, tipoDeLaSuperclase)`.

El encadenado es el de siempre (`NodoVarEncadenada`,
`NodoLlamadaEncadenada`, `NodoAccesoArregloEncadenado`), así que, sin
código nuevo:

- se buscan los miembros en la superclase, que ya tiene copiados los de sus
  ancestros;
- se instancian desde el tipo del receptor (`tabla.instanciacionDe`);
- un miembro que sólo declara la clase actual o una interfaz da
  `ERR_ATRIBUTO_NO_ENCONTRADO` o `ERR_METODO_NO_ENCONTRADO` sobre su
  nombre;
- un private de la superclase da `ERR_ACCESO_PRIVADO`, porque la clase
  actual no es la declarante;
- un método estático se llama igual que desde cualquier receptor;
- `super.x` es asignable porque su último eslabón es un atributo.

Como `super.b` es un `NodoVarEncadenada` y no un `NodoAccesoVar`, nunca es
referencia adelantada (spec 0005, D6). En un inicializador de atributo vale,
porque todo atributo es de instancia y su inicializador no es contexto
estático (spec 0005, D4). `static int s = super.b;` no llega a este chequeo,
porque MiniJava no tiene atributos `static` (D11).

**D7 · Ubicación de `super(...)`.** La decide el AST, no un estado de la
tabla. El constructor marca como **en lugar** la sentencia que está primera
en su cuerpo, si es una `NodoLlamadaSuperConstructor`. Que esté "primera" es
literal: si el cuerpo empieza con `;`, `{ super(); }` o un `if`, no hay
ninguna en lugar. Al chequearse, una `NodoLlamadaSuperConstructor` que no
está en lugar lanza `ERR_SUPER_FUERA_DE_LUGAR` sobre `super`, antes de mirar
los argumentos. Eso cubre el método (estático o no), una segunda llamada,
una dentro de un bloque anidado o de un `if`, y una después de otra
sentencia. En un método estático gana este error, no `ERR_SUPER_EN_ESTATICO`.
Un inicializador de atributo es una expresión, así que no puede contener la
sentencia.

**D8 · Resolución del constructor.** Una llamada en lugar hace lo mismo que
`NodoCreacionObjeto`, sobre la clase de la superclase y con el token
`super`:

1. Busca un constructor de esa aridad entre los de la superclase. Si no lo
   hay, lanza `ERR_CONSTRUCTOR_NO_ENCONTRADO`. Como en `new`, el
   constructor por defecto existe sólo si la clase no declaró ninguno, y
   `Object` tiene sólo el sin parámetros (ADR 0004).
2. Controla la visibilidad: un constructor private da `ERR_ACCESO_PRIVADO`.
3. Chequea los argumentos en el contexto previo a la construcción (D9).
4. Exige su conformidad con los parámetros, vistos desde el tipo de la
   superclase (`tabla.instanciacionDe`). Si no conforman, lanza
   `ERR_ARGUMENTO_INCOMPATIBLE`.

Todos esos errores van sobre `super`, que cumple el papel del nombre de la
clase en `new`. La descripción de los tres códigos se arma con el lexema, y
el texto se ajusta para que se lea bien cuando ese lexema es `super`, por
ejemplo nombrando la superclase. La descripción no es parte del contrato,
la etiqueta sí.

> **Nota (ticket 08):** al implementarlo, la variante ya no se elige por el
> lexema sino por el campo `detalle` de `ErrorSemantico`, que
> `ChequeoLlamada.chequearConstructor()` pone en
> `ErrorSemantico.DETALLE_SUPER` para `super(...)`: otro camino podría
> reportar esos códigos sobre un token `super`. La etiqueta no cambia.

**D9 · Contexto previo a la construcción.** `TablaSimbolos` lleva una marca
que `NodoLlamadaSuperConstructor` prende mientras chequea sus argumentos y
apaga al terminar, aunque salga por una excepción. Con la marca prendida,
usar el objeto actual es `ERR_REFERENCIA_ANTES_DE_SUPER`, sobre el token
del uso:

| Uso | Dónde se controla | Lexema |
|---|---|---|
| `this` | `NodoThis`, junto al control de contexto estático | `this` |
| acceso con super | `NodoSuper` (D6, paso 2) | `super` |
| atributo (propio o heredado) por su nombre suelto | `NodoAccesoVar`, en el lugar del control de contexto estático: después de resolver el nombre y su visibilidad | el nombre |
| llamada sin receptor a un método de instancia | `NodoLlamadaMetodo`, en el lugar del control de contexto estático | el nombre del método |

Valen los parámetros del constructor (resuelven antes que los atributos),
los literales, las llamadas estáticas con o sin receptor, `new` de cualquier
clase (la actual incluida) y los miembros de otros objetos (`p.x`, `p.m()`).
La marca cubre todo lo que está adentro de los argumentos, entre paréntesis
o anidado (`super(new C(this))`). El primer error corta la sentencia
(*Multi-detección*: el `NodoBloque` lo registra y sigue con la siguiente).

**D10 · Interacción con otros logros.**

- *Todos los caminos llevan al retorno*: una sentencia inalcanzable que
  empieza con `super` se marca sobre `super`. Igual que con cualquier
  sentencia, después se chequea, así que un `super(...)` después de un
  `return` da también `ERR_SUPER_FUERA_DE_LUGAR`.
- *Visibilidad chequeada*: la nota de `requerimientos.md` "un constructor
  private no molesta a una subclase, porque MiniJava no tiene la llamada
  implícita a `super()`" se reescribe. Sigue sin molestar si la subclase no
  escribe `super(...)`, y es error si lo llama.
- Una lambda en los argumentos sigue dando `ERR_NO_SOPORTADO` (spec 0003,
  D11).

**D11 · Invariantes, sin regla ni casos.** Estos casos no se pueden dar en
MiniJava, así que la spec los documenta y no hay reglas ni casos de prueba
para ellos:

- Un método abstracto alcanzado con `super.m()`: no hay `abstract`, una
  interfaz nunca es la superclase, y el contrato de interfaz exige métodos
  concretos.
- Asignar con `super.x = ...` un atributo `final`: el parser rechaza
  `final` en atributos.
- Usar `super` en el inicializador de un atributo `static`: la gramática
  sólo admite `static` en métodos (§6.1), así que `static int s = super.b;`
  es un error sintáctico y nunca llega a `ERR_SUPER_EN_ESTATICO`.
- Usar `super` en un `class Object` del programa: redeclarar un tipo
  predefinido da `ERR_CLASE_DUPLICADA` (ADR 0004), y después de un error de
  declaración no corre el chequeo de sentencias.

**D12 · Nota para la Etapa 5.** Un método de instancia llamado con
`super.m()` se despacha de forma estática, a la versión de la superclase, y
no por despacho dinámico. `super.mEstatico()` se compila como una llamada
estática a la superclase. Esta spec sólo garantiza que el AST guarda el
`NodoSuper` como receptor.

**D13 · Contrato de salida.** No cambia: `Error Semántico en línea <L>:
<descripción>` y después `[Error:<lexema>|<línea>]`. Los errores
sintácticos tienen el formato de siempre.

### Códigos de error

| Código | Lexema | Descripción |
|---|---|---|
| `ERR_SUPER_EN_ESTATICO` (nuevo) | `super` | no se puede acceder a super desde un contexto estático |
| `ERR_SUPER_FUERA_DE_LUGAR` (nuevo) | `super` | la llamada al constructor de la superclase debe ser la primera sentencia de un constructor |
| `ERR_REFERENCIA_ANTES_DE_SUPER` (nuevo) | el token del uso (`this`, `super`, el atributo o el método) | no se puede usar el objeto actual antes de llamar al constructor de la superclase |
| `ERR_CONSTRUCTOR_NO_ENCONTRADO` (existe) | `super` | (texto ajustado, D8) |
| `ERR_ARGUMENTO_INCOMPATIBLE` (existe) | `super` | (texto ajustado, D8) |
| `ERR_ACCESO_PRIVADO` (existe) | `super` en `super(...)`; el miembro en un acceso con super | (texto ajustado, D8) |
| `ERR_ATRIBUTO_NO_ENCONTRADO`, `ERR_METODO_NO_ENCONTRADO` (existen) | el nombre del miembro | sin cambios |

Las filas nuevas y los lexemas nuevos de las filas existentes se agregan a
la tabla de convenciones de `etapa4-semantico2/casos-de-prueba.md`, y
`super` se suma a la lista de primeros tokens de la fila de sentencia
inalcanzable.

## Testing Decisions

Se observa sólo la salida del compilador (las etiquetas `[Error:…]`), como
en las specs 0001–0005. No hay tests unitarios. El único punto de prueba es
el que ya existe: `TesterLogrosSem2` sobre
`resources/semantico2/logros/superKeyword/`, que exige **exactamente** las
etiquetas `///[...]` iniciales, en orden. Los errores sintácticos del logro
también van ahí, porque `ModuloPrincipalET4` los imprime con la misma
etiqueta (antecedente: `semIIInstanceOfError13`–`15`). Cada caso sintáctico
tiene un solo error, para que el modo pánico no agregue etiquetas.

- **Habilitar el logro:** `superKeyword` se agrega a `LOGROS_HABILITADOS`.
- **Base y Etapas 1–3:** ningún caso usa `super` como identificador, así que
  no cambia ninguna etiqueta. `semIICorrecto22` y
  `semIIVisibilidadCorrecto04`, que dependen de §6.3, siguen sin errores.
- **Casos correctos** (`semIISuperCorrectoNN`):

  | # | Qué prueba |
  |---|---|
  | 01 | *(existe)* `super.m1()` |
  | 02 | Acceso: `super.x` leído y asignado, `super.get().y`, un miembro del abuelo, un método redefinido llamado con `super`, `super.mEstatico()` en un método de instancia y `super.toString()` en una clase sin `extends` |
  | 03 | Genéricos: en `class B extends A<String>`, `String s = super.dato;`, `String t = super.get();` y `super("hola")` con el constructor `A(T t)` |
  | 04 | Llamada en lugar: `super()` sin `extends` (`Object`), `super(1, true)` eligiendo por aridad entre constructores sobrecargados, `super()` con una superclase sin constructores declarados, y un constructor sin `super(...)` cuya superclase sólo tiene constructores con parámetros |
  | 05 | Lo que vale antes de la construcción: parámetros (uno con el nombre de un atributo), literales, `A.m()`, `m()` estático propio, `new` de la clase actual, `p.x` y `p.m()` |
  | 06 | Contextos de instancia: `super.b` en un inicializador de atributo (no es referencia adelantada) y un acceso con super en un constructor, después de `super()` y en otro sin `super()` |

- **Casos con error** (`semIISuperErrorNN`; `L` es la línea marcada):

  | # | Qué prueba | Etiquetas, en orden |
  |---|---|---|
  | 01 | *(existe)* `super.noExiste()` | `noExiste` |
  | 02 | Atributo que sólo declara la clase actual: `super.x` | `x` |
  | 03 | Método que sólo viene de la interfaz implementada: `super.m()` | `m` |
  | 04 | Private de la superclase, en dos sentencias: `super.secreto` y `super.privM()` | `secreto`, `privM` |
  | 05 | En un método estático, en dos sentencias: `super.x` y `super.m()` | `super`, `super` |
  | 06 | Tipos: `int i = super.nombre;` con `String nombre`, y en `B extends A<String>`, `int j = super.dato;` | `=`, `=` |
  | 07 | Sintaxis: `x = super(1);` | `(` |
  | 08 | Sintaxis: `super;` | `;` |
  | 09 | Sintaxis: `super(1).m();` | `.` |
  | 10 | Ubicación: `super()` en un método de instancia | `super` |
  | 11 | Ubicación, en dos constructores: `{ ; super(); }` y `{ super(); super(); }` | `super`, `super\|L` (la segunda llamada) |
  | 12 | Ubicación, en dos constructores: `{ { super(); } }` y `{ if (c) super(); }` | `super`, `super` |
  | 13 | Ubicación en un método estático: `super()` (no es error de contexto estático) | `super` |
  | 14 | Aridad, en dos clases: `super(1)` con la superclase con sólo `A()`, y `super(1)` sin `extends` | `super`, `super` |
  | 15 | Argumento incompatible, en dos clases: `super(true)` con `A(int)`, y `super(5)` en `B extends A<String>` con `A(T t)` | `super`, `super` |
  | 16 | Constructor private de la superclase: `super()` | `super` |
  | 17 | Antes de la construcción, en dos constructores: `super(this)` y `super(x)` con `x` heredado | `this`, `x` |
  | 18 | Antes de la construcción, en tres constructores: `super(m())` con `m` de instancia, `super(super.x)` y `super(new C(this))` | `m`, `super`, `this` |
  | 19 | Sentencia inalcanzable: `super.m();` después de un `return;` en un método | `super` |
  | 20 | Multi-detección en un método: `super.noExiste();`, `super.x = true;` (con `int x`) y `this.y = 1 + true;` | `noExiste`, `=`, `+` |

  Las líneas se fijan al escribir cada archivo. Un caso con error que
  dependa de otro logro no implementado no vale.

- **Corrida completa:** `./test.sh` en verde al final de cada ticket.
- **Antecedentes:** los logros *Instanceof* (palabra clave nueva, gramática
  y casos sintácticos en `logros/`), *Visibilidad chequeada* y *Atributos
  inicializados* (mismo tester y forma de habilitarlos).

## Out of Scope

- Sentencias antes de `super(...)` (JEP 513, JDK 25).
- La llamada implícita a `super()` y la exigencia de un constructor sin
  parámetros en la superclase (§6.3 se mantiene).
- `this(...)` para encadenar constructores de la misma clase: el logro no
  lo pide.
- `Interfaz.super.m()` (super calificado) y las referencias a método
  (`super::m`).
- `super` dentro de una lambda: la lambda sigue sin soportarse.
- Generar código para el despacho estático de `super.m()`: es de la Etapa 5
  (D12).
- Las reglas para `abstract`, atributos `final` y un `Object` del programa:
  son invariantes (D11).

## Further Notes

- Referencias: JLS §8.8.7 (cuerpo del constructor y llamada explícita),
  §8.8.7.1 (contexto previo a la construcción, en JDK 21 "static context"
  de los argumentos), §15.11.2 (acceso con `super`) y §15.12.4.4 (llamada
  con `super` sin despacho dinámico). javac reporta "call to super must be
  first statement in constructor" y "cannot reference this before
  supertype constructor has been called".
- Vocabulario: `GLOSARIO.md` (*superclase*, *acceso con super*, *llamada al
  constructor de la superclase*, *contexto previo a la construcción*).
- **ADR 0010** (`adr/0010-super-constructor-primera-sentencia-jdk21.md`,
  escrito en el ticket 01): `super(...)` es una sentencia (y no una
  expresión) y va primera, como en JDK 21 y no como en JDK 25. Cumple los
  tres criterios: es difícil de revertir (gramática y casos), sorprende
  frente al "como en Java" y hubo una alternativa real.
- Al cerrar hay que actualizar:
  - en `etapa4-semantico2/requerimientos.md`: una sección "Decisiones de la
    spec 0006", el "Estado de la implementación" (sacar el logro de los
    pendientes y la línea "*Super!*: la gramática todavía no lo tiene"), la
    nota de visibilidad (D10), "Pendientes de gramática" y el árbol de
    herencia (sacar los `[pendiente]`);
  - la gramática de `etapa2-sintactico/analizador-sintactico.md` (D3);
  - `casos-de-prueba.md` (convenciones e índice);
  - el `CLAUDE.md` (lista de logros chequeados y conteo de casos);
  - el índice de `Documentación/README.md`.

## Tickets

En `0006-super/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Acceso con super | — | hecho |
| 02 | Llamada al constructor de la superclase | 01 | hecho |
| 03 | Contexto previo a la construcción | 02 | hecho |
| 04 | Cierre | 03 | hecho |
| 05 | Miembros heredados de una superclase genérica sin argumento | — | hecho |
| 06 | Resolver la superclase una sola vez | — | hecho |
| 07 | Contexto previo a la construcción en un solo lugar | 06 | hecho |
| 08 | Descripciones de super(...) con detalle | 07 | hecho |
| 09 | Documentación de la revisión | 08 | hecho |

Reparto de casos: 01 → Correcto 01 y 02, Error 01–08, 19 y 20; 02 →
Correcto 03 y 04, Error 09–16; 03 → Correcto 05 y 06, Error 17 y 18.

**Cierre (2026-10-27):** `./test.sh` en verde. Etapas 1–3 sin cambios
(léxico 14 + 24, sintáctico 38 + 63 + 4 de modo pánico, semántico 37 + 104);
Sem2 con 92 casos sin error, 171 con error y 154 de logros (26 de este
logro: 6 correctos y 20 con error). La base no cambió.

Los tickets 05–09 salen de la revisión (`0006-super/revision.md`).

**Cierre de la revisión (2026-10-27):** `./test.sh` en verde. Etapas 1–3
sin cambios (léxico 14 + 24, sintáctico 38 + 63 + 4 de modo pánico,
semántico 37 + 104); Sem2 con 93 casos sin error, 172 con error y 156 de
logros (28 de este logro: 7 correctos y 21 con error). Los casos nuevos son
los de 05; 06–09 no cambian ninguna salida.
