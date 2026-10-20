---
titulo: Corrección del control de declaraciones (Etapa 3)
estado: ready-for-agent
origen: etapa3-semantico/plan-correccion-etapa3.md
fecha: 2026-10-20
---

# Corrección del control de declaraciones (Etapa 3)

## Problem Statement

El compilador MiniJava ya hace la construcción de la tabla, el control de
declaraciones y la consolidación. Pero, comparado con el documento de
referencia de la etapa ("Semántica de MiniJava – Parte I. Declaraciones"),
se comporta distinto en varios puntos, y en uno se cae:

- **Se cae.** Si una clase extiende una interfaz, el compilador termina con
  una excepción en lugar de imprimir una etiqueta. Se pierden todos los
  errores detectados, y cualquier caso de la cátedra con ese programa falla.
- **No existen los tipos predefinidos.** `Object`, `String` y `System` no
  están en la tabla. Una clase sin `extends` no hereda de `Object`. Se puede
  redeclarar `String`, y no se puede usar `String` como tipo sin declararlo.
- **La clave de los métodos está mal.** Los duplicados se detectan por
  nombre y tipos de parámetros, cuando la Parte I pide la clave (nombre,
  aridad). Hoy `imprimir(int)` + `imprimir(char)` pasa, y debería ser una
  declaración repetida. Con los constructores pasa lo mismo.
- **La redefinición no se controla.** Un método puede redefinir uno
  heredado con otra signatura. Las reglas de los estáticos no existen. El
  contrato de interfaz sólo mira la clave y el retorno.
- **No se instancian los genéricos al comparar.** Las signaturas de los
  miembros heredados de un tipo declarado genérico no se ven con el
  argumento de la herencia. Además, `Tipo` no distingue `int[]` de `int`.
- **Relaciones mal formadas que pasan sin error:** `implements` de una
  clase, `extends` de una interfaz hacia una clase, argumentos inválidos en
  `extends`/`implements` (`extends Caja<Nada>`), constructores con otro
  nombre que el de su clase y `class X implements A, B` (ADR 0005).
- **No hay constructor por defecto.**

Todo esto tiene que quedar resuelto antes de empezar la Etapa 4. El
chequeo de sentencias va a apoyarse en los tipos predefinidos, en las
claves y en la vista instanciada de los miembros heredados.

## Solution

Se corrige el control de declaraciones y la consolidación para que el
compilador cumpla la Parte I. Lo que la Parte I no especifica sigue las
reglas de Java (su "regla de oro"), salvo las desviaciones deliberadas de
los ADR 0001, 0004 y 0005.

Al terminar:

- Todo programa produce una salida con etiqueta: `[SinErrores]` o un
  `[Error:<lexema>|<línea>]` por cada error. El compilador nunca se cae.
- Se reportan todos los errores de declaración. Cada uno lleva el lexema y
  la línea del token de la entidad que lo provoca.
- La tabla queda consolidada con:
  - los tipos predefinidos;
  - `Object` como padre de toda clase sin `extends`;
  - los miembros heredados;
  - un constructor por defecto donde corresponde.

Los logros de Etapa 3 que ya están hechos (`sealed`/`final`, métodos
genéricos, multi-detección, genericidad avanzada y herencia múltiple de
interfaces) siguen funcionando.

El trabajo se hace en pasos chicos y revisables. Cada uno termina con todos
los testers de Etapas 1–3 en verde.

## User Stories

**Robustez y formato de salida**

1. Como estudiante que entrega el compilador, quiero que nunca termine con
   una excepción ante un programa sintácticamente válido, para que el
   tester de la cátedra siempre encuentre una etiqueta.
2. Como corrector de la cátedra, quiero que `class Bb extends Ii` (con `Ii`
   interfaz) imprima `[Error:Ii|<línea>]`, para poder evaluar el caso como
   cualquier otro.
3. Como corrector, quiero que los errores detectados antes de un error de
   relación se sigan imprimiendo, para no perder información por un error
   posterior.
