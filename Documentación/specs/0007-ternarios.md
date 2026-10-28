---
titulo: Logro Ternarios (Etapa 4)
estado: ready-for-agent
origen: entrevista de diseño (grill with docs, 2026-10-28)
fecha: 2026-10-28
---

# Logro *Ternarios* (Etapa 4)

## Problem Statement

El logro *Ternarios* (`etapa4-semantico2/requerimientos.md`) pide que "el
compilador controla que las expresiones ternarias son correctamente
tipadas". Hoy el parser ya reconoce `c ? e1 : e2` y arma un
`NodoExpTernaria`, pero el chequeo de sentencias lo rechaza siempre con
`ERR_NO_SOPORTADO` sobre el `?` (spec 0003, D11). Así, un programa tan
común como `x = c ? 1 : 2;` no compila. El logro tiene sólo un esqueleto de
casos (`semIITernarioCorrecto01`, `semIITernarioError01`) y no está en
`LOGROS_HABILITADOS`.

Además, hasta ahora el chequeo de expresiones va sólo de abajo hacia
arriba: cada nodo devuelve su tipo sin saber dónde se usa. Con eso, el
ternario tendría que inventarse un tipo propio, y `A1 a; a = c ? new B2() :
new C2();` (dos subclases hermanas de `A1`) sería error, aunque en Java es
válido porque el ternario de referencias se tipa contra su destino (JLS
§15.25.3).

## Solution

El ternario se chequea con un esquema **híbrido** (ADR 0011):

- La **condición** tiene que ser `boolean`.
- **Ninguna rama puede ser void**, aunque lo sean las dos.
- **Con tipo esperado** (el destino de `=`, el atributo que se inicializa,
  el retorno de un método no void, el parámetro formal que recibe un
  argumento), cada rama tiene que conformar con ese tipo, como en Java. El
  tipo esperado baja a través de paréntesis y de ternarios anidados.
- **Sin tipo esperado** (receptor de una cadena, operandos, `==`,
  `instanceof`, condiciones, índices, for-each, `var`), el ternario tiene
  el tipo de la rama con la que la otra conforma. Si ninguna conforma con la
  otra, es error. No se busca un supertipo común, así que dos clases
  hermanas sin tipo esperado son error, a diferencia de Java.

Todos los errores propios del ternario van sobre su `?`. No cambian la
gramática ni el análisis de flujo, y sigue habiendo un error por sentencia
(*Multi-detección*).

## User Stories

