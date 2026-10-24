---
titulo: Chequeo de sentencias base (Etapa 4)
estado: ready-for-agent
origen: entrevista de diseño (grilling, 2026-10-24)
fecha: 2026-10-24
---

# Chequeo de sentencias base (Etapa 4)

## Problem Statement

El compilador controla las declaraciones (Etapa 3), pero no mira los
cuerpos: los métodos, los constructores y los inicializadores de atributos
se parsean y se descartan. Un programa con una variable no declarada, una
asignación incompatible o una llamada con argumentos equivocados da
`[SinErrores]`. El documento "Semántica de MiniJava – Documento completo"
(§9–§12) define esos controles, y los 138 casos de
`Código/resources/semantico2/` los fijan.

El diseño ya está casi completo en `etapa4-semantico2/requerimientos.md`
(decisiones del 2026-10-17, ⚠1–⚠13 y la revisión del 2026-10-23), pero
quedaban abiertos el alcance, el corte en tickets y algunos puntos de
diseño (representación de tipos, resolución de variables, conformidad,
vista desde el receptor, logros sin nodo propio). Además, el WIP que hay
en el árbol (`Model/semantico/Nodo*.java`, `Alcance`, los stubs de scope de
`TablaSimbolos`, `ModuloPrincipalET4`, `AnalizadorSemanticoHandler2Impl`)
contradice ese diseño y no compila.

## Solution

El parser construye un AST de cada cuerpo mientras parsea y lo engancha en
`Metodo`, `Constructor` y `Atributo`. Si no hubo errores léxicos,
sintácticos ni de declaraciones, una tercera pasada
(`TablaSimbolos.chequearSentencias()`) recorre los cuerpos propios de cada
clase y chequea tipos, destinos asignables, formas de sentencia, scopes
locales y resolución de nombres según §9–§12. Cada sentencia se chequea
aparte: un error corta esa sentencia y el chequeo sigue con la próxima
(logro *Multi-detección*).

La sintaxis de logros que el parser ya acepta se convierte en nodos, pero
su chequeo da un error explícito de "no soportado". Los logros entran
después, en specs propias. La única excepción son los **postfijos**,
porque el caso de referencia de la cátedra `semIICorrecto03` usa `a1++`.

El análisis completo pasa a un main propio, `ModuloPrincipalET4`, que es el
del jar. `ModuloPrincipalET3` queda congelado con sólo declaraciones.

## Contrato heredado

Valen sin cambios, y esta spec no los reabre:

- `requerimientos.md`, "Decisiones de diseño": main propio, sin chequeo en
  cascada, predefinidas, sólo miembros propios, alcance, estado del
  chequeo en `TablaSimbolos`, tres campos de unidad actual, ocultamiento,
  excepción y captura por sentencia, paquete `Model.semantico.ast`.
- `requerimientos.md`, "Decisiones sobre los nodos" (⚠1–⚠13), con el
  mapeo de no terminal a nodo y el árbol de herencia de los nodos.
- `requerimientos.md`, "Revisión contra el enunciado completo": receptor
  interfaz, receptor arreglo, receptor parámetro de tipo y tipos raw en
  `new`.

Si algo de esas secciones contradice a esta spec, vale esta spec.

## Implementation Decisions

**D1 · Alcance.** Lo base de §9–§12 (los 138 casos de `semantico2/`), más
el logro *Multi-detección* y los *Postfijos*. *Caminos de retorno* y
*Floats y coerción* quedan afuera también en las specs de logros, hasta
decidir qué pasa con los casos base que contradicen (`semIICorrecto31`,
`semIIError104`; `semIIError33`, `34`, `52`, `59`, `70`).

**D2 · El WIP se descarta.** Se borran `Model/semantico/Nodo*.java`,
`Alcance.java`, los métodos `abrirScope`/`cerrarScope`/
`añadirAtributoScope`/`buscarAtributo` de `TablaSimbolos`,
`Metodo.variablesLocales` y `Controller/AnalizadorSemanticoHandler2{,Impl}`.
Se rehacen según el diseño: los nodos en `Model.semantico.ast`, y el
controller como `AnalizadorSemantico2Handler` / `AnalizadorSemantico2HandlerImpl`.

**D3 · Pipeline.** `AnalizadorSemantico2HandlerImpl` corre lo mismo que
el de ET3 (léxico → sintáctico → `estaBienDeclarado()` → `consolidar()`) y
después `chequearSentencias()`, **sólo** si no hubo errores léxicos,
sintácticos ni semánticos de declaraciones. `build.sh` arma el jar con main
`View.ModuloPrincipalET4`, y `Tester{Sin,Con}ErroresSem2` usan ET4.