4. Como programador MiniJava, quiero que cada error de declaración se
   reporte una sola vez, para no leer el mismo mensaje repetido.

**Tipos predefinidos**

5. Como programador MiniJava, quiero usar `Object`, `String` y `System`
   como tipos de atributos, parámetros y retornos sin declararlos, para
   escribir programas como en Java.
6. Como programador MiniJava, quiero que `class Object {}`,
   `class String {}` o `class System {}` den error de clase duplicada sobre
   mi declaración, para saber que no puedo redeclarar un tipo predefinido.
7. Como programador MiniJava, quiero que, si redeclaro `String` por error,
   el resto del programa siga viendo el `String` predefinido, para no
   recibir errores en cascada en cada uso de `String`.
8. Como programador MiniJava, quiero que `class A extends String {}` y
   `class A extends System {}` den error de herencia de clase final, porque
   así se comporta Java (ADR 0004).
9. Como programador MiniJava, quiero poder extender `Object`
   explícitamente, para escribir `class A extends Object` como en Java.
10. Como programador MiniJava, quiero que `Object` declare
    `static void debugPrint(int i)` y `String toString()`, y que toda clase
    los herede, para usarlos en la Etapa 4.
11. Como programador MiniJava, quiero que `System` declare `read`,
    `printB`, `printC`, `printI`, `printS`, `println`, `printBln`,
    `printCln`, `printIln` y `printSln` como estáticos, para la entrada y
    salida de la Etapa 4.
12. Como programador MiniJava, quiero que `String` herede de `Object` y no
    declare métodos propios, como dice el §7.

**Herencia implícita de `Object`**

13. Como programador MiniJava, quiero que una clase sin relación explícita
    extienda `Object`, para que herede sus métodos.
14. Como programador MiniJava, quiero que una clase que implementa una
    interfaz también tenga `Object` como padre, para que herede
    `toString` y `debugPrint`.
15. Como desarrollador del compilador, quiero que la herencia implícita no
    cambie lo que el programa escribió, para que `sealed`/`permits` sigan
    distinguiendo `class A` de `class A extends Object`.

**Relaciones entre tipos declarados**

16. Como programador MiniJava, quiero un error cuando una clase extiende
    una interfaz, que diga que el tipo nombrado no es una clase.
17. Como programador MiniJava, quiero un error cuando una clase implementa
    una clase, que diga que el tipo nombrado no es una interfaz.
18. Como programador MiniJava, quiero un error cuando una interfaz extiende
    una clase, que diga que el tipo nombrado no es una interfaz.
19. Como programador MiniJava, quiero que, tras un error de relación, la
    clase siga analizándose con `Object` como padre, para ver los demás
    errores del programa.
20. Como programador MiniJava, quiero que `extends Nada` o
    `implements Nada`, con `Nada` inexistente, den un único error de tipo
    no declarado.
21. Como programador MiniJava, quiero que `extends Caja<Nada>` dé error de
    tipo no declarado sobre `Nada`, para que el argumento de una herencia
    se valide como cualquier tipo referencia.
22. Como programador MiniJava, quiero que `extends Bb<Bb>`, con `Bb` no
    genérica, dé error de tipo no genérico.
23. Como programador MiniJava, quiero que un argumento que es un parámetro
    de tipo fuera de alcance dé error de parámetro de tipo no declarado.
24. Como programador MiniJava, quiero poder heredar de un tipo declarado
    genérico sin argumento, como permite el §5.
25. Como programador MiniJava, quiero que `class X implements A, B` sea un
    error sintáctico, porque una clase implementa como máximo una interfaz
    (ADR 0005).
26. Como programador MiniJava, quiero que una interfaz pueda extender
    varias interfaces, para mantener el logro de herencia múltiple de
    interfaces.
27. Como programador MiniJava, quiero que repetir una interfaz en la lista
    de `extends` de una interfaz siga dando error.

**Clave (nombre, aridad)**

