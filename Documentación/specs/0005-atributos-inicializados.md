---
titulo: Logro Atributos inicializados (Etapa 4)
estado: hecho
origen: entrevista de diseño (grill with docs, 2026-10-26)
fecha: 2026-10-26
---

# Logro *Atributos inicializados* (Etapa 4)

## Problem Statement

El parser acepta atributos con inicializador (`int x = 5;`, REQ-AS-011) y
arma su AST, pero el chequeo de sentencias no lo controla: cada atributo
inicializado da `ERR_NO_SOPORTADO` sobre su nombre (spec 0003, D11). Así,
un programa correcto como `class A { int a = 5; }` no compila, y uno
incorrecto como `int a = true;` da un error que no dice qué está mal. El
logro *Atributos inicializados* (`etapa4-semantico2/requerimientos.md`) pide
que el compilador controle que los atributos inicializados están
correctamente tipados, "los controles son los mismos que en Java". Hoy el
logro tiene sólo un esqueleto de casos (`semIIAtribInicCorrecto01`,
`semIIAtribInicError01`) y no está en `LOGROS_HABILITADOS`.

## Solution

El chequeo de sentencias controla el inicializador de cada atributo, en su
clase de origen, igual que Java:

- El tipo del inicializador tiene que **conformar** con el del atributo,
  con las mismas reglas que el lado derecho de una asignación simple. Si no,
  es una asignación incompatible sobre el `=`.
- El inicializador es un **contexto de instancia**: valen `this`, los
  atributos declarados antes o heredados y las llamadas a métodos de
  instancia sin receptor.
- Una **referencia adelantada** es un error: usar por su nombre suelto un
  atributo de la misma clase que no está declarado antes que el que se
  inicializa (el propio atributo incluido). Como en Java, no cuenta si el
  nombre es el destino directo de un `=`, ni los accesos con receptor
  (`this.b`), ni lo que lea un método llamado.

Nada se aparta de Java (JLS §8.3.2 y §8.3.3), así que no hay ADR. Las
decisiones están en "Decisiones de la spec 0005" de
`etapa4-semantico2/requerimientos.md`.

## User Stories

1. Como estudiante que escribe MiniJava, quiero que `int a = 5;` sea válido, para inicializar un atributo al declararlo.
2. Como estudiante, quiero que `int a = true;` sea un error sobre el `=`, igual que una asignación incompatible, para reconocer el mismo error en los dos lugares.
3. Como estudiante, quiero que `int a = null;` sea un error, porque `null` sólo conforma con referencias.
4. Como estudiante, quiero que `A a = null;` sea válido, para dejar una referencia vacía de forma explícita.
5. Como estudiante, quiero que inicializar un atributo con la llamada a un método `void` sea un error, porque no produce valor.
6. Como estudiante, quiero que `A a = new B();` sea válido si `B` extiende a `A`, y que `B b = new A();` sea un error, igual que en una asignación.
7. Como estudiante, quiero que un atributo de tipo interfaz acepte un objeto de una clase que la implementa.
8. Como estudiante, quiero que `Caja<A> c = new Caja<B>();` sea un error aunque `B` extienda a `A`, porque los argumentos genéricos son invariantes.
9. Como estudiante, quiero que un atributo arreglo exija la misma cantidad de dimensiones y el mismo tipo base que su inicializador.
10. Como estudiante, quiero que, en `class Caja<T>`, `T t = null;` sea válido, para inicializar un atributo genérico.
11. Como estudiante, quiero que un error adentro del inicializador (`int a = 1 + true;`) salga sobre su propio token y no se sume además un error de conformidad, para no ver errores en cascada.
12. Como estudiante, quiero que una variable no declarada en un inicializador dé el mismo error que en un método.
13. Como estudiante, quiero usar `this` y `this.x` en un inicializador, porque los atributos son de instancia.
14. Como estudiante, quiero usar en un inicializador un atributo declarado antes en la misma clase.
15. Como estudiante, quiero usar en un inicializador un atributo heredado, aunque la clase padre esté escrita más abajo en el archivo, porque está declarado en otra clase.
16. Como estudiante, quiero que un atributo heredado de un padre genérico tenga el tipo instanciado (`class B extends A<String>` ve el `T dato` de `A` como `String`).
17. Como estudiante, quiero llamar sin receptor a un método de instancia en un inicializador, y que valga aunque el método lea atributos declarados después.
18. Como estudiante, quiero llamar a un método estático (`A.m()`) en un inicializador.
19. Como estudiante, quiero que `int a = b; int b = 1;` sea un error sobre `b`, igual que en Java, para no leer un atributo que todavía no se inicializó.
20. Como estudiante, quiero que `int x = x + 1;` sea un error sobre `x`, porque el atributo se lee en su propia inicialización.
21. Como estudiante, quiero que la referencia adelantada se detecte también con los dos atributos en la misma línea.
22. Como estudiante, quiero que `int a = this.b; int b;` sea válido, porque Java sólo prohíbe el nombre suelto.
23. Como estudiante, quiero que `int a = (b = 3); int b;` sea válido, porque `b` es el destino de la asignación y no se lee, igual que en Java.
24. Como estudiante, quiero que `int a = b++; int b;` sea un error, porque `++` lee `b`, igual que en Java.
25. Como estudiante, quiero que un atributo posterior usado como argumento (`m(b)`), indexado (`b[0]`) o con encadenado (`b.x`) sea una referencia adelantada, porque se lee.
26. Como estudiante, quiero que en `int a = (c = b); int b; int c;` se marque `b` y no `c`, porque sólo `c` es destino.
27. Como estudiante, quiero que en los métodos y constructores se pueda usar cualquier atributo sin importar dónde está declarado, porque la regla es sólo de los inicializadores.
28. Como estudiante, quiero que acceder a un atributo `private` de otra clase en un inicializador sea un error, igual que en un método (logro *Visibilidad chequeada*).
29. Como estudiante, quiero que un ternario, una lambda o un diamante en un inicializador sigan dando "no soportado" sobre su token, sin un segundo error del atributo.
30. Como estudiante, quiero ver todos los atributos mal inicializados de una corrida, mezclados con los errores de métodos y constructores en el orden del programa (*Multi-detección*).
31. Como estudiante, quiero que un atributo mal inicializado no genere errores en los lugares donde se usa, porque su tipo declarado sigue valiendo.
32. Como corrector de la cátedra, quiero que el logro funcione con el jar tal como se entrega, sin opciones, para poder probarlo directamente.
33. Como desarrollador del compilador, quiero que el logro esté en `LOGROS_HABILITADOS`, para que `./test.sh` y el hook pre-commit lo protejan.
34. Como desarrollador, quiero que la referencia adelantada no dependa del orden de un `HashMap` ni de la línea, para que el resultado sea el mismo en cualquier corrida.