**D4 · Recorrido.** `chequearSentencias()` resetea la unidad actual,
recorre las clases en el orden del `LinkedHashMap` (salta interfaces y
predefinidas) y en cada una, sólo los miembros **propios**: atributos con
inicializador, constructores y métodos, en ese orden. Para eso se setea
`Metodo.miClase` al registrar el método (hoy nunca se setea), y `Atributo` y
`Constructor` guardan su clase declarante. `setUnidadActual(metodo,
constructor, atributo)` deja siempre un solo campo distinto de `null`.

**D5 · Tipos.** `Tipo` sigue siendo una sola clase, sin la jerarquía
diferida. Se le agregan:
- `conformaCon(Tipo destino, TablaSimbolos tabla)` (D9);
- `esPrimitivo()`, `esArreglo()`, `esReferencia()`, `esVoid()`, `esNull()`;
- las constantes `Tipo.NULL` y `Tipo.VOID`, con un token sintético, para el
  literal `null` y para el resultado de una llamada a un método `void`.

`Metodo` sigue representando `void` con `tipoRetorno == null`, porque la
comparación de signaturas de la Etapa 3 depende de eso. Al tipar una
llamada se traduce a `Tipo.VOID`.

**D6 · Variables y scopes.** Hay una interfaz `Variable { Token
getNombre(); Tipo getTipo(); }`, implementada por `Parametro`, `Atributo` y
una nueva `VariableLocal`. La pila es `Deque<Map<String, VariableLocal>>`
en `TablaSimbolos`, con `abrirBloque()` y `cerrarBloque()`, y todo nodo que
abre un scope lo cierra en un `finally`. `buscarVariable(nombre)` busca en
este orden y devuelve `null` si no encuentra:
1. locales, del tope de la pila a la base;
2. parámetros de `metodoActual` o de `constructorActual` (un inicializador
   de atributo no tiene);
3. `claseActual.getAtributo(nombre)`, que ya incluye los heredados porque
   `consolidar()` los copia.

Quien la llama reporta `ERR_VAR_NO_DECLARADA`. El acceso implícito a un
atributo desde un contexto estático lo reporta `NodoAccesoVar` después de
resolver (`ERR_ATRIBUTO_EN_ESTATICO`).

Si el inicializador de un `var` falla, la local **no** se registra. Un uso
posterior da `ERR_VAR_NO_DECLARADA`. Es aceptable, porque el piso de
*Multi-detección* es seguir con la próxima sentencia, y ningún caso
depende de lo contrario.

**D7 · Errores.** Las expresiones y las sentencias lanzan
`ExcepcionSemantica(Token, String codigo)` (en `Model.semantico`).
`NodoBloque` la atrapa en cada sentencia y la agrega con
`tabla.agregarError(...)`. El chequeo de un atributo inicializado tiene su
propia captura. Los códigos nuevos van en la tabla de abajo, cada uno con su
descripción en `ErrorSemantico.getDescripcion()`. La tabla "Convención de
lexema" de `etapa4-semantico2/casos-de-prueba.md` pasa de propuesta a
contrato.

**D8 · AST con errores sintácticos.** En modo pánico el parser puede dejar
nodos incompletos (hijos en `null`). Los `chequear` no llevan guardas
contra `null`, porque D3 impide chequear un árbol roto. La única exigencia
es que construir el AST nunca lance una excepción durante el pánico.

**D9 · Conformidad (§9, §11.2).** `S.conformaCon(D)`:
- primitivos: sólo si son iguales (sin conversiones);
- `void`: no conforma con nada, ni siquiera con `void`;
- `null`: conforma con clases, interfaces, parámetros de tipo y arreglos,
  y no con primitivos;
- clase/interfaz `S` con `D<a>`: si `D` es `S` o un ancestro de `S` por
  `extends`/`implements` (transitivo; toda clase llega a `Object`) y el
  argumento con que `S` ve a `D` (vía `instanciacion`) es **igual** a `a`,
  es decir, invariante;
- parámetro de tipo `T`: sólo conforma con el mismo `T` (mismo origen). No
  conforma con `Object`, porque no tiene cotas;
- arreglos: tipo base y dimensiones iguales. Todo arreglo conforma con
  `Object`;
- `==` y `!=`: válidos si uno de los operandos conforma con el otro.