28. Como programador MiniJava, quiero declarar `mostrar()` y
    `mostrar(int n)` en la misma clase, para usar la sobrecarga por aridad.
29. Como programador MiniJava, quiero que `imprimir(int n)` +
    `imprimir(char c)` dé error de método duplicado sobre el segundo,
    porque tienen la misma clave.
30. Como programador MiniJava, quiero que dos métodos con la misma clave y
    distinto retorno den error de método duplicado.
31. Como programador MiniJava, quiero que dos constructores con la misma
    aridad den error de constructor duplicado, aunque difieran los tipos de
    sus parámetros.
32. Como programador MiniJava, quiero que la descripción de esos errores
    hable de "la misma cantidad de parámetros", para entender por qué son
    duplicados.
33. Como programador MiniJava, quiero que la misma regla de clave valga
    para los métodos de una interfaz.

**Constructores**

34. Como programador MiniJava, quiero un error con el nombre y la línea de
    un constructor cuyo nombre no es el de su clase, como pide el §6.3.
35. Como programador MiniJava, quiero que una clase sin constructores
    reciba un constructor por defecto sin parámetros, para poder hacer
    `new A()` en la Etapa 4.
36. Como programador MiniJava, quiero que una clase con al menos un
    constructor no reciba el constructor por defecto.
37. Como programador MiniJava, quiero que `Object` y `String` tengan
    constructor por defecto y `System` no tenga ninguno (ADR 0004).
38. Como programador MiniJava, quiero que los parámetros repetidos de un
    constructor sigan dando error.

**Redefinición**

39. Como programador MiniJava, quiero que redefinir un método heredado con
    la misma signatura sea válido, para escribir `String toString()` en mis
    clases.
40. Como programador MiniJava, quiero que redefinir un método heredado con
    otro retorno (`int toString()`) dé error de redefinición incompatible
    sobre mi método.
41. Como programador MiniJava, quiero que redefinir con otros tipos de
    parámetros dé error de redefinición incompatible.
42. Como programador MiniJava, quiero que `int[]` e `int` sean tipos
    distintos al comparar signaturas, para que `m(int[] a)` y `m(int a)` no
    se confundan.
43. Como programador MiniJava, quiero que los nombres de los parámetros no
    importen al comparar signaturas.
44. Como programador MiniJava, quiero que `class D extends Base<String>`
    pueda redefinir `T get()` como `String get()`, porque la comparación se
    hace después de instanciar.
45. Como programador MiniJava, quiero que la instanciación se componga en
    cadena (`Final extends Intermedia<String>`, con
    `Intermedia<U> extends Base<U>`), para que `Final` redefina con
    `String` los métodos de `Base`.
46. Como programador MiniJava, quiero que el `T` de `class D<T> extends Base`
    no se confunda con el `T` de `Base`, porque son parámetros de tipo
    distintos.
47. Como programador MiniJava, quiero que, al heredar sin argumento, el
    parámetro de tipo conservado no se reemplace por `Object` al comparar,
    como dice el §5.
48. Como programador MiniJava, quiero que `<V> V id(V x)` redefina
    `<U> U id(U x)`, porque los parámetros de tipo de método se comparan
    por posición.
49. Como programador MiniJava, quiero que un método genérico y uno no
    genérico con la misma clave tengan signaturas distintas.
50. Como programador MiniJava, quiero que redefinir un método final siga
    dando error.

**Métodos estáticos**

51. Como programador MiniJava, quiero que declarar `void debugPrint(int x)`
    en una clase dé error, porque `Object` declara un estático con esa
    clave.
52. Como programador MiniJava, quiero que ningún descendiente pueda
    declarar otro método, estático o de instancia, con la clave de un
    estático heredado.
53. Como programador MiniJava, quiero que un estático no pueda tener la
    clave de un método de instancia heredado.
54. Como programador MiniJava, quiero que los estáticos heredados sigan
    visibles en los descendientes, para escribir `A.debugPrint(3)` en la
    Etapa 4.