## Implementation Decisions

**D1 · Alcance.** El logro está siempre activo en la tercera pasada de
`ModuloPrincipalET4`. Aplica a los atributos **propios** con inicializador
de cada clase. `Clase.chequearSentencias` ya los encola junto con
constructores y métodos, ordenados por línea, setea `atributoActual` (los
otros dos campos de la unidad actual en null) y atrapa la excepción del
inicializador, porque no pasa por un `NodoBloque`. Eso no cambia. Las
interfaces no tienen atributos. `ModuloPrincipalET3` no cambia.

**D2 · Conformidad.** `Atributo.chequearInicializador` deja de lanzar "no
soportado": chequea el inicializador y exige que su tipo conforme con el del
atributo, con la misma conformidad que `NodoExpAsignacion` (la que recibe la
tabla). Si no conforma, lanza `ERR_ASIGNACION_INCOMPATIBLE` sobre el token
`=`. Si una subexpresión lanza su propio error, ése es el único error del
atributo.

**D3 · Token `=`.** `Atributo` guarda el token `=` de su declaración. El
parser ya lo tiene (`operadorAsignacion()` lo devuelve) y hoy lo descarta en
la rama de `<RestoMiembro>` del atributo inicializado. El AST guarda sólo
los tokens que necesita para ubicar errores (§14).

**D4 · Contexto de instancia.** No hay atributos `static` (§6.1, la
gramática sólo admite `static` en métodos), así que `enContextoEstatico()`
sigue dando false con `atributoActual` seteado: `this`, los atributos
implícitos y las llamadas sin receptor a métodos de instancia valen. La
visibilidad se controla como en cualquier cuerpo de la clase. La
resolución de nombres es la de siempre: un inicializador no tiene locales
ni parámetros, así que un nombre suelto es un atributo de la clase actual
(propio o heredado) o no está declarado.

**D5 · Orden de declaración.** `Atributo` lleva un **índice de
declaración** que asigna `Clase.agregarAtributo()` en el orden del fuente
(un contador de la clase; un atributo duplicado no se registra y no recibe
índice). Hace falta porque `atributos` es un `HashMap` y `Token` no guarda
la columna: con la línea sola, `int a = b; int b = 1;` en una línea no se
podría resolver. El índice sólo se compara entre atributos de la misma
clase declarante.