1. Como estudiante que escribe MiniJava, quiero escribir `x = c ? 1 : 2;`, para elegir un valor según una condición sin un `if`.
2. Como estudiante, quiero que la condición de un ternario tenga que ser `boolean`, para que `x = 1 ? 2 : 3;` se marque como error, igual que `if (1)`.
3. Como estudiante, quiero que el error de una condición no boolean apunte al `?`, para ubicar enseguida qué ternario está mal.
4. Como estudiante, quiero asignar `a = c ? new B2() : new C2();` a un atributo de tipo `A1` cuando `B2` y `C2` extienden `A1`, porque Java lo acepta.
5. Como estudiante, quiero devolver `return c ? new B2() : new C2();` en un método que retorna `A1`, por la misma razón.
6. Como estudiante, quiero pasar `usar(c ? new B2() : new C2())` a un método con parámetro `A1`, por la misma razón.
7. Como estudiante, quiero inicializar un atributo con un ternario (`A1 a = c ? new B2() : new C2();`), con la misma regla que la asignación.
8. Como estudiante, quiero usar un ternario como argumento de `new` y de `super(...)`, con la misma regla que en una llamada.
9. Como estudiante, quiero que el formal de un método genérico se vea desde el receptor al chequear un ternario como argumento: en `cs.poner(c ? "a" : null)` con `cs` de tipo `Caja<String>`, cada rama se compara con `String`.
10. Como estudiante, quiero que una rama que no conforma con el tipo esperado se marque sobre el `?`, para que todos los errores del ternario salgan en el mismo token.
11. Como estudiante, quiero que en un ternario anidado el error se marque en el `?` del ternario dueño de la rama, para saber cuál de los dos está mal.
12. Como estudiante, quiero que el tipo esperado baje a través de paréntesis (`a = c1 ? (c2 ? new B2() : new C2()) : null;`), porque los paréntesis no cambian el valor.
13. Como estudiante, quiero usar un ternario como receptor (`x = (c ? b2 : a1).getN();`) cuando una rama conforma con la otra, y que tenga el tipo de la más general.
14. Como estudiante, quiero que dos clases hermanas sin tipo esperado (`(c ? new B2() : new C2()).getN()`) se marquen como error, sabiendo que esa es una diferencia con Java.
15. Como estudiante, quiero mezclar `null` con una referencia (clase, interfaz, arreglo o `T`), de cualquiera de los dos lados, y que el ternario tenga el tipo de esa referencia.
16. Como estudiante, quiero que `null` con un primitivo (`x = c ? 1 : null;`) sea error, porque MiniJava no tiene boxing.
17. Como estudiante, quiero que `c ? null : null` tenga el tipo null, para que valga asignarlo a una referencia y que `var v = c ? null : null;` falle como `var v = null;`.
18. Como estudiante, quiero que una rama void (`c ? m() : 1`) sea error con un mensaje propio, porque una llamada void no produce valor.
19. Como estudiante, quiero que `c ? m() : n()` con las dos ramas void también sea error, como en Java.
20. Como estudiante, quiero que `c ? new Caja<String>() : new Caja<Object>()` respete la invarianza de los argumentos genéricos, igual que la asignación.
21. Como estudiante, quiero que un parámetro de tipo `T` siga sin conformar con `Object` dentro de un ternario, por coherencia con `o = t;`.
22. Como estudiante, quiero que un ternario no valga como sentencia suelta (`c ? a : b;`), como en Java.
23. Como estudiante, quiero que un ternario no sea destino de una asignación (`(c ? a : b) = 3;`), como en Java.
24. Como estudiante, quiero que `return c ? 1 : 2;` en un método void siga dando el error de retorno de siempre.
25. Como estudiante, quiero que la condición se chequee antes que las ramas, y la rama izquierda antes que la derecha, para que el error informado sea el primero del fuente.
26. Como estudiante, quiero que un error dentro de una rama (una variable no declarada) se marque en su propio token y no en el `?`.
27. Como estudiante, quiero que en `m(1, c ? x : "a")` con formales `(String, int)` el error informado sea el del `?`, porque los argumentos se tipan antes de compararse con sus formales, como hoy.
28. Como estudiante, quiero que dos sentencias con ternarios mal tipados den dos errores, en orden (*Multi-detección*).
29. Como estudiante, quiero que `while (c ? true : true)` sea un bucle común y no una condición constante, como manda el ADR 0009.
30. Como estudiante, quiero usar un ternario en un índice, en la condición de un `if` o en un `var`, y que ahí se tipe por conformidad entre ramas.
31. Como mantenedor del compilador, quiero que los casos que no usan ternarios den exactamente las mismas etiquetas que antes, para no romper la base ni los otros logros.
32. Como mantenedor, quiero que el tipo esperado pase por un único método con implementación por defecto, para que los nodos que no son ternarios no cambien.
33. Como mantenedor, quiero que el ternario devuelva el tipo esperado después de validar sus ramas, para que el contexto siga con su control de conformidad de siempre y nunca reporte un segundo error.
34. Como mantenedor, quiero que la regla quede escrita en un ADR, para saber por qué el ternario no busca un supertipo común.

## Implementation Decisions

**D1 · Alcance.** El logro está siempre activo en `ModuloPrincipalET4`. La
gramática no cambia (las ramas siguen siendo `<ExpresionCompuesta>`,
anidadas a derecha, sin asignación dentro de una rama), así que
`ModuloPrincipal`, `ET2` y `ET3` no cambian.