**Contratos de interfaz**

55. Como programador MiniJava, quiero que una clase cumpla el contrato con
    un método de instancia propio o heredado de la misma clave y la misma
    signatura.
56. Como programador MiniJava, quiero que un método con la clave correcta y
    otra signatura dé error de redefinición incompatible sobre el método de
    la clase.
57. Como programador MiniJava, quiero que un método faltante dé error de
    método de interfaz no implementado.
58. Como programador MiniJava, quiero que un estático no cumpla un
    contrato, como dice el §6.2.
59. Como programador MiniJava, quiero que el contrato se compare después
    de instanciar el argumento de `implements`.
60. Como programador MiniJava, quiero que, al implementar sin argumento una
    interfaz genérica, los métodos que mencionan su parámetro de tipo no se
    puedan cumplir con `Object`, como dice el §5.
61. Como programador MiniJava, quiero que `interface X extends I1, I2` dé
    error cuando `I1` e `I2` traen la misma clave con distinta signatura.
62. Como programador MiniJava, quiero que `interface I { int toString(); }`
    dé error de redefinición incompatible, porque choca con un método de
    instancia de `Object`.
63. Como programador MiniJava, quiero que un método de interfaz con la
    clave de `debugPrint` no choque con `Object`, porque `debugPrint` es
    estático.
64. Como programador MiniJava, quiero que `toString`, heredado de `Object`,
    cumpla el contrato de una interfaz que declara `String toString()`.

**Atributos y parámetros**

65. Como programador MiniJava, quiero que un atributo con el nombre de un
    atributo heredado siga dando error, como pide el §6.1.
66. Como programador MiniJava, quiero que dos parámetros con el mismo
    nombre en una unidad sigan dando error.

**Proceso de desarrollo**

67. Como desarrollador del compilador, quiero una línea de base de todos
    los testers de Etapas 1–3 antes del primer cambio, para detectar
    regresiones.
68. Como desarrollador, quiero que cada paso termine con todos los testers
    de Etapas 1–3 en verde, para que cada cambio se pueda revisar solo.
69. Como desarrollador, quiero que, si un caso existente cambia de
    resultado por una regla de la Parte I, se corrija su etiqueta y se
    reporte el cambio, para que los casos reflejen el enunciado.
70. Como desarrollador, quiero que los casos que usaban `implements A, B`
    se reescriban con una interfaz intermedia, para conservar lo que
    probaban.
71. Como desarrollador, quiero que la documentación de diseño marque como
    implementado lo que se implementó, para no leer decisiones viejas como
    pendientes.
72. Como desarrollador de la Etapa 4, quiero que la tabla consolidada ya
    tenga predefinidas, claves y vista instanciada, para chequear
    sentencias sin volver a tocar las declaraciones.

## Implementation Decisions

**Orden de trabajo.** Cada paso es un cambio revisable que termina con
todos los testers de Etapas 1–3 en verde. No se hace commit sin pedido.

0. Línea de base.
1. Herencia múltiple sólo entre interfaces.
2. Relaciones entre tipos declarados y herencia como `Tipo`.
3. `Tipo`: dimensiones, origen de los parámetros de tipo e igualdad.
4. Clave (nombre, aridad).
5. Tipos predefinidos y constructores.
6. Instanciación genérica al comparar.
7. Redefinición, estáticos y contratos.
8. Cierre de documentación.

**Gramática (ADR 0005).** `<HerenciaOpcional>` vuelve a aceptar una única
interfaz tras `implements`. `<ExtensionOpcional>` de las interfaces sigue
aceptando una lista. El control de interfaz repetida en el `implements` de
una clase deja de tener sentido y se retira. En la lista de `extends` de
una interfaz se mantiene.

**Herencia como `Tipo` (D2).**
- La relación de herencia de una clase, su interfaz implementada y las
  interfaces extendidas por una interfaz pasan a guardarse como `Tipo`
  (cabeza más argumento opcional), en lugar de un token suelto.