**D6 · Referencia adelantada.** En `NodoAccesoVar`, cuando el nombre
resuelve a un atributo y `atributoActual` no es null, es error si el
atributo encontrado lo declara la clase actual y su índice no es menor que
el de `atributoActual` (el propio atributo incluido). Se reporta
`ERR_REFERENCIA_ADELANTADA` sobre el nombre usado, después de resolverlo y
antes de seguir con el encadenado. Así `b.x`, `b[0]` y `m(b)` cuentan,
porque `b` es un `NodoAccesoVar`. No cuentan los accesos con receptor
(`this.b` y `obj.b` son `NodoVarEncadenada`), los atributos heredados (los
declara otra clase y no se pueden ocultar: `ERR_ATRIBUTO_DUPLICADO`) ni lo
que lea un método llamado. En métodos y constructores `atributoActual` es
null y la regla no aplica.

**D7 · Excepción del destino.** Como en JLS §8.3.3, el nombre suelto que es
el destino **directo** de un `=` no es referencia adelantada:
`int a = (b = 3); int b;` es válido. `NodoExpAsignacion` chequea su lado
izquierdo "como destino", y sólo un `NodoAccesoVar` **sin encadenado** usa
eso para saltear D6. `b[0] = 1` y `b.x = 1` siguen siendo referencias
adelantadas, porque el destino es el componente o el atributo, no `b`.
`NodoExpPostfija` chequea su operando como siempre, así que `b++` y `b--`
son referencias adelantadas, igual que en javac.

**D8 · Interacción con logros no implementados.** Un ternario, una lambda,
un diamante, un arreglo con inicializador o una llamada a un método
genérico en el inicializador dan `ERR_NO_SOPORTADO` sobre su propio token
(spec 0003, D11). La fila "atributo inicializado" de la tabla de D11 deja
de existir.

**D9 · Contrato de salida.** No cambia: `Error Semántico en línea <L>:
<descripción>` y después `[Error:<lexema>|<línea>]`. El tipo declarado del
atributo sigue valiendo aunque su inicializador tenga errores, así que sus
usos no dan errores en cascada.

### Códigos de error

| Código | Lexema | Descripción |
|---|---|---|
| `ERR_ASIGNACION_INCOMPATIBLE` (existe) | `=` | el valor asignado no conforma con el tipo del destino |
| `ERR_REFERENCIA_ADELANTADA` (nuevo) | el nombre usado | el atributo X se usa en un inicializador antes de su declaración |

Las dos filas ya están en la tabla de convenciones de
`etapa4-semantico2/casos-de-prueba.md`.

## Testing Decisions

Se observa sólo la salida del compilador (la etiqueta `[Error:…]`), como en
las specs 0001–0004. El único punto de prueba es el que ya existe:
`TesterLogrosSem2` sobre `resources/semantico2/logros/atributosInicializados/`,
que exige **exactamente** las etiquetas `///[...]` iniciales y en orden. No
hay tests unitarios ni casos nuevos en la base: hoy ningún caso de
`semantico2/{sinErrores,conErrores}` usa un atributo inicializado, así que
habilitar el logro no cambia ninguna etiqueta base.

- **Habilitar el logro:** se agrega `atributosInicializados` a
  `LOGROS_HABILITADOS`.
- **Casos correctos** (`semIIAtribInicCorrectoNN`):

  | # | Qué prueba |
  |---|---|
  | 01 | *(existe)* `int` y `boolean` con literales |
  | 02 | Conformidad: `char`, `String`, `null` en una referencia, subclase en un atributo del padre, clase en un atributo de su interfaz, `int[]` con `new int[3]`, `Caja<A>` con `new Caja<A>()`, y `T t = null;` en `Caja<T>` |
  | 03 | Contexto de instancia: `this`, `this.x`, un atributo anterior, uno heredado de un padre escrito más abajo, uno heredado de un padre genérico ya instanciado, una llamada sin receptor a un método de instancia, `A.m()` estático y un atributo `private` propio |
  | 04 | Lo que no es referencia adelantada: `this.b`, `new A().b`, `m()` que lee un atributo posterior y `(b = 3)` con `b` posterior |
  | 05 | La regla no aplica en métodos ni constructores: los dos usan un atributo declarado al final de la clase |