**D2 · Tipo esperado.** `NodoExpresion` gana un método,
`chequearConTipoEsperado(tabla, esperado)`, como `chequearComoDestino` (spec
0005, D7). Por defecto devuelve `chequear(tabla)`: no controla la
conformidad, que la sigue haciendo el contexto. Lo redefinen sólo dos
nodos:

- `NodoExpTernaria` (D4).
- `NodoExpParentizada`, que lo delega a la expresión de adentro **sólo si
  no tiene encadenado**. En `(c ? b : a).getN()`, el ternario se chequea sin
  tipo esperado.

**D3 · Contextos con tipo esperado.** Pasan su tipo con
`chequearConTipoEsperado` en lugar de `chequear`, y después siguen con su
control de siempre (mismo código y token):

- `NodoExpAsignacion`: el valor, con el tipo del destino ya chequeado. El
  orden destino → valor no cambia.
- `Atributo.chequearInicializador`: el inicializador, con el tipo del
  atributo.
- `NodoReturn`: la expresión, con el tipo de retorno, sólo en un método no
  void. En un constructor o en un método void se chequea con `chequear`,
  como hoy.
- `ChequeoLlamada.chequearArgumentos`: cada argumento, con su formal ya
  visto desde el receptor (`sustituir(vista)`), en las llamadas a método
  (de instancia, estáticas y encadenadas), en `new` y en `super(...)`. Para
  eso recibe los formales y la vista del método o constructor ya resuelto
  por aridad. Las dos fases se mantienen: primero se tipan todos los
  argumentos (y los errores de rama de un ternario salen ahí, en orden del
  fuente), después `exigirConformidad` da `ERR_ARGUMENTO_INCOMPATIBLE`. Un
  método con parámetros de tipo propios (logro *Métodos genéricos*) tipa
  sus argumentos sin tipo esperado, como hoy, antes de dar
  `ERR_NO_SOPORTADO`. En `super(...)`, el contexto previo a la construcción
  sigue envolviendo los argumentos (spec 0006, D9).

El resto de los lugares que tipan una expresión (for-each, índices y
tamaños de arreglo, operandos, receptor, `instanceof`, condiciones, `var`)
siguen con `chequear`, como en Java, que tampoco los tipa por destino.

**D4 · Chequeo del ternario.** `NodoExpTernaria`, en este orden:

1. **Condición.** Si no es `boolean`, lanza `ERR_CONDICION_NO_BOOLEAN`
   sobre el `?`.
2. **Con tipo esperado** (`chequearConTipoEsperado`), cada rama completa, la
   izquierda y después la derecha: `rama.chequearConTipoEsperado(tabla,
   esperado)`. Si da void, lanza `ERR_TERNARIO_RAMA_VOID`. Si no conforma con
   `esperado`, lanza `ERR_TERNARIO_RAMA_INCOMPATIBLE`. Devuelve `esperado`.
   Así, `a = c ? 1 : m();`, con `a` de tipo `A1`, reporta la rama `1`.
3. **Sin tipo esperado** (`chequear`): tipa la rama izquierda y la derecha.
   Si alguna es void, lanza `ERR_TERNARIO_RAMA_VOID`; el filtro es explícito
   aunque `conformaCon` ya rechace void. Después, si la izquierda conforma
   con la derecha, devuelve el tipo de la derecha. Si no, y la derecha
   conforma con la izquierda, devuelve el de la izquierda. Si no, lanza
   `ERR_TERNARIO_INCOMPATIBLE`.

Todos los errores van sobre el `?`, que es el único token propio que guarda
el nodo. No hace falta guardar el `:`. Las consecuencias sobre null salen
de `conformaCon` sin código especial: `null : null` da el tipo null,
`null` con una referencia (clase, interfaz, arreglo o `T`) da esa
referencia, y `null` con un primitivo es error. `T` sigue sin conformar con
`Object` (`semIIError41-43`).