- Se validan en el control de declaraciones con la misma validación que
  cualquier tipo referencia: no declarado, no genérico y parámetro de tipo
  no declarado. Heredar sin argumento es válido.

**Relación con el tipo equivocado (D1).**
- Un código nuevo, `ERR_HERENCIA_TIPO_INCORRECTO`, cubre tres variantes:
  una clase que extiende una interfaz, una clase que implementa una clase y
  una interfaz que extiende una clase.
- El lexema y la línea son los del tipo nombrado. La descripción dice qué
  se esperaba ("Ii no es una clase", "Aa no es una interfaz").
- La relación se ignora: la clase queda con `Object` como padre efectivo.
  Después se sigue chequeando lo demás.

**Supertipo inexistente (D7).**
- Se reporta una sola vez, como `ERR_TIPO_NO_DECLARADO`, en el control de
  declaraciones.
- La consolidación saltea sin reportar las relaciones que no resuelven.
- Se retira `ERR_INTERFAZ_NO_DECLARADA`. Ningún caso la documenta.

**`Tipo`.**
- Guarda la cantidad de dimensiones. El parser deja de descartarlas.
- Un `Tipo` que es parámetro de tipo guarda su origen: la clase, interfaz
  o método que lo declara, resuelto en el control de declaraciones.
- Igualdad: dos tipos son iguales si coinciden cabeza, argumento
  (recursivo), dimensiones y, en los parámetros de tipo, el origen.
- Los parámetros de tipo de método son la excepción (D6). Al comparar dos
  signaturas se emparejan por posición. Si uno de los métodos es genérico y
  el otro no, las signaturas difieren.
- La jerarquía completa de `Tipo` (primitivos y referencias) queda para la
  Etapa 4.

**Clave.**
- `getFirma()` pasa a `getClave()` en métodos, constructores y atributos.
  Para un método devuelve `nombre/aridad`; para un constructor, la aridad;
  para un atributo, el nombre.
- Los mapas de miembros de clases e interfaces se indexan por clave.
- Las descripciones de método y constructor duplicados hablan de "la misma
  cantidad de parámetros" (D4).

**Tipos predefinidos (ADR 0004).**
- `TablaSimbolos` registra `Object`, `String` y `System` al construirse,
  antes de analizar el programa. Son `Clase` comunes con una marca de
  predefinida y tokens sintéticos en la línea 0.
- `String` y `System` son finales. `Object` y `String` tienen constructor
  por defecto; `System` no tiene ninguno.
- Redeclarar una predefinida da `ERR_CLASE_DUPLICADA` sobre la declaración
  del programa. Sólo esa se descarta: la predefinida se conserva y su
  nombre no se invalida. Es una excepción a la regla general de nombres
  repetidos.
- Las predefinidas no pasan por el control de declaraciones. Sí se
  consolidan, y van primero.

**`Object` implícito.**
- El padre efectivo de una clase sin `extends` (que no sea `Object`) es
  `Object`. Lo mismo vale para una relación descartada por D1.
- Se resuelve en la consolidación. Lo que el programa escribió queda
  intacto, porque `sealed`/`permits` lo lee.

**Constructores.**
- Un constructor cuyo nombre no es el de su clase da
  `ERR_CONSTRUCTOR_NOMBRE_INVALIDO` (D3), con su lexema y su línea. Se
  reporta al encontrarlo y no se registra.
- Una clase del programa sin constructores registrados recibe uno por
  defecto, sin parámetros y con el token del nombre de la clase.

**Instanciación genérica.**
- `Tipo.sustituir(nombreParam, argumento)` se usa sólo para comparar
  signaturas. Los miembros copiados a la hija no se reescriben.
- Cada clase compone en la consolidación un mapa
  `ancestro → argumento visto desde acá`, a partir del de su padre. Con un
  solo parámetro por tipo declarado alcanza con un mapa.
- Si un ancestro no tiene entrada (herencia sin argumento), su parámetro se
  conserva y no se reemplaza por `Object` (D5).