**D10 · Vista desde el receptor (§5.1).** `TablaSimbolos.instanciacionDe(Tipo
receptor)` arma el mapa `{entidad del receptor → su argumento}` compuesto
con `entidad.getInstanciacion()` (usando `Tipo.componerInstanciacion`).
`NodoVarEncadenada`, `NodoLlamadaEncadenada` y `NodoCreacionObjeto` aplican
`Tipo.sustituir(mapa)` al tipo del atributo, a los parámetros y al retorno.
Reemplaza el `sustituir(nombreParam, argumento)` del boceto de
`requerimientos.md`.

**D11 · Logros: "no soportado".** El `chequear` de un nodo de logro lanza
`ERR_NO_SOPORTADO` sobre este token:

| Construcción | Lexema |
|---|---|
| `for` / for-each | `for` |
| ternario | `?` |
| local clásica (`int x = …;`) | nombre de la primera local |
| atributo inicializado | nombre del atributo |
| lambda | `->` |
| arreglo con inicializador | `new` |
| llamada a un método genérico (con parámetros de tipo propios) | nombre del método |
| diamante `new C<>()` | nombre de la clase |

Como cualquier otro error, se informa una vez por sentencia. Lo que no
tiene nodo ni regla queda así: la **visibilidad** se ignora (todo es
accesible; el parser ni siquiera la guarda), y los **genéricos anidados**
(`Caja<Caja<String>>`) se aceptan sin trato especial, porque salen solos de
la recursión de `Tipo`.

**D12 · Postfijos.** `NodoExpPostfija` exige un operando asignable de tipo
`int`. El resultado es `int` y no es asignable, así que `a++++` es error.
Si no, se reporta `ERR_OPERADOR_INCOMPATIBLE` sobre el operador o
`ERR_DESTINO_NO_ASIGNABLE` sobre `++`/`--`. `a++;` es una sentencia
válida: `NodoSentenciaExpresion` acepta una asignación, una expresión que
termina en llamada o en `new` de objeto, o un postfijo.

**Contrato de salida.** No cambia: `Error Semántico en línea <L>:
<descripción>` y después `[Error:<lexema>|<línea>]`.

### Códigos de error nuevos

| Código | Lexema | Casos de ejemplo |
|---|---|---|
| `ERR_VAR_NO_DECLARADA` | nombre | `semIIError01`, `06`, `07`, `90` |
| `ERR_ATRIBUTO_EN_ESTATICO` | nombre | `08` |
| `ERR_ATRIBUTO_NO_ENCONTRADO` | nombre del atributo | `09`, `12`, `73` |
| `ERR_RECEPTOR_SIN_MIEMBROS` (primitivo, `T` sin instanciar) | nombre del miembro | `10`, `11`, `20`, `21` |
| `ERR_METODO_NO_ENCONTRADO` (nombre/aridad) | nombre del método | `13`, `14`, `22`, `74` |
| `ERR_ARGUMENTO_INCOMPATIBLE` (método o constructor) | nombre del método o de la clase | `04`, `15`, `16`, `27`, `28`, `49` |
| `ERR_METODO_NO_ESTATICO` | nombre del método | `17`, `18` |
| `ERR_NEW_INTERFAZ` | nombre de la interfaz | `23` |
| `ERR_CONSTRUCTOR_NO_ENCONTRADO` | nombre de la clase | `25`, `26` |
| `ERR_THIS_EN_ESTATICO` | `this` | `30`, `31` |
| `ERR_PARAMETRO_TIPO_EN_ESTATICO` | `T` | `32` |
| `ERR_ASIGNACION_INCOMPATIBLE` | `=` | `02`, `33`–`40`, `42`–`48`, `97` |
| `ERR_DESTINO_NO_ASIGNABLE` | `=` (o `++`/`--`) | `03`, `63`–`68` |
| `ERR_OPERADOR_INCOMPATIBLE` (incluye operando `void`) | el operador | `50`–`62` |
| `ERR_INDICE_NO_ENTERO` (índice o tamaño) | `[` | `69`, `70` |
| `ERR_NO_ES_ARREGLO` | `[` | `71`, `72`, `75` |
| `ERR_SENTENCIA_INVALIDA` | `;` | `76`–`83` |
| `ERR_LOCAL_DUPLICADA` (repetida, choca con parámetro u oculta una local visible) | nombre de la local | `05`, `84`–`87` |
| `ERR_VAR_SIN_TIPO` (`null` o `void`) | nombre de la local | `88`, `89` |
| `ERR_DECLARACION_COMO_CUERPO` | `var` | `91`–`93` |
| `ERR_CONDICION_NO_BOOLEAN` | `if` / `while` | `94`–`96` |
| `ERR_RETORNO_INCORRECTO` | `return` | `41`, `98`–`103` |
| `ERR_NO_SOPORTADO` | ver D11 | — |

