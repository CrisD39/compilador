---
titulo: Logro Todos los caminos llevan al retorno sin código muerto (Etapa 4)
estado: ready-for-agent
origen: entrevista de diseño (grill with docs, 2026-10-26)
fecha: 2026-10-26
---

# Logro *Todos los caminos llevan al retorno sin código muerto* (Etapa 4)

## Problem Statement

El compilador acepta métodos no void a los que les falta un retorno
(`int m() {}`, o `if (b) return 1;` sin `else`) y sentencias que ninguna
ejecución puede alcanzar (`return 1; x = 2;`, o el cuerpo de un
`while (false)`). Eso es lo que pide la base: el enunciado completo (§12.5 y
§13) dice que MiniJava no controla el retorno en todos los caminos ni
rechaza código inalcanzable. El logro *Todos los caminos llevan al retorno
sin código muerto* (`etapa4-semantico2/requerimientos.md`) pide justo lo
contrario, "al igual que en Java". Hoy el logro tiene solo un esqueleto de
casos (`semIIRetornoCorrecto01`, `semIIRetornoError01`) y no está en
`LOGROS_HABILITADOS`.

## Solution

El chequeo de sentencias controla el flujo de ejecución de cada unidad
según JLS §14.22, recortado a las sentencias de MiniJava:

- Un método no void cuyo cuerpo **termina normalmente** es un error de
  **falta de retorno**, sobre el nombre del método.
- Una **sentencia inalcanzable** es un error, sobre su primer token. Igual
  se chequea, como pide §12.5, así que puede dar más errores después del de
  inalcanzable.
- Una condición de bucle que es una **expresión constante** cuenta como en
  Java: `while (true)` y `for (;;)` no terminan, y el cuerpo de un
  `while (false)` es inalcanzable.
- La recuperación copia la de javac: se reporta una sola vez por cada tramo
  muerto, sin errores en cascada, y en combinación con la *Multi-detección*.

El logro queda siempre activo en `ModuloPrincipalET4` y reemplaza la parte
de §12.5 que lo contradice (ADR 0008). Los casos base que se apoyaban en la
regla vieja pasan al logro.

## User Stories