**Redefinición y estáticos.**
- Un método de instancia con la clave de uno de instancia heredado lo
  redefine. Su signatura (instanciada) tiene que ser exactamente igual. Si
  no lo es, da `ERR_REDEFINICION_INCOMPATIBLE` sobre el método de la hija.
- Un código nuevo, `ERR_REDEFINE_ESTATICO`, cubre las dos reglas del §6.2.
  Se reporta sobre el método del descendiente.
- Los estáticos se siguen copiando a los descendientes.
- Se mantiene `ERR_METODO_REDEFINE_FINAL`.

**Contratos e interfaces.**
- Una clase cumple un método de su interfaz con un método de instancia,
  propio o heredado, de la misma clave y la misma signatura instanciada.
  - Si la clave está y la signatura difiere: `ERR_REDEFINICION_INCOMPATIBLE`
    sobre el método de la clase.
  - Si falta, o sólo hay un estático con esa clave:
    `ERR_METODO_INTERFAZ_NOIMPLEMENTADO`.
- `ERR_METODO_RETORNO_INCOMPATIBLE` compara la signatura completa. Su
  descripción pasa a "se hereda con signaturas incompatibles".
- Los métodos de una interfaz se comparan con los métodos de instancia de
  `Object` usando la regla de la redefinición. La interfaz no copia los
  miembros de `Object`.

**Contrato de salida.** No cambia:
- éxito: `[SinErrores]`;
- error semántico: una línea `Error Semántico en línea <L>: <descripción>`
  y la etiqueta `[Error:<lexema>|<línea>]`.

Los códigos `ERR_*` sólo eligen la descripción y nunca se imprimen.

## Testing Decisions

**Qué es un buen test acá.** Un caso es un programa MiniJava en un
archivo. Se observa sólo la salida del compilador: la etiqueta
`[SinErrores]` o `[Error:<lexema>|<línea>]`. No se testean clases
internas (`Tipo`, `Metodo`, la tabla). Así los casos sobreviven a cambios
de diseño interno, y son la misma clase de prueba que corre la cátedra.

**Puntos de prueba** (ya existen; no se agrega ninguno):

1. **El compilador completo, de archivo fuente a etiqueta.** Lo recorren
   los testers parametrizados del semántico de declaraciones, uno para
   casos sin errores y otro para casos con errores. En los casos con
   errores, la primera línea del archivo es la etiqueta esperada. Casi
   toda la spec se prueba acá, incluida la caída: hoy ese caso no imprime
   ninguna etiqueta y el tester falla.
2. **El tester sintáctico de casos con errores**, sólo para
   `class X implements A, B`.

**Casos nuevos:**
- Sin errores, `semCorrecto23`–`28`:
  - predefinidas como tipos;
  - `toString` redefinido;
  - redefinición instanciada;
  - cadena de instanciación;
  - sobrecarga por aridad;
  - `<V>` contra `<U>`.
- Con errores, `semError54`–`64`: predefinidas, clave, redefinición,
  estáticos, conflicto entre interfaces, interfaz contra `Object` y
  contrato con otra signatura.
- Con errores, `semError65`–`70`:
  - clase que extiende una interfaz;
  - clase que implementa una clase;
  - interfaz que extiende una clase;
  - `extends Caja<Nada>`;
  - `extends Bb<Bb>`;
  - constructor con otro nombre.
- Con errores, `semError71`: implementar sin argumento una interfaz
  genérica, con `Object obtener()` contra `T obtener()`.
- Un caso sintáctico de `implements A, B`.

El detalle de cada caso, con su código y su etiqueta, está en el
documento de casos de prueba de la Etapa 3, sección 5.6, y en el plan de
corrección.

**Casos existentes:**
- Los diez que usan `implements` con lista se reescriben con una interfaz
  intermedia, y uno pasa a ser el caso sintáctico.