Se reusan dos códigos de la Etapa 3: `ERR_TIPO_NO_DECLARADO` para
`Inexistente.m()` y `new Inexistente()` (`19`, `29`), y
`ERR_TIPO_SIN_ARGUMENTO` para `new Caja()` (`24`). Los nombres pueden
ajustarse al implementar, siempre que el lexema y la línea sigan la tabla.

## Testing Decisions

Se observa sólo la salida del compilador, como en las specs 0001 y 0002.

- **Casos base:** `Tester{Sin,Con}ErroresSem2` (ya existen) sobre
  `resources/semantico2/{sinErrores,conErrores}`. Cada ticket de chequeo
  (03–06) lista los casos que tiene que poner en verde. Cada caso aparece en
  exactamente un ticket: el primero que cubre todo lo que el caso usa.
- **Logros:** un `TesterLogrosSem2` nuevo recorre
  `logros/<logro>/{conErrores,sinErrores}` sólo para una lista de logros
  habilitados: `multiDeteccionErrores` y `postfijos`. En los casos con error
  exige **todas** las etiquetas `///[...]` de las primeras líneas. Las specs
  de logros futuras sólo agregan el nombre a la lista.
- **Casos que se ajustan:**
  - `semIIMultiErrorError01`: la línea 2 pasa a `///[Error:z|11]`, sin
    mover el resto de las líneas.
  - `semIIPostfijoError01`: la local clásica `boolean b = true;` pasa a ser
    parámetro (`void m1(boolean b)`), y `b++` queda en la línea 7. Si no, el
    caso dependería del logro de locales clásicas.
- **Caso sintáctico nuevo** para ⚠13: postfijo y ternario en la rama `.` de
  `<SentIdClase>` (`A1.m().x++;` y `A1.m().b ? 1 : 2;`). El caso es del
  sintáctico (`resources/sintactico/sinErrores/`), así que sólo exige que
  parsee: semánticamente, la segunda sentencia no es válida.
- **`test.sh`:** Sem2 sigue aparte (`./test.sh sem2`) hasta el ticket 07, así
  los casos que todavía están en rojo no bloquean el hook pre-commit. En el 07,
  Sem2 y `TesterLogrosSem2` entran en la corrida por defecto.
- **Etapas 1–3:** todos sus testers siguen en verde al final de cada
  ticket. `ModuloPrincipalET3` no cambia de comportamiento.
- `semIIError01` (cátedra, sin modificar) tiene `//[Error:v1|9]` con dos
  barras. El tester corta 3 caracteres y busca `Error:v1|9]`, que igual
  aparece en la salida. Se deja como está.

## Out of Scope

- Los demás logros de la Etapa 4: minilambdas, for, caminos de retorno,
  floats y coerción, ternarios, genericidad avanzada (diamante),
  instanceof, visibilidad, super, métodos genéricos, locales clásicas y
  atributos inicializados. Sus nodos se construyen, pero dan
  `ERR_NO_SOPORTADO`.
- La jerarquía de `Tipo` (`TipoPrimitivo`, `TipoReferencia`, …).
- Registrar una local cuyo inicializador falló (D6).
- Generación de código (Etapas 5–6).

## Further Notes

- Documento de referencia: "Semántica de MiniJava – Documento completo"
  (v3.0). Ante lo no especificado, Java (ADR 0004).
- Gramática de referencia: el código de `AnalizadorSintacticoImpl`, no el
  documento de Etapa 2.
- Índice de casos: `etapa4-semantico2/casos-de-prueba.md`.

## Tickets

En `0003-chequeo-sentencias-base/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Infraestructura | — | hecho |
| 02 | AST completo | 01 | hecho |
| 03 | Núcleo: expresiones, variables y sentencias | 02 | hecho |
| 04 | Objetos y llamadas | 03 | pendiente |
| 05 | Arreglos | 04 | pendiente |
| 06 | Genéricos | 05 | pendiente |
| 07 | Cierre | 06 | pendiente |

Reparto de casos base: 03 → 43 con error + 6 sin error; 04 → 38 + 15;
05 → 14 + 5; 06 → 9 + 8. En total, 104 + 34.