1. Como estudiante que escribe MiniJava, quiero que `int m() {}` sea un error, para enterarme de que el método no devuelve nada.
2. Como estudiante, quiero que `if (b) return 1;` sin `else` sea un error de falta de retorno, para no olvidarme del camino en el que `b` es falso.
3. Como estudiante, quiero que un `if`/`else` con `return` en las dos ramas sea válido, para escribir métodos con varias salidas.
4. Como estudiante, quiero que `if (true) return 1;` sin `else` sea un error de falta de retorno, igual que en Java, porque el `if` no se trata como constante.
5. Como estudiante, quiero que un método `int` cuyo cuerpo termina en `while (true) {}` o en `for (;;) {}` sea válido sin `return`, porque nunca llega al final.
6. Como estudiante, quiero que `while (b) { return 1; }` sin `return` después sea un error, porque el bucle puede no ejecutarse nunca.
7. Como estudiante, quiero que un for-each con `return` solo adentro del cuerpo sea un error de falta de retorno, porque el for-each siempre puede terminar.
8. Como estudiante, quiero que un método void o un constructor sin `return` sea válido, porque no tienen que devolver nada.
9. Como estudiante, quiero que `return 1; x = 2;` marque `x = 2;` como inalcanzable, para encontrar código que nunca se ejecuta.
10. Como estudiante, quiero que la sentencia que viene después de un `if`/`else` que retorna en las dos ramas sea inalcanzable.
11. Como estudiante, quiero que la sentencia que viene después de un `while (true)` o de un `for (;;)` sea inalcanzable.
12. Como estudiante, quiero que el cuerpo de un `while (false)` o de un `for` con condición `false` sea inalcanzable, igual que en Java.
13. Como estudiante, quiero que `if (false) { … }` no sea un error, porque en Java el `if` no se trata como constante.
14. Como estudiante, quiero que condiciones como `!false`, `1 < 2`, `'a' == 'a'` o `"a" == "a"` se traten como constantes, igual que en Java.
15. Como estudiante, quiero que `null == null` y `1 / 0 == 0` no se traten como constantes, igual que en Java.
16. Como estudiante, quiero que una expresión constante se evalúe con las reglas de MiniJava (sin precedencia entre binarios, `int` de 32 bits con desborde), para que el resultado coincida con lo que hace el chequeo de tipos.
17. Como estudiante, quiero ver un solo error por cada tramo inalcanzable y no uno por sentencia, para que la salida no se llene de errores en cascada.
18. Como estudiante, quiero que, si después de un tramo inalcanzable hay otro `return` y más código, ese segundo tramo también se reporte.
19. Como estudiante, quiero que un método con código inalcanzable al final no me dé además un error de falta de retorno, porque sería un error en cascada.
20. Como estudiante, quiero que adentro de una sentencia inalcanzable se sigan buscando sentencias inalcanzables, como hace javac.
21. Como estudiante, quiero que una sentencia inalcanzable se siga chequeando (nombres, tipos y contexto), para ver en una sola corrida los errores que tiene adentro.
22. Como estudiante, quiero que `return; x = 1;` sea un error también en un constructor o en un método void.
23. Como estudiante, quiero que un bloque vacío o una sentencia vacía inalcanzables (`return 1; { }`, `return 1;;`) también se reporten, igual que en javac.
24. Como estudiante, quiero que los errores de flujo salgan junto con los de tipos y en el orden del programa, para leerlos de arriba hacia abajo.
25. Como estudiante, quiero que el error de falta de retorno salga antes que los errores del cuerpo del método, porque está sobre el nombre, que va antes en el programa.
26. Como estudiante, quiero que la etiqueta de una sentencia inalcanzable apunte a algo reconocible de la sentencia (`var`, `return`, `if`, `while`, `for`, `{`, `;` o el primer nombre de una asignación o llamada).
27. Como corrector de la cátedra, quiero que el informe diga que el logro reemplaza la parte de §12.5 que lo contradice, para entender por qué CP-SII-38 da errores.
28. Como corrector, quiero que el logro funcione con el jar tal como se entrega, sin opciones, para poder probarlo directamente.
29. Como desarrollador del compilador, quiero que el análisis de flujo no dependa de los tipos, para que corra aunque la unidad tenga errores de tipos.
30. Como desarrollador, quiero que el logro esté en `LOGROS_HABILITADOS`, para que `./test.sh` y el hook pre-commit lo protejan.
31. Como desarrollador, quiero que los casos base que contradicen el logro se muden al logro, para que los testers base no queden en rojo y nada se pierda.
32. Como desarrollador, quiero que el AST guarde solo los tokens necesarios para ubicar errores (§14), para no meter sintaxis concreta de más.

## Implementation Decisions

**D1 · Alcance y convivencia con la base.** El logro está siempre activo en
la tercera pasada de `ModuloPrincipalET4`, sin opción para apagarlo (ADR
0008). Reemplaza la parte de §12.5 y §13 que dice que no se controla el
retorno ni el código inalcanzable. Se mantiene la otra parte: una sentencia
inalcanzable se chequea igual. `ModuloPrincipalET3` no cambia.

**D2 · A qué aplica.** La falta de retorno aplica a métodos no void. La
sentencia inalcanzable se busca en todas las unidades: métodos (void o no) y
constructores. Los inicializadores de atributos y los cuerpos de lambda son
expresiones, así que no entran. La pasada sigue corriendo solo si no hubo
errores léxicos, sintácticos ni de declaraciones (spec 0003, D3).

**D3 · Estados del flujo.** Hay tres estados, copiados de la `Liveness` de
javac: **vivo**, **muerto** y **recuperando**. Cada sentencia transforma un
estado de entrada (vivo o recuperando, nunca muerto) en un estado de salida.
Las reglas son estructurales: no miran tipos ni la tabla de símbolos.

```
return                 → muerto
if (c) s               → entrada ⊔ salida(s)
if (c) s1 else s2      → salida(s1) ⊔ salida(s2)
while (c) s            → muerto si c es la constante true; si no, entrada
for (…; c; …) s        → igual que while; sin condición cuenta como true
for (T x : e) s        → entrada
{ s1 … sn }            → se recorre en orden: si el estado es muerto antes
                         de si, si es inalcanzable y se sigue con recuperando
resto (var, local
clásica, asignación,
llamada, ;)            → entrada

⊔: vivo si alguno es vivo; si no, recuperando si alguno lo es; si no, muerto
```

El cuerpo de un `while`/`for` cuya condición es la constante `false` es
inalcanzable y se analiza con entrada recuperando. Como MiniJava no tiene
`break`, `continue`, `switch` ni `throw`, el cuerpo de un bucle nunca cambia
la salida del bucle. El `if` no se trata como constante, ni siquiera con
`if (true)` o `if (false)`, igual que en Java. El for-each siempre termina
normalmente. (El esquema salió de la entrevista; los nombres pueden
cambiar al implementar.)