- Si otro caso cambia de resultado por una regla de la Parte I (por
  ejemplo, uno que dependía de la sobrecarga por tipos), se corrige su
  etiqueta, no el código, y se reporta.

**Regresión.** En la línea de base y al final de cada paso se corren todos
los testers de Etapas 1–3: léxico, sintáctico, modo pánico y semántico de
declaraciones. Los testers Sem2 siguen fallando, como se espera hasta la
Etapa 4.

**Antecedentes:** los casos `semCorrectoNN`/`semErrorNN` y sus testers
parametrizados, y la guía de testers del proyecto.

## Out of Scope

- Toda la Etapa 4:
  - el AST;
  - el chequeo de sentencias y expresiones;
  - el módulo principal y el handler de la Etapa 4;
  - los testers Sem2 y sus logros;
  - el boceto de `Entorno`;
  - la jerarquía completa de `Tipo`;
  - `new System()` como error, que se detecta en la Etapa 4.

  El módulo principal de la Etapa 3 sigue siendo el main del jar.
- Los tipos raw en declaraciones (ADR 0001). No cambian: la Parte I §3 los
  admite.
- Mover la detección de parámetros repetidos a la construcción de la
  tabla. La Parte I la ubica en la primera pasada, pero el resultado
  observable no cambia.
- Los desvíos de nombres anotados en el diseño de la Etapa 3: los
  nombres "Pasada 1/2", que `consolidar()` también corra el control de
  declaraciones, "clase" usado también para interfaces, y "unidad actual".
  Quedan para después.
- La lectura literal del enunciado sobre `String`/`System` (no finales,
  `System` con constructor por defecto). Se siguió a Java (ADR 0004).

## Further Notes

- Documento de referencia: "Semántica de MiniJava – Parte I.
  Declaraciones" (2026). Ante lo no especificado, Java.
- Vocabulario: el glosario del proyecto (**Clave**, **Signatura**,
  **Redefinición**, **Contrato de interfaz**, **Tipo predefinido**,
  **Constructor por defecto**, **Herencia sin argumento**, **Parámetro de
  tipo de método**, **Herencia múltiple de interfaces**).
- Decisiones registradas:
  - ADR 0004: las predefinidas siguen a Java.
  - ADR 0005: la herencia múltiple es sólo entre interfaces.
  - D1–D7 del plan de corrección de la Etapa 3.
  - La sección "Decisiones: tipos predefinidos, clave y redefinición" del
    diseño de la Etapa 3.
- Riesgo principal: el paso 4 (clave por aridad) es el que más
  probablemente cambie casos existentes. El paso 1 toca la gramática y diez
  casos, y por eso va primero.
- Si la cátedra confirma que una clase puede implementar varias
  interfaces, o que las predefinidas siguen la lectura literal, los ADR
  correspondientes explican cómo revertir.

## Tickets

En `0001-correccion-etapa3/tickets/`, uno por archivo. Los casos de prueba
ya están escritos: cada ticket termina cuando sus casos pasan y no se rompe
ninguno de los que ya pasaban.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Línea de base | — | hecho |
| 02 | `implements` de una sola interfaz (ADR 0005) | 01 | hecho |
| 03 | Herencia como `Tipo`, con argumentos validados | 02 | ready-for-agent |
| 04 | Tipos predefinidos y `Object` implícito | 01 | hecho |
| 05 | Relación con el tipo equivocado | 03, 04 | ready-for-agent |
| 06 | Clave (nombre, aridad) | 01 | ready-for-agent |
| 07 | Constructores | 04 | ready-for-agent |
| 08 | Prefactor de `Tipo` | 01 | hecho |
| 09 | Redefinición exacta y estáticos | 04, 06, 08 | ready-for-agent |
| 10 | Instanciación genérica en la redefinición | 03, 09 | ready-for-agent |
| 11 | Contratos de interfaz e interfaz contra `Object` | 10 | ready-for-agent |
| 12 | Cierre de documentación | 02–11 | ready-for-agent |

Se pueden empezar ya: 02, 04, 06 y 08.