- **Casos con error** (`semIIAtribInicErrorNN`; `L` es la línea marcada):

  | # | Qué prueba | Etiquetas, en orden |
  |---|---|---|
  | 01 | *(existe)* `int a1 = true;` | `=` |
  | 02 | `int a = null;` | `=` |
  | 03 | Llamada a un método `void` | `=` |
  | 04 | Padre en un atributo de la subclase: `B b = new A();` | `=` |
  | 05 | Invariancia y dimensiones, en dos atributos: `Caja<A> c = new Caja<B>();` y `int[] v = new int[2][2];` | `=`, `=` |
  | 06 | Error adentro del inicializador: `int a = 1 + true;` (sin otro error de conformidad) | `+` |
  | 07 | Variable no declarada: `int a = z;` | `z` |
  | 08 | Referencia adelantada: `int a = b;` con `int b = 1;` en la línea siguiente | `b\|L` |
  | 09 | Autorreferencia: `int x = x + 1;` | `x` |
  | 10 | Los dos atributos en una línea: `int a = b; int b = 1;` | `b` |
  | 11 | Postfijo: `int a = b++;` con `b` posterior | `b` |
  | 12 | Uso como argumento, indexado y encadenado, en tres atributos: `m(b)`, `v[0]`, `o.x` con `b`, `v` y `o` posteriores | `b`, `v`, `o` |
  | 13 | Sólo el destino directo se salva: `int a = (c = b);` con `b` y `c` posteriores, y `int d = (v[0] = 1);` con `v` posterior | `b`, `v` |
  | 14 | Visibilidad: `int a = new B().secreto;` con `secreto` private en `B` | `secreto` |
  | 15 | Logro no implementado: `int a = true ? 1 : 2;` | `?` |
  | 16 | Multi-detección: un atributo mal inicializado, un error de tipos en un método escrito antes del siguiente atributo, y una referencia adelantada en ese atributo | los tres, en orden de programa |

  Las líneas se fijan al escribir cada archivo. Un caso con error que
  dependa de otro logro no implementado no vale, salvo el 15.

- **Corrida completa:** `./test.sh` en verde al final de cada ticket. La
  base y las Etapas 1–3 no cambian.
- **Antecedentes:** los logros *For*, *Instanceof*, *Visibilidad
  chequeada* y *Todos los caminos llevan al retorno* (mismo tester,
  carpetas de casos y forma de habilitarlos).

## Out of Scope

- Atributos `static` y sus reglas (`this` o métodos de instancia desde un
  inicializador estático): la gramática no los admite (§6.1).
- Bloques inicializadores (`{ ... }` como miembro) y el orden de
  inicialización en tiempo de ejecución: no son parte de MiniJava ni de esta
  etapa.
- Asignación definida (JLS cap. 16): los atributos tienen valor por defecto.
- Evaluar el inicializador o tratar atributos como constantes: `final` no se
  aplica a atributos, y esta etapa no calcula valores (ADR 0009).
- Inicializadores de arreglo sueltos (`int[] v = {1, 2};`): la gramática no
  los admite (§11, "Estas restricciones son sintácticas").
- El chequeo de la sintaxis de otros logros adentro del inicializador (D8).

## Further Notes

- Referencias: JLS §8.3.2 (inicializadores de atributos) y §8.3.3
  (referencias adelantadas); javac las reporta como "illegal forward
  reference" y "self-reference in initializer".
- Vocabulario: `GLOSARIO.md` (*chequeo de sentencias*, *contexto de
  chequeo*, *referencia adelantada*). Un inicializador de atributo **no** es
  una *unidad*.
- El boceto de `requerimientos.md` (sección NODOS, "Atributos
  inicializados") ya está alineado con esta spec.
- Al cerrar hay que actualizar: el "Estado de la implementación" de
  `etapa4-semantico2/requerimientos.md` (sacar el logro de los pendientes),
  `casos-de-prueba.md` (índice), la tabla D11 de la spec 0003 si se la
  referencia, el `CLAUDE.md` (lista de logros chequeados y conteo de casos)
  y el índice de `Documentación/README.md`.

## Tickets

En `0005-atributos-inicializados/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Conformidad del inicializador | — | hecho |
| 02 | Referencia adelantada | 01 | hecho |
| 03 | Cierre | 02 | hecho |

Reparto de casos: 01 → Correcto 01–03, Error 01–07, 14 y 15; 02 → Correcto
04 y 05, Error 08–13 y 16.

**Cierre (2026-10-26):** `./test.sh` en verde. Etapas 1–3 sin cambios
(léxico 14 + 24, sintáctico 38 + 63 + 4 de modo pánico, semántico 37 + 104);
Sem2 con 92 casos sin error, 171 con error y 128 de logros (21 de este
logro: 5 correctos y 16 con error). La base no cambió.