**D4 · Falta de retorno.** Un método no void tiene falta de retorno si su
cuerpo, partiendo de vivo, sale **vivo**. Si sale recuperando no se reporta,
porque sería un error en cascada de una inalcanzable (`int m() { return 1;
x = 2; }` da solo el error de inalcanzable). `Clase.chequearSentencias` lo
calcula **antes** de chequear el cuerpo de la unidad. Así el error, que va
sobre el nombre del método, sale antes que los errores del cuerpo y la
salida queda en orden de líneas.

**D5 · Reporte de la inalcanzable, intercalado.** `NodoBloque` lleva el
estado mientras chequea sus sentencias: si una sentencia llega en estado
muerto, **primero** registra `ERR_SENTENCIA_INALCANZABLE` sobre su primer
token, **después** la chequea como siempre y sigue con recuperando. El error
se registra, no se lanza: no corta la sentencia. `NodoWhile` y `NodoFor` hacen
lo mismo con su cuerpo cuando la condición es la constante `false`, después
de chequear la condición. Esto da el orden de programa sin ordenar nada,
incluido "la sentencia antes que su contenido". La consecuencia, aceptada,
es que si una sentencia corta por un error de tipos antes de llegar a sus
ramas (`if` con condición no boolean), adentro de esas ramas no se buscan
inalcanzables. Es el mismo corte que ya tienen los errores de tipos con
*Multi-detección*. No se hace una pasada de flujo aparte porque `Token` no
guarda la columna, y dos errores de la misma línea no se podrían ordenar.

**D6 · Expresiones constantes.** Siguen a JLS §15.29 en lo que MiniJava
tiene. Son constantes los literales `int`, `boolean`, `char` y `String`,
combinados con los operadores binarios (`|| && == != < > <= >= + - * / %`),
los unarios (`+ - !`) y paréntesis. Cada nodo de expresión puede dar su
valor constante (por defecto, ninguno). Se pliega sobre el AST, así que se
respeta la asociatividad a izquierda sin precedencia de MiniJava
(`1 + 2 * 3 == 9` es `true`). Reglas:

- La aritmética es `int` de 32 bits con desborde: `999999999 * 3 < 0` es `true`
  (un literal tiene como mucho 9 dígitos, REQ-AL-09).
- `==` y `!=` entre `char` comparan los valores; entre `String`, comparan el
  contenido (las constantes `String` se internan, así que Java da lo mismo).
- `null` no es constante.
- Una división o un módulo por cero hacen que la expresión no sea constante,
  igual que en javac (`while (1 / 0 == 0)` es un bucle común).
- Si los operandos no tienen los tipos que pide el operador (una expresión
  mal tipada), la expresión no es constante. El plegado no reporta errores:
  eso es tarea del chequeo de tipos.
- No hay variables constantes: `final` no se aplica a atributos ni a
  locales (en un atributo es error sintáctico).

**D7 · Primer token de una sentencia.** `NodoSentencia` da el token sobre el
que se reporta si es inalcanzable. El AST guarda solo los tokens que
necesita para ubicar errores (§14):

| Sentencia | Token |
|---|---|
| `NodoVarLocal` | `var` |
| `NodoVarLocalClasica` | el token del tipo (`int`, `A1`) |
| `NodoReturn`, `NodoIf`, `NodoWhile`, `NodoFor`, `NodoForEach` | `return`, `if`, `while`, `for` (ya los guardan) |
| `NodoBloque` | `{` (**campo nuevo**) |
| `NodoSentenciaVacia` | `;` (**campo nuevo**) |
| `NodoSentenciaExpresion` | el primer token que ya guarda la expresión, bajando por la izquierda |

En `NodoSentenciaExpresion` el token se deriva sin campos nuevos: el nombre
en una variable o una llamada (`x = 2;` da `x`), `this`, la clase en una
llamada estática (`A1.m();` da `A1`) o en una creación (`new A1().m();` da
`A1`), y una expresión parentizada se atraviesa (`(x).m();` da `x`). El
parser pasa la `{` a `NodoBloque` y el `;` a `NodoSentenciaVacia`.