**D5 · Lo que no cambia.** El ternario no es asignable ni llamable (los
valores por defecto de `NodoExpresion`), así que como sentencia suelta da
`ERR_SENTENCIA_INVALIDA` sobre el `;`, y como destino da
`ERR_DESTINO_NO_ASIGNABLE` sobre el `=`. `valorConstante()` sigue en null:
un ternario nunca es una condición constante (ADR 0009). Su `primerToken()`
sigue siendo el de la condición.

**D6 · Interacción con otros logros.**

- *Atributos inicializados*: un ternario en un inicializador se chequea con
  el tipo del atributo (D3). Una rama con un nombre suelto sigue la regla de
  referencia adelantada sin cambios. Un error del ternario es el único del
  atributo, sin un segundo `ERR_ASIGNACION_INCOMPATIBLE`.
- *Super!*: en `super(...)`, el ternario sigue en el contexto previo a la
  construcción, así que `super(c ? this : null)` da
  `ERR_REFERENCIA_ANTES_DE_SUPER` sobre `this`.
- *Variables locales clásicas* y los inicializadores de arreglo: son
  contextos con tipo esperado en Java, pero sus logros siguen sin
  soportarse. Cuando se implementen, tienen que usar
  `chequearConTipoEsperado` (ADR 0011).
- *Minilambdas*: una lambda como rama sigue dando `ERR_NO_SOPORTADO`.

**D7 · Contrato de salida.** No cambia: `Error Semántico en línea <L>:
<descripción>` y después `[Error:<lexema>|<línea>]`.

### Códigos de error

| Código | Lexema | Descripción |
|---|---|---|
| `ERR_CONDICION_NO_BOOLEAN` (existe) | `?` | la condición de ? tiene que ser boolean |
| `ERR_TERNARIO_RAMA_VOID` (nuevo) | `?` | una rama de ? no produce valor |
| `ERR_TERNARIO_RAMA_INCOMPATIBLE` (nuevo) | `?` | una rama de ? no conforma con el tipo esperado |
| `ERR_TERNARIO_INCOMPATIBLE` (nuevo) | `?` | las ramas de ? no tienen tipos compatibles |

Las filas se agregan a la tabla de convenciones de
`etapa4-semantico2/casos-de-prueba.md`.

## Testing Decisions

Se observa sólo la salida del compilador (las etiquetas `[Error:…]`), como
en las specs 0001–0006. No hay tests unitarios. El único punto de prueba es
el que ya existe: `TesterLogrosSem2` sobre
`resources/semantico2/logros/ternarios/`, que exige **exactamente** las
etiquetas `///[...]` iniciales, en orden.

- **Habilitar el logro:** `ternarios` se agrega a `LOGROS_HABILITADOS`.
- **Base y Etapas 1–3:** ningún caso base usa ternarios en el chequeo de
  sentencias. Los sintácticos que los usan (`sintCorrecto17`–`19`, `38`,
  `sintError41`, `42`) corren en `ET2`, que no cambia.
- **Otros logros:** `logros/atributosInicializados/conErrores/semIIAtribInicError15`
  probaba que un ternario en un inicializador da `ERR_NO_SOPORTADO`. Se
  reescribe como `int a = true ? 1 : "x";`, con la misma etiqueta `[Error:?|7]`
  (ahora `ERR_TERNARIO_RAMA_INCOMPATIBLE`) y sin un segundo error del
  atributo. Se actualiza su comentario.
- **Destinos:** una local con tipo explícito (`A1 a = ...;`) es del logro
  *Variables locales clásicas* y da `ERR_NO_SOPORTADO`. Los casos usan
  atributos o parámetros como destino de `=`, inicializadores de atributo,
  `return` y argumentos.
- **Lexemas repetidos:** dos `?` en la misma línea dan la misma etiqueta.
  En los casos de anidados con error, el ternario que falla va en su propia
  línea.