**D8 · Interacción con logros no implementados.** Una local clásica
inalcanzable da dos errores: primero el de inalcanzable y después su
`ERR_NO_SOPORTADO`, hasta que se implemente el logro de locales clásicas.
Una lambda o un ternario como condición nunca son constantes.

**D9 · Contrato de salida.** No cambia: `Error Semántico en línea <L>:
<descripción>` y después `[Error:<lexema>|<línea>]`.

### Códigos de error nuevos

| Código | Lexema | Descripción |
|---|---|---|
| `ERR_FALTA_RETORNO` | nombre del método | el método no retorna un valor en todos sus caminos |
| `ERR_SENTENCIA_INALCANZABLE` | primer token de la sentencia (D7) | sentencia inalcanzable |

Se agregan a la tabla de convenciones de `etapa4-semantico2/casos-de-prueba.md`.

## Testing Decisions

Se observa solo la salida del compilador (la etiqueta `[Error:…]`), como en
las specs 0001–0003. El único punto de prueba es el que ya existe:
`TesterLogrosSem2` sobre
`resources/semantico2/logros/caminosRetornoSinCodigoMuerto/`, que exige
**exactamente** las etiquetas `///[...]` iniciales y en orden. Los testers
base siguen igual.

- **Habilitar el logro:** se agrega `caminosRetornoSinCodigoMuerto` a
  `LOGROS_HABILITADOS`.
- **Casos correctos** (`semIIRetornoCorrectoNN`):

  | # | Qué prueba |
  |---|---|
  | 01 | *(existe)* `if`/`else` con `return` en las dos ramas |
  | 02 | void y constructores: cuerpo vacío, sin `return`, con `return;` al final y adentro de un `if` |
  | 03 | Bucle infinito sin `return` después en un método `int`: `while (true) {}`, `for (;;) {}` y `for (var i = 0; ; i = i + 1) {}` |
  | 04 | Constantes `true`: `!false`, `(true)`, `1 < 2`, `true && !false`, `'a' == 'a'`, `"a" == "a"`, `1 + 2 * 3 == 9` y `999999999 * 3 < 0` |
  | 05 | Condiciones no constantes, con código alcanzable después: `while (b)`, `while (null == null)`, `while (1 / 0 == 0)`, y `if (true) return 1;` seguido de `return 2;` |
  | 06 | `if (false) { x = 1; }` es válido |
  | 07 | Anidados: `if`/`else` con `if`/`else` adentro, `{ { return 1; } }`, y una rama con `return` y la otra con `while (true) {}` |
  | 08 | Bucles con `return` adentro y `return` después: `while (b) { return 1; } return 2;`, y lo mismo con for-each |
  | 09 | `for (var i = 0; ; i = i + 1)` y `for (var i = 0; i < 3;)` en métodos separados (lo que tenía `semIIForCorrecto04.m2`) |

- **Casos con error** (`semIIRetornoErrorNN`; `L` es la línea de la
  sentencia marcada):

  | # | Qué prueba | Etiquetas, en orden |
  |---|---|---|
  | 01 | *(existe)* `if` sin `else` | `m1` |
  | 02 | Método `int` con cuerpo vacío | `m1` |
  | 03 | `if (true) return 1;` sin `else` | `m1` |
  | 04 | `while (b) { return 1; }` sin `return` después | `m1` |
  | 05 | For-each con `return` solo adentro | `m1` |
  | 06 | `if`/`else` anidados, con una rama interna sin `return` | `m1` |
  | 07 | `return 1; x = 2;` | `x\|L` |
  | 08 | `if`/`else` con `return` en las dos ramas y después `return 3;` | `return\|L` |
  | 09 | `while (true) {} x = 1;` y `for (;;) {} x = 1;`, en dos métodos | `x`, `x` |
  | 10 | Cuerpo inalcanzable: `while (false) { x = 1; }` y `for (var i = 0; false; i = i + 1) x = i;` | `{`, `x` |
  | 11 | Constante no literal: `while (!false) {} return 1;` | `return` |
  | 12 | Recuperación: `return 1; x = 2; y = 3;` (uno) y `return 1; x = 2; return 3; y = 4;` (dos) | `x`, `x`, `y` |
  | 13 | La recuperación tapa la falta de retorno: `int m() { return 1; x = 2; }` | `x` |
  | 14 | Inalcanzable adentro de una inalcanzable: `return 1; if (b) { return 2; z = 3; }` | `if`, `z` |
  | 15 | Constructor y método void: `return; x = 1;` | `x`, `x` |
  | 16 | *(de `semIIError104`)* `return 1; var y = z;` | `var`, `z` (misma línea) |
  | 17 | Primer token (D7), uno por método después de un `return`: bloque, sentencia vacía, `this.m();`, `new A1().m();`, `A1.m();`, `(x).m();`, `if`, `while`, `for` | `{`, `;`, `this`, `A1`, `A1`, `x`, `;`, `if`, `while`, `for` (el segundo `;` es `ERR_SENTENCIA_INVALIDA`: `(x).m();` no es sentencia válida, `semIIError82`) |
  | 18 | Falta de retorno antes de un error del cuerpo (D4): `int m(boolean b) { if (b) return true; }` | `m`, `return` |
  | 19 | *(de `semIICorrecto31`)* sin retorno, parcial e inalcanzable | `sinRetorno`, `parcial`, `var` |
  | 20 | *(de `semIICorrecto72`, CP-SII-38 de la cátedra)* | `sinRetornoDeclarado`, `parcial`, `var` |
  | 21 | Multi-detección entre unidades: un error de tipos en un método, una falta de retorno en otro y una inalcanzable en un constructor | los tres, en orden de programa |

  Las líneas se fijan al escribir cada archivo. Un caso con error que
  depende de otro logro no implementado no vale (por ejemplo, una local
  clásica fuera del caso de D8).

- **Casos que se mudan o se ajustan:**
  - `sinErrores/semIICorrecto31` → se borra de la base; su contenido es
    `semIIRetornoError19`.
  - `sinErrores/semIICorrecto72` (CP-SII-38) → se borra de la base; es
    `semIIRetornoError20`, con un comentario que dice de dónde viene.
  - `conErrores/semIIError104` → se borra de la base; es
    `semIIRetornoError16`, con las dos etiquetas.
  - `logros/for/sinErrores/semIIForCorrecto04` → el segundo `for` de `m2`
    pasa a su propio método, así deja de quedar después de un
    `for (…; ; …)` infinito y sigue probando las cláusulas opcionales.
- **Corrida completa:** `./test.sh` en verde al final de cada ticket. Los
  testers base pasan a tener dos casos sin error menos y uno con error menos.
  Las Etapas 1–3 no cambian.
- **Antecedentes:** los logros *For*, *Instanceof* y *Visibilidad
  chequeada* (mismo tester, carpetas de casos y forma de habilitarlos).

## Out of Scope

- `break`, `continue`, `switch`, `throw` y `try`: MiniJava no los tiene.
- Asignación definida de locales antes de su uso (JLS cap. 16): es otro
  análisis de flujo y no está pedido.
- Literales `float` en las expresiones constantes: entran con el logro
  *Floats y coerción*, que sigue pendiente.
- Una opción para apagar el logro (ADR 0008).
- Reportar inalcanzables adentro de las ramas de una sentencia que cortó
  por un error de tipos (D5).

## Further Notes

- Referencias: JLS §14.22 (alcanzabilidad), §15.29 (expresiones
  constantes) y la `Liveness` de javac (`com.sun.tools.javac.comp.Flow`)
  para la recuperación.
- Vocabulario: `GLOSARIO.md`, sección "Flujo de ejecución" (*termina
  normalmente*, *sentencia inalcanzable*, *expresión constante*, *falta de
  retorno*).
- Al cerrar hay que actualizar: el "Estado de la implementación" de
  `etapa4-semantico2/requerimientos.md`, `casos-de-prueba.md` (códigos,
  índice y la nota "Pendiente de confirmar" sobre §12.5, que se borra), el
  estado del ADR 0008, el `CLAUDE.md` y el índice de `Documentación/README.md`.
- El informe de la Etapa 4 tiene que decir que el logro reemplaza esa parte
  de §12.5 y que CP-SII-38 ya no da `[SinErrores]`.

## Tickets

En `0004-caminos-de-retorno/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Falta de retorno | — | hecho |
| 02 | Sentencia inalcanzable | 01 | ready-for-agent |
| 03 | Expresiones constantes completas | 02 | ready-for-agent |
| 04 | Cierre | 03 | ready-for-agent |

Reparto de casos: 01 → Correcto 01–03 y 07, Error 01–06 y 18, y Error 19 y
20 sin la etiqueta de la inalcanzable; 02 → Correcto 06, 08 y 09, Error
07–10, 12–17 y 21, y completa 19 y 20; 03 → Correcto 04 y 05, Error 11.