- **Casos correctos** (`semIITernarioCorrectoNN`):

  | # | Qué prueba |
  |---|---|
  | 01 | *(existe)* ramas `int` iguales asignadas a un atributo `int` |
  | 02 | Hermanas con tipo esperado: `a = c ? new B2() : new C2();` con `a` atributo `A1`, `return` en un método que retorna `A1`, e inicializador de atributo `A1` |
  | 03 | Hermanas como argumento: método de instancia, estático y encadenado con parámetro `A1`, `new D(...)` y `super(...)`, y un formal genérico visto desde el receptor (`cs.poner(c ? "a" : null)` con `Caja<String>`) |
  | 04 | Sin tipo esperado: subclase y superclase en los dos órdenes como receptor (`x = (c ? b2 : a1).getN();`) y en `==`, y en `var` (`var v = c ? new B2() : new A1();` y después `a = v;`), y una interfaz con una clase que la implementa |
  | 05 | null: con una clase, un arreglo y un `T`, de los dos lados, `null : null` asignado a un atributo `A1`, y `var v = c ? null : new A1();` |
  | 06 | Anidados: a derecha (`i = c1 ? 1 : c2 ? 2 : 3;`), a izquierda, con paréntesis y tipo esperado propagado (`a = c1 ? (c2 ? new B2() : new C2()) : null;`) y con condición compuesta (`i = x > 1 && b ? 1 : 2;`) |
  | 07 | Contextos sin tipo esperado: índice (`v[c ? 0 : 1]`), condición de `if` y de `while` (`c ? true : false`) y genéricos iguales (`var k = c ? new Caja<String>() : new Caja<String>();`) |

- **Casos con error** (`semIITernarioErrorNN`):

  | # | Qué prueba | Etiquetas, en orden |
  |---|---|---|
  | 01 | *(existe)* `a1 = true ? 1 : "hola";` con `int a1` | `?` |
  | 02 | Condición `int`: `i = 1 ? 2 : 3;` | `?` |
  | 03 | Rama void con tipo esperado: `i = c ? m() : 1;` | `?` |
  | 04 | Dos ramas void sin tipo esperado: `var v = c ? m() : n();` | `?` |
  | 05 | null con `int`, con tipo esperado: `i = c ? 1 : null;` | `?` |
  | 06 | null con `int`, sin tipo esperado: `var v = c ? 1 : null;` | `?` |
  | 07 | Hermanas como receptor: `i = (c ? new B2() : new C2()).getN();` | `?` |
  | 08 | Hermanas en `==`: `b = (c ? new B2() : new C2()) == null;` | `?` |
  | 09 | Invarianza: `co = c ? new Caja<String>() : null;` con `Caja<Object> co` | `?` |
  | 10 | `int[]` con `int[][]` sin tipo esperado: `var v = c ? a1 : a2;` | `?` |
  | 11 | `char` con tipo esperado `int`: `i = c ? 'a' : 1;` | `?` |
  | 12 | `T` con `Object`: `o = c ? t : new Object();` en `Caja<T>` | `?` |
  | 13 | Rama incompatible en `return`: `return c ? 1 : "a";` en un método `int` | `?` |
  | 14 | Rama incompatible en un argumento, con formal genérico: `cs.poner(c ? 1 : "a")` con `Caja<String>` | `?` |
  | 15 | Rama incompatible en `new D(c ? 1 : "a")` con `D(int)` | `?` |
  | 16 | Rama incompatible en `super(c ? 1 : "a")` con `A(int)` | `?` |
  | 17 | `return c ? 1 : 2;` en un método void | `return` |
  | 18 | `var v = c ? null : null;` | `v` |
  | 19 | Dos sentencias: `c ? a : b;` (ramas `int`; con ramas void sale antes `ERR_TERNARIO_RAMA_VOID`) y `(c ? a : b) = 3;` | `;`, `=` |
  | 20 | Orden, dos sentencias: `i = 1 ? x : "a";` (la condición gana) e `i = c ? noExiste : 1;` | `?`, `noExiste` |
  | 21 | Anidado con tipo esperado propagado, con el ternario interno en otra línea y la rama `5` contra `A1` | `?\|L` (el interno) |
  | 22 | Orden en una llamada: `m(1, c ? x : "a")` con formales `(String, int)` | `?` |
  | 23 | *Multi-detección*: dos sentencias con ternarios mal tipados | `?`, `?` |

  Las líneas se fijan al escribir cada archivo. Un caso con error que
  dependa de otro logro no implementado no vale.

- **Corrida completa:** `./test.sh` en verde al final de cada ticket.
- **Antecedentes:** los logros *Atributos inicializados* (método aparte del
  chequeo de expresiones, `chequearComoDestino`), *Instanceof* y *Super!*
  (mismo tester y forma de habilitarlos).

## Out of Scope

- Supertipo común (lub) de las ramas sin tipo esperado, y tipos
  intersección.
- Boxing y promoción numérica (`c ? 1 : null` como `Integer`,
  `c ? 'a' : 1` como `int`).
- Tipo esperado en locales clásicas y en inicializadores de arreglo: sus
  logros siguen sin soportarse (D6).
- Lambdas como ramas (*Minilambdas*).
- Evaluar la condición: `true ? 1 : "a"` es error aunque la rama derecha
  nunca se ejecute (ADR 0009, no se calculan valores).
- Generar código para el ternario: es de la Etapa 5.

## Further Notes

- Referencias: JLS §15.25 (operador condicional), §15.25.3 (ternario de
  referencias como *poly expression* en contextos de asignación e
  invocación) y §5.2, §5.3 (contextos de asignación e invocación). javac
  reporta "incompatible types: bad type in conditional expression" sobre la
  rama.
- Vocabulario: `GLOSARIO.md` (*ternario*, *tipo esperado*).
- **ADR 0011** (`adr/0011-ternario-tipado-por-destino-hibrido.md`, escrito
  en la entrevista): tipado por destino donde hay tipo esperado, y
  conformidad entre ramas sin supertipo común en el resto. La diferencia
  con Java (hermanas sin tipo esperado) conviene confirmarla con la cátedra.
- Al cerrar hay que actualizar:
  - en `etapa4-semantico2/requerimientos.md`: una sección "Decisiones de la
    spec 0007", y sacar *Ternarios* de la lista de logros con sintaxis
    pero sin chequeo (y de la nota de *Atributos inicializados* que lo
    nombra entre la sintaxis de otros logros);
  - `casos-de-prueba.md` (convenciones e índice);
  - el comentario de `NodoExpTernaria`;
  - el `CLAUDE.md` (lista de logros chequeados, conteo de casos y la línea
    de logros con nodos que reportan `ERR_NO_SOPORTADO`).

## Tickets

En `0007-ternarios/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Ternario sin tipo esperado | — | hecho |
| 02 | Tipo esperado en asignación, inicializador y return | 01 | hecho |
| 03 | Tipo esperado en los argumentos | 02 | hecho |
| 04 | Cierre | 03 | ready-for-agent |

Reparto de casos: 01 → Correcto 01, 04, 05 y 07, Error 01–08, 10–12,
17–20 y 23, y la reescritura de `semIIAtribInicError15`; 02 → Correcto 02
y 06, Error 09, 13 y 21; 03 → Correcto 03, Error 14, 15, 16 y 22.

Error 13–16 y 22 ya dan la etiqueta `?` sin tipo esperado (con
`ERR_TERNARIO_INCOMPATIBLE`), porque sus ramas son incompatibles entre sí.
Van en 02 y 03 porque prueban esos contextos. Los que sólo pasan con el
tipo esperado son Correcto 02, 03 y 06 y Error 09 y 21.
