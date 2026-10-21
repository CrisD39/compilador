# Propuesta de Casos de Prueba — Analizador Semántico (Etapa 3)

> Estado: documento de propuesta, para discutir antes de implementar. Sigue el
> mismo criterio que `analizador-sintactico.md` ("este documento fija el
> contrato antes de terminar la implementación"): acá se decide **qué** se va
> a probar y **con qué oráculo**, antes de escribir `AnalizadorSemanticoImpl`.
> No modifica nada del código ni de `analizador-semantico.md`; donde hace
> falta una decisión que hoy no está tomada, queda marcada como pregunta
> abierta en vez de asumida.

## 0. Punto de partida: qué hay hoy y qué bloquea poder testear

El esqueleto de modelo ya existe (`Atributo`, `Chequeable`, `Constructor`,
`Interfaz`, `Metodo`, `Parametro`, `TablaSimbolos`, `Tipo`, `Clase`, todos en
`Código/src/Model/`), pero es un primer corte sin terminar. Antes de que
cualquier caso de los propuestos acá pueda correr, hace falta resolver (esto
es trabajo de implementación, no de diseño de tests — lo dejo anotado para que
se tenga en cuenta al planificar, no lo toco acá):

- `TablaSimbolos.estaBienDeclarada()` itera `clases` (un
  `HashMap<String, Clase>`) con `for (Clase c : clases)` — un `HashMap` no es
  `Iterable<Clase>`, no compila tal cual está.
- `Clase.estaBienDeclarado()` tiene el mismo problema con `metodos`
  (`HashMap<String, Metodo>`) y `atributos` (`HashMap<String, Atributo>`) —
  literalmente comentado `//No es iterable` en el código. Además itera
  `constructores` como `List<Constructor>`, pero el campo está declarado
  `List<Metodo>` (`agregarConstructor(Metodo constructor)` también recibe
  `Metodo`, no `Constructor`) — hay una inconsistencia de tipos entre
  `Constructor` (clase aparte) y cómo `Clase` guarda los constructores.
- `Atributo` y `Parametro` guardan el tipo como `Token` crudo
  (`private Token tipo`), no como `Tipo` — contradice directamente la sección
  "Por qué un `Token` no alcanza" del propio `analizador-semantico.md`
  (genéricos, arreglos y referencias sin resolver no entran en un `Token`).
- `Interfaz` no implementa `Chequeable`, no tiene getters, y
  `TablaSimbolos.insertarClase(Clase clase)` no acepta un `Interfaz` — pero el
  diseño documentado (Pasada 1 del EDT) dice `tabla.insertarClase(i)` con `i`
  una `Interfaz`, "mismo espacio de nombres que las clases".
- `TablaSimbolos` no tiene `buscarClase(String)`, que el EDT da por existente
  para resolver `idClase` en `<TipoReferencia>`/`<InstanciadoOParametrico>`.
- `Constructor.java` tiene un `import java.awt.*` sin usar y usa `List` sin
  importar `java.util.List`.

**Implicancia para este documento**: las secciones 3 y 5 están escritas contra
el *diseño* (el EDT de `analizador-semantico.md`), no contra el código actual.
Van a servir de contrato una vez que el modelo compile y tenga la forma que el
EDT ya supone (`Tipo` resuelto, `buscarClase`, `Interfaz` como `Chequeable`
insertable en la misma tabla).

## 1. Metodología de testing propuesta

Mismo criterio de "testing inteligente" que ya usan los testers de léxico y
sintáctico, adaptado a que acá hay más superficie (no es solo "aceptar o
rechazar una cadena", sino también *qué objetos quedaron en la tabla de
símbolos* después de un análisis válido):

- **Cobertura por regla del EDT**: cada acción semántica de
  "Acciones semánticas sobre la gramática original" (`analizador-semantico.md`)
  tiene al menos un caso positivo y, donde aplique un `error(...)`, al menos un
  caso negativo que lo dispare.
- **Partición de equivalencia sobre la jerarquía `Tipo`**: primitivo/primitivo,
  primitivo/referencia, referencia/referencia (con y sin subtipado),
  genérico/genérico, arreglo/arreglo (igual y distinta dimensión), `null`
  contra cada categoría — en vez de probar "todos los tipos contra todos",
  alcanza con un representante por combinación de categorías.
- **Casos límite**: 0 clases en el archivo, 0 miembros en una clase, cadena de
  herencia de longitud 1/2/N, genéricos anidados a 1/2/3 niveles, N clases con
  el mismo nombre (no solo 2).
- **Multi-error (Logro 4) como eje transversal**: todo caso negativo de la
  sección 3 debería tener, además de su versión aislada, al menos una variante
  combinada con otro error independiente en el mismo archivo, para confirmar
  que el análisis no corta en el primero — mismo rol que cumple
  `TesterSintacticoModoPanico` para el sintáctico.
- **Casos de regresión atados a decisiones de diseño ya escritas**, para que
  si alguien las cambia sin querer un test lo note: las dos pasadas sobre
  `<ListaClases>` (referencias hacia adelante), `null` no asignable a
  primitivo, el scope de `idGen` limitado a la clase/método que lo declaró.
- **Reuso del patrón de harness ya probado** en vez de inventar uno nuevo (ver
  sección 2): oráculo en comentario dentro del propio archivo de caso,
  carpetas separadas por resultado esperado, tester parametrizado para el caso
  general y un tester aparte no parametrizado para lo que un harness
  parametrizado no puede expresar (igual rol que
  `TesterSintacticoModoPanico`).

## 2. Infraestructura de test propuesta (espejo del patrón sintáctico)

Carpetas propuestas, paralelas a `resources/sintactico/`:

```
resources/semantico/
├── sinErrores/     (análisis válido; además de "no hay error" hay que poder
│                    afirmar algo sobre la TablaSimbolos resultante)
├── conErrores/     (un error semántico esperado, igual mecánica que
│                    TesterSintacticoDeCasosConErrores)
└── multiError/     (Logro 4: varios errores independientes en una corrida,
                     igual rol que resources/sintactico/panico/)
```

**Decisión tomada — formato del oráculo de error: etiqueta de la cátedra
`[Error:<lexema>|<línea>]`** (revisada el 2026-10-16). Las pautas de la
cátedra (`enunciados/Pautas Etapa 1-6.pdf`) piden que **todo** error, también
el semántico, se reporte como:

```
Error Semántico en línea <L>: <descripción del error>
[Error:<lexema>|<L>]
```

Por eso la primera línea de cada caso usa la misma convención de tres barras
que el léxico y el sintáctico (`///[...]`, sin espacio, ver `sintError44.java`
y siguientes):

```
///[Error:<lexema>|<línea>]
```

Ejemplo real, ver `resources/semantico/conErrores/semError01.java`:
`///[Error:A1|5]`. El lexema es el nombre de la entidad que dispara el error
(tabla de abajo). Lexema y línea salen del **mismo** token: por eso
`ERR_TIPO_NO_DECLARADO` en `extends` usa la línea del token del padre, no la
del nombre de la clase.

Los códigos fijos por regla violada (`ERR_HERENCIA_CICLICA`,
`ERR_TIPO_NO_DECLARADO`, ...) **se mantienen como categoría interna** de
`ErrorSemantico`. Eligen la descripción legible (`getDescripcion()`), aparecen
en los comentarios de cada caso y en las tablas de este documento, pero no van
en la etiqueta. Antes la etiqueta era `///[ErrorSem:<CODIGO>|<línea>]`, porque
distinguía la categoría aunque dos errores cayeran sobre el mismo token. Se
abandonó por no respetar el formato de la cátedra. Los 53 casos de
`conErrores/` se migraron conservando la línea esperada y tomando el lexema que
reportaba cada error.

| Categoría (`ERR_*`) | Lexema en la etiqueta |
|---|---|
| `CLASE_DUPLICADA`, `HERENCIA_CICLICA`, `HERENCIA_CLASE_FINAL`, `HERENCIA_NO_PERMITIDA`, `HERENCIA_PERMITIDA_SIN_MODIFICADOR`, `NONSEALED_SIN_PADRE_SEALED` | nombre de la clase/interfaz que se declara |
| `TIPO_NO_DECLARADO`, `TIPO_NO_GENERICO`, `TIPO_GENERICO_NO_DECLARADO` | el nombre del tipo usado (en `extends`, `implements`, atributo, parámetro, retorno, argumento de tipo, `permits`) |
| `HERENCIA_TIPO_INCORRECTO` | el tipo nombrado en `extends`/`implements` que no es de la categoría esperada (una interfaz en el `extends` de una clase, una clase en `implements` o en el `extends` de una interfaz) |
| `INTERFAZ_DUPLICADA` | la interfaz repetida en el `extends` de una interfaz |
| `PERMITS_NO_ES_SUBTIPO` | el nombre listado en `permits` |
| `ATRIBUTO_DUPLICADO`, `PARAMETRO_DUPLICADO` | nombre del atributo/parámetro |
| `METODO_DUPLICADO`, `METODO_REDEFINE_FINAL`, `REDEFINE_ESTATICO`, `REDEFINICION_INCOMPATIBLE`, `METODO_INTERFAZ_NOIMPLEMENTADO`, `METODO_RETORNO_INCOMPATIBLE` | nombre del método (cuál de los dos, ver la tabla de códigos de 5.6) |
| `CONSTRUCTOR_DUPLICADO` | nombre del constructor (= nombre de la clase) |
| `CONSTRUCTOR_NOMBRE_INVALIDO` | el nombre escrito en el constructor (distinto del de la clase) |

`ERR_INTERFAZ_NO_DECLARADA` **se retiró** (spec 0001, D7): un supertipo
inexistente en `extends` o `implements` se reporta una sola vez, como
`ERR_TIPO_NO_DECLARADO`, en el control de declaraciones, y la consolidación
saltea la relación sin volver a reportarla. Ningún caso lo usaba. El detalle
de los códigos que agregó la spec está en 5.6.

Para **sinErrores**, además de `[SinErrores]`, conviene que el tester pueda
afirmar algo sobre la tabla resultante (cuántas clases quedaron, de qué tipo
quedó resuelto tal atributo) — a diferencia del léxico/sintáctico, que solo
afirman sobre la secuencia de tokens/errores impresa. Esto puede necesitar que
`ModuloPrincipalET3` exponga la `TablaSimbolos` final además de la lista de
errores (mismo tipo de decisión que ya se tomó para `getErrores()` en el
sintáctico).

## 3. Casos de prueba — Chequeo de declaraciones

Organizados por la regla del EDT que cada uno ejercita. Los fragmentos son
MiniJava mínimos, no archivos completos.

### 3.1 Registro de clases/interfaces (Pasada 1)

- **Positivo**: `class A{} class B{}` — dos nombres distintos, ambos quedan en
  `tabla`.
- **Negativo**: `class A{} class A{}` — nombre de clase duplicado. Caso
  concreto ya escrito: `conErrores/semError07.java`. Ver sección 3.5 para la
  convención de reporte (sobre la segunda declaración) y el resto de los
  casos de declaración repetida.
- **Negativo**: `class A{} interface A{}` — mismo espacio de nombres entre
  clase e interfaz (asumiendo que se resuelve el gap de la sección 0 que hoy
  se lo impide a nivel de tipos). Caso concreto: `conErrores/semError08.java`.
- **Límite**: archivo con cero clases (`eof` solo) — tabla vacía, análisis
  trivialmente válido (ningún REQ lo prohíbe).
- **Límite**: N clases (≥10) con nombres únicos — confirma que no hay un tope
  arbitrario de tamaño de tabla.

### 3.2 Resolución de `<TipoReferencia>` / `idClase` (Pasada 2)

- **Positivo — referencia hacia adelante**: `class A{ B b; } class B{}` — el
  atributo de `A` usa un tipo declarado más abajo en el archivo; válido
  gracias a las dos pasadas.
- **Positivo — tipo recursivo**: `class Nodo{ Nodo siguiente; }` — una clase
  que se referencia a sí misma.
- **Negativo**: `class A{ Foo f; }` sin que exista `Foo` en ningún lado —
  "clase no declarada", apuntando a la línea de `Foo`. **Hecho** —
  `conErrores/semError15.java`, `ERR_TIPO_NO_DECLARADO`, implementado en
  `Atributo.estaBienDeclarado`.
- **Negativo**: `class A extends Foo{}` con `Foo` no declarado. **Hecho** —
  `conErrores/semError04.java` (y `semError84.java` para `implements`),
  `ERR_TIPO_NO_DECLARADO` sobre `Foo`, reportado una sola vez en el control
  de declaraciones (D7, ver 5.6).
- **Negativo — mismo chequeo en un parámetro**: un método con un parámetro de
  tipo no declarado (`void m(Foo f){}`). **Hecho** —
  `conErrores/semError16.java`, `ERR_TIPO_NO_DECLARADO`, implementado en
  `Parametro.estaBienDeclarado`, invocado ahora desde
  `Metodo.estaBienDeclarado`/`Constructor.estaBienDeclarado` (antes no se
  llamaba desde ningún lado).
- **Negativo**: `class A{ Caja<Foo> c; }` con `Caja` existente pero `Foo` (el
  argumento genérico) no — el error debe apuntar al tipo anidado, no solo
  confirmar que `Caja` existe. **Hecho** con el Logro 5 (`Tipo` recursivo,
  ver 5.5): `conErrores/semError48.java`. Desde la spec 0001 (D2) vale
  también para el argumento de `extends`/`implements` (`semError68.java`).

### 3.3 Tipo genérico (`idGen`) y su scope — **Hecho**

Implementado: `Clase`/`Interfaz.parametrosTipo` + `Metodo.parametrosTipoPropios`,
con `Chequeable.estaBienDeclarado(TablaSimbolos, Map<String, OrigenParametroTipo>)`
propagando el entorno de `idGen` vigente: cada nombre apunta a la clase,
interfaz o método que lo declara, y `Tipo.estaBienDeclarado` le deja ese
**origen** a cada `idGen` en alcance (spec 0001, ticket 08). Nuevo error
`ERR_TIPO_GENERICO_NO_DECLARADO`.

- **Positivo**: `class Caja<T>{ T contenido; T obtener(){ return contenido; } }`
  — `T` usado dentro de la clase que lo declaró (atributo y retorno). Ver
  `resources/sintactico/sinErrores/sintCorrecto23.java`.
- **Negativo**: `class Foo{ T x; }` sin `<T>` en la declaración de `Foo` —
  "tipo genérico no declarado en este contexto". Ver
  `resources/semantico/conErrores/semError41.java` (parámetro) y
  `semError38.java` (retorno).
- **Negativo**: fuga de scope entre clases —
  `class Caja1<T>{} class Caja2{ T x; }` (la `T` de `Caja1` no existe en
  `Caja2`). Ver `resources/semantico/conErrores/semError39.java`.
- **Negativo**: fuga de scope entre métodos de la misma clase — el `T` propio
  de un método no está vigente en otro método hermano. Ver
  `resources/semantico/conErrores/semError40.java`.
- **Límite**: dos clases distintas, cada una con su propio `<T>` homónimo
  (`class A<T>{} class B<T>{}`) — scopes independientes, no debe haber
  confusión entre el `T` de una y el de la otra (cada `Clase.estaBienDeclarado`
  construye su propio entorno, no hay estado compartido entre entidades, y
  cada `T` queda con su propio origen).
- **Shadowing (decisión tomada, ver sección 5.3)**: permitido sin error. Si un
  método redeclara el mismo nombre que su clase/interfaz contenedora
  (`class Caja<T>{ <T> T metodo(T x){...} }`), el del método pisa al de la
  clase en el entorno vigente para su signatura, como en Java: esos `T`
  tienen origen `metodo`. Ver
  `resources/semantico/sinErrores/semCorrecto17.java`.

### 3.4 Jerarquía `Tipo` — compatibilidad

(Aplica ya a inicializadores de atributo, `REQ-AS-011`, aun antes de que exista
chequeo de sentencias.)

- Primitivo con el mismo primitivo: `int x = 0;`-equivalente a nivel atributo
  (`int x = 5;`) — válido.
- Primitivo con otro primitivo: `boolean b = 1;`-equivalente — inválido (sin
  conversión implícita entre `int`/`char`/`boolean`; **pregunta abierta**: si
  la cátedra pide alguna promoción numérica puntual, falta confirmarla).
- Referencia con subtipo: `class A{} class B extends A{} class C{ A a = new B(); }`
  — válido por subtipado.
- Referencia con supertipo (sentido inverso): `A a; B b = a;`-equivalente a
  nivel atributo — inválido sin cast explícito.
- `null` a una referencia: `class A{} class C{ A a = null; }` — válido.
- `null` a un primitivo: `int x = null;`-equivalente — inválido.
- Arreglos con igual tipo base e igual dimensión: `int[] a = new int[]{1,2};`
  — válido.
- Arreglos con distinta dimensión: `int[] a = new int[][]{{1}};`-equivalente
  (si lo deja pasar el sintáctico) — inválido en semántico (`int[]` ≠
  `int[][]`).
- Genéricos instanciados con distinto argumento: `Lista<Item> a = new Lista<Otro>();`
  — inválido aunque compartan la misma clase base (`Lista`).

### 3.5 Declaraciones repetidas (clases, miembros, parámetros)

Todas las categorías de "nombre repetido" del lenguaje que son **declaración**
en el sentido de "Organización" de `analizador-semantico.md` (clase, atributo,
método, constructor, parámetro) — no solo dentro de una clase, la misma regla
general ("¿dos declaraciones compiten por el mismo nombre en el mismo scope?")
aplica en capas anidadas: archivo → clase → lista de parámetros. Casos ya
bajados a archivos concretos en
`Código/resources/semantico/{sinErrores,conErrores,multiError}/` e
implementados: `TablaSimbolos.insertarClase`, `Clase.agregarAtributo`/
`agregarMetodo`/`agregarConstructor`, `Metodo`/`Constructor.estaBienDeclarado`.

**Fuera de alcance de esta sección — variables locales.** "¿Dos variables
locales compiten por el mismo nombre en el mismo scope?" es la misma pregunta
general, pero una variable local se declara dentro de un `<Bloque>`, que es
**sentencia**, no declaración (ver "Organización" en `analizador-semantico.md`:
"Los controles de variables locales deben realizarse en el chequeo de
sentencias"). Por eso **no hay, ni va a haber en esta etapa**, casos de
prueba de `ERR_VARIABLE_LOCAL_DUPLICADA` — los 5 negativos y los 3 positivos
de control que se habían bajado a archivo (variable repetida en el mismo
bloque, en un bloque anidado, tapando un parámetro, en la variable de un
`for`, en `int x, x;`, más los positivos de bloques hermanos y shadowing de
atributo) se borraron de
`Código/resources/semantico/{conErrores,sinErrores}/` porque necesitan un
`Entorno`/pila de scopes que recorra el `Bloque` ya construido (ver "Chequeo
de sentencias" en `analizador-semantico.md`), que todavía no existe. Van a
volver a bajarse a archivo cuando se diseñe esa etapa, no antes.

**Convención de reporte, común a toda la sección**: el error se marca sobre la
línea de la **segunda** declaración (la que "ya existe" cuando se intenta
registrar) — a diferencia de la convención de herencia circular (3.6), acá no
hace falta recorrer ningún grafo: alcanza con insertar en un mapa por scope y
fallar en el segundo intento con el mismo nombre, así que no depende de que
`TablaSimbolos` sea `LinkedHashMap` (ese requisito es específico del chequeo
de ciclos).

**Qué SÍ es una declaración repetida (casos negativos):**

| Archivo | Caso | Código |
| --- | --- | --- |
| `conErrores/semError07.java` | dos clases con el mismo nombre | `ERR_CLASE_DUPLICADA` |
| `conErrores/semError08.java` | clase e interfaz con el mismo nombre (mismo namespace) | `ERR_CLASE_DUPLICADA` |
| `conErrores/semError09.java` | dos atributos con el mismo nombre en una clase | `ERR_ATRIBUTO_DUPLICADO` |
| `conErrores/semError10.java` | dos métodos con el mismo nombre **y la misma firma** | `ERR_METODO_DUPLICADO` |
| `conErrores/semError11.java` | dos constructores con la misma lista de parámetros | `ERR_CONSTRUCTOR_DUPLICADO` |
| `conErrores/semError12.java` | dos parámetros con el mismo nombre en `<ArgsFormales>` de un método | `ERR_PARAMETRO_DUPLICADO` |
| `conErrores/semError13.java` | mismo caso que 12, pero en `<ArgsFormales>` de un constructor | `ERR_PARAMETRO_DUPLICADO` |
| `multiError/semMultiErrorVariasDeclaracionesRepetidas.java` | clase + atributo + parámetro duplicados en un mismo archivo, categorías distintas (Logro 4) | `ERR_CLASE_DUPLICADA`, `ERR_ATRIBUTO_DUPLICADO` y `ERR_PARAMETRO_DUPLICADO`, uno por categoría |

Esta sección no tiene (por ahora) un positivo de control propio: los tres que
había (`semCorrecto04-06`) eran justamente los casos "no es error porque son
scopes distintos" para variables locales, y se borraron junto con sus
negativos (`semError14-18`) — ver el aviso de "Fuera de alcance" arriba. Un
positivo de control para, por ejemplo, "mismo nombre de atributo en clases
distintas no es error" queda pendiente de bajar a archivo si hace falta.

**Caso nuevo, fuera de la tabla de arriba — atributo redeclarado por
herencia.** No estaba contemplado en esta sección (que solo cubre colisiones
dentro del mismo scope de declaración) ni en 3.6 (que cubre herencia pero solo
para ciclos/tipo no declarado): una subclase que redeclara un atributo con el
mismo nombre que uno heredado (`class Base{ int x; } class Derivada extends
Base{ int x; }`) no reportaba nada — `Clase.consolidarConPadre` tenía un
`//lanzar error` sin implementar. **Hecho** — `conErrores/semError17.java`,
`ERR_ATRIBUTO_DUPLICADO`. Convención elegida (no había una ya decidida para
este caso): se reporta sobre la línea de la declaración **propia de la
subclase** (no la heredada del padre), porque es la línea que hay que
corregir.

**Preguntas abiertas que quedan** (no bajadas a archivo porque el oráculo
depende de una decisión que todavía no está tomada):

- ~~**Regla exacta de sobrecarga**~~ — **resuelta** por la clave (nombre,
  aridad) de la Parte I (spec 0001, ticket 06; ver 5.6): sólo se sobrecarga
  por cantidad de parámetros. `imprimir(int)` + `imprimir(char)` es
  `ERR_METODO_DUPLICADO` (`semError56`), igual que dos constructores de la
  misma aridad (`semError76`); `mostrar()` + `mostrar(int)` es válido
  (`semCorrecto27`).
- **Atributo y método con el mismo nombre** en la misma clase — ¿conviven en
  namespaces separados (como en Java) o es error acá? Sigue sin respuesta.
- **Parámetro de tipo genérico (`idGen` de `<GenericidadOpcional>`) vs. nombre
  de miembro** — p. ej. `class Caja<T>{ int T; }`: en Java conviven (son
  namespaces distintos, tipos vs. variables), pero no se confirmó si este
  proyecto sigue el mismo criterio. No bajado a archivo todavía.

### 3.6 Herencia (`extends`/`implements`) — chequeo de corrección básico

(Corre después de armar toda la `TablaSimbolos`, ver "TODO — Chequeo de
corrección" en `analizador-semantico.md`.)

**Casos ya bajados a archivos concretos** en
`Código/resources/semantico/{sinErrores,conErrores,multiError}/` (ver índice
abajo). Decisiones de diseño que fijan el oráculo de estos casos:

- **Un solo error por ciclo, no uno por clase involucrada.** Como
  `TablaSimbolos.estaBienDeclarada()` recorre todas las clases y llama
  `c.estaBienDeclarado()` en cada una, una implementación ingenua detectaría
  el mismo ciclo una vez por cada clase que lo integra (en `A→B→C→A`,
  reportaría 3 veces el "mismo" error). Se decidió deduplicar: el ciclo se
  detecta una única vez y las clases involucradas se marcan como inválidas sin
  volver a reportarlo — mismo espíritu que la deduplicación ya aplicada en el
  modo pánico del sintáctico.
- **Convención de qué clase "cierra" el ciclo (y por lo tanto en qué línea se
  reporta).** Recorriendo el grafo en **orden de declaración** (requiere que
  `TablaSimbolos.clases` sea un `LinkedHashMap`, no un `HashMap` — con
  `HashMap` el orden de iteración no está garantizado y la línea reportada
  dejaría de ser determinística, rompiendo el test), arrancando por la primera
  clase del ciclo que aparece en el archivo y siguiendo la cadena `extends`: el
  error se reporta sobre la **última clase visitada antes de volver a
  encontrar una ya vista en el recorrido actual** — la que, al seguir
  cerrando la cadena, apunta de nuevo hacia atrás. Es una nota de
  implementación, no solo de test: sin el `LinkedHashMap` la línea exacta no
  se puede afirmar de forma estable.
- **Distinguir "ciclo" de "tipo no declarado".** Si al caminar la cadena el
  padre de una clase no existe en la tabla (no es que ya se haya visitado, es
  que no está en ningún lado), no es un ciclo — es un `ERR_TIPO_NO_DECLARADO`
  normal (sección 3.2). Una implementación que no distinga los dos casos de
  corte ("ya visitado" vs. "no existe") puede reportar el error equivocado o,
  en el peor caso, colgarse si asume que siempre va a encontrar un padre.
- **La recursión por tipo de ATRIBUTO no es un ciclo de herencia.** Una clase
  con un atributo de su propio tipo (`class Nodo{ Nodo siguiente; }`) o dos
  clases que se referencian mutuamente por atributo (no por `extends`) son
  válidas — el grafo que hay que chequear por ciclos es el de
  `extends`/`implements`, no el de tipos de atributo. Casos de control
  dedicados a esto en `sinErrores/` para que una implementación que confunda
  ambos grafos falle un test enseguida.
- **El chequeo también aplica a `Interfaz.herencias`**, no solo a
  `Clase.herencia` — son dos campos/objetos distintos en el modelo actual, y
  conviene un caso dedicado (`semError06.java`) para no asumir que cubrir uno
  cubre el otro.

Índice de archivos (`sem` = semántico, numeración independiente de la del
sintáctico):

| Archivo | Caso | Oráculo |
| --- | --- | --- |
| `sinErrores/semCorrecto01.java` | cadena de herencia lineal sana (A←B←C) | `[SinErrores]` |
| `sinErrores/semCorrecto02.java` | autoreferencia por atributo, no es ciclo | `[SinErrores]` |
| `sinErrores/semCorrecto03.java` | composición mutua por atributo entre dos clases, no es ciclo | `[SinErrores]` |
| `conErrores/semError01.java` | ciclo directo (`A extends A`) | `ERR_HERENCIA_CICLICA\|5` |
| `conErrores/semError02.java` | ciclo indirecto, longitud 2 | `ERR_HERENCIA_CICLICA\|7` |
| `conErrores/semError03.java` | ciclo indirecto, longitud 3 | `ERR_HERENCIA_CICLICA\|7` |
| `conErrores/semError04.java` | tipo no declarado en `extends` (control, no es ciclo) | `ERR_TIPO_NO_DECLARADO\|6` |
| `conErrores/semError05.java` | cadena rota a mitad de camino por tipo no declarado (robustez) | `ERR_TIPO_NO_DECLARADO\|9` |
| `conErrores/semError06.java` | ciclo entre interfaces (`extends` de `Interfaz`) | `ERR_HERENCIA_CICLICA\|7` |
| `multiError/semMultiErrorDosCiclosIndependientes.java` | dos ciclos independientes en el mismo archivo (Logro 4) | dos `ERR_HERENCIA_CICLICA` (líneas 10 y 12) |
| `multiError/semMultiErrorCicloMasClaseSana.java` | un ciclo + una clase sana sin relación | un `ERR_HERENCIA_CICLICA` (línea 7), `Sana` no afectada |
| `multiError/semMultiErrorCicloLargoNoCuelga.java` | ciclo grande (50 clases), guardrail de robustez | un único error, con `@Test(timeout = ...)` |

**Hecho** (spec 0001, D1): `class A implements Foo{}` con `Foo` una clase
da `ERR_HERENCIA_TIPO_INCORRECTO` sobre `Foo` (`semError66.java`), igual que
una clase que extiende una interfaz (`semError65.java`) y una interfaz que
extiende una clase (`semError67.java`). Ver 5.6.

## 4. Casos de prueba — Chequeo de sentencias (categorías propuestas, sin diseño previo)

`analizador-semantico.md` todavía no desarrolla esta parte (la nombra en
"Organización" pero el EDT solo cubre declaraciones). Antes de bajar esto a
casos concretos con oráculo exacto, conviene acordar las reglas — dejo acá
las categorías que parecen necesarias, a modo de lista de chequeo para esa
conversación, no como casos listos para implementar:

- **Asignación**: compatibilidad de tipos entre el lado izquierdo y el
  derecho (reusa la partición de 3.4).
- **Retorno**: el tipo de la expresión de `return` debe ser compatible con el
  tipo de retorno declarado; un método `void` no admite `return expr;`; un
  método no-`void` — **pregunta abierta**: ¿se exige que todo camino retorne,
  como en Java, o queda fuera de alcance de esta etapa?
- **Llamada a método/constructor**: cantidad y tipo de los argumentos
  actuales contra algún método/constructor declarado (depende de cómo se
  resuelva la sobrecarga, ver 3.5).
- **Acceso a variable**: todo `idMetVar` usado debe estar declarado en algún
  scope visible (parámetro, variable local, atributo propio o heredado).
- **Condición de `if`/`while`/`for`/ternario**: debe ser de tipo `boolean`.
- **Operadores binarios**: tipos de operandos compatibles con el operador
  (aritméticos solo entre `int`, `&&`/`||` solo entre `boolean`, `==`/`!=`
  entre tipos compatibles entre sí, comparaciones relacionales solo entre
  `int`, concatenación de `String` si existe como operador — a confirmar).
- **`this`**: válido solo dentro de un método/constructor de instancia, no en
  uno `static`.
- **Postfijo/prefijo (`++`/`--`)**: solo sobre algo asignable (variable,
  atributo, elemento de arreglo), no sobre un literal ni el resultado de una
  llamada — la gramática ya lo permite sintácticamente (`a++--` es válido
  sintáctico, ver "Postfijo" en `analizador-sintactico.md`); acá es donde se
  rechazaría semánticamente.

## 5. Casos de prueba por logro de Etapa 3

Para cada logro de `analizador-semantico.md` ("Logros (Etapa 3)"), indico si
hace falta extender la **gramática sintáctica** antes de que haya algo que
testear en semántico, y propongo categorías.

### 5.1 Logro 1 — `sealed` y `final`

**Gap sintáctico — resuelto, para `sealed`/`permits`/`nonsealed` (clase e
interfaz) y `final` (clase y método):** todos son palabras clave hoy
(`TokenType`/`TablaPalabrasClave`). `sealed`/`nonSealed` ahora se bifurcan
internamente entre clase e interfaz (`AnalizadorSintacticoImpl.sealed()` →
`sealedClase()`/`sealedInterfaz()`; `nonSealed()` →
`nonSealedClase()`/`nonSealedInterfaz()`; `permitidosSealedInterfaz()`/
`permitidosSealedRestoInterfaz()` duplican `permitidosSealed()`/
`permitidosSealedResto()` sobre `Interfaz` en vez de `Clase`, mismo
código). `final` (clase) sigue siendo alternativa separada al principio de
`<ListaClases>` (`finalClase()`) — como todas arrancan con una palabra
clave distinta, `sealed final class X` ni siquiera parsea, sin necesitar un
chequeo aparte que lo prohíba.

**Hecho — mensaje de error dedicado para modificador repetido/combinado**
(`errorModificadorRepetido()`, llamado desde `sealed()`/`nonSealed()`/
`finalClase()` justo después de consumir el primer modificador, si lo que
sigue es OTRO de `sealed`/`nonsealed`/`final`): antes, `sealed sealed
class X` o `final sealed final class X` caían en el `match(PR_CLASS)`
genérico, que fallaba con un mensaje que no apuntaba al problema real
("se esperaba class, se encontró sealed") y además disparaba
`sincronizar()` — el modo pánico saltaba hasta el próximo `{`, comiéndose
toda la declaración real (`class X permits Y`, etc.) como si fuera
basura. Ahora se reporta un único error claro y **no** se llama a
`sincronizar()`: el modificador sobrante queda sin consumir, así que la
recursión de `listaClases()` lo vuelve a mirar como un modificador nuevo
en la próxima vuelta y la declaración real se termina parseando bien
(efecto práctico: cada modificador de más cuesta exactamente un error,
no un párrafo de errores encadenados sin sentido).
`conErrores/sintError57.java` (`sealed` repetido), `sintError58.java`
(`final` repetido), `sintError59.java` (`nonsealed` repetido),
`sintError60.java` (combinación de los tres,
`final sealed final class X`).

`final` en método se agregó como
`<FinalOpcional>` entre `<Visibilidad>` y `<CuerpoMiembro>`
(`miembro()`/`finalOpcional()`), threadeado hasta `crearMetodo()`; en
atributo o constructor es **error sintáctico** explícito (`restoMiembro()`/
`trasIdClaseMiembro()`), porque el logro dice que `final` solo aplica a
clase/método (interfaz: ver "Decisión" abajo).

**Decisión — `final` en interfaz: NO se implementa.** El enunciado original
del logro lo pedía, pero Java real no admite `final interface` (contradice
el propósito de una interfaz: existe para ser implementada). Se sacó del
todo de la gramática — `final interface X {}` cae en el error sintáctico
normal de `clase()` esperando `"class"` y encontrando `"interface"`, sin
caso especial. Esto cierra, con una respuesta negativa, la pregunta abierta
de la sección 6 punto 7 sobre qué significa `final` en interfaz.

**Hecho** (`Clase.consolidarHerencia`/`consolidarInterfaces`/`consolidarConPadre`,
y su espejo en `Interfaz.consolidarHerencia` reusando los mismos códigos de
error — igual criterio que ya comparten `ERR_HERENCIA_CICLICA`/
`ERR_TIPO_NO_DECLARADO` entre clase e interfaz, ahora extendido también
entre `extends`/`implements`):
- `sealed` en clase/interfaz + `permits`: una subclase/subinterfaz/clase que
  implementa, directa, que no figura en la lista → `ERR_HERENCIA_NO_PERMITIDA`.
  `conErrores/semError18.java` (clase extiende clase), `semError24.java`
  (interfaz extiende interfaz), `semError27.java` (clase implementa
  interfaz).
- `nonsealed` sin ningún supertipo directo `sealed` real (ni `extends` ni
  `implements`) → `ERR_NONSEALED_SIN_PADRE_SEALED`.
  `conErrores/semError19.java`/`semError20.java` (clase, sin `extends` /
  padre no `sealed`), `semError26.java` (interfaz, sin `extends`),
  `semError29.java` (clase que implementa una interfaz no `sealed`).
  `sinErrores/semCorrecto09.java`: positivo de control — una clase
  `nonsealed` **solo** por `implements` (sin ningún `extends`) no debe
  dispararlo, porque alcanza con un supertipo directo `sealed` por
  cualquiera de los dos lados.
- **Exhaustividad de `permits`** (regla real de Java, ya implementada: toda
  subclase/subinterfaz/implementación directa permitida debe declararse
  `final` (solo clases), `sealed` o `nonsealed`, nunca `class`/`interface`
  liso): si no, `ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR`.
  `conErrores/semError23.java` (clase extiende clase lisa), `semError25.java`
  (interfaz extiende interfaz lisa), `semError28.java` (clase implementa
  interfaz lisa).
  `sinErrores/semCorrecto04.java` (clase permitida declarada `final`),
  `semCorrecto05.java` (clase permitida declarada `nonsealed`, caso de
  control de un solo nivel — un nieto de la `sealed` vía esa `nonsealed`
  queda libre), `semCorrecto06.java` (clase permitida declarada `sealed` a
  su vez, con su propio `permits` anidado), `semCorrecto07.java` (mismo
  patrón que `semCorrecto05` pero entre interfaces), `semCorrecto08.java`
  (clase permitida declarada `final` al **implementar**, no al extender).
- Extender una clase `final` → `ERR_HERENCIA_CLASE_FINAL`, sin excepción
  (no hay `permits` que valga; no aplica a interfaces porque `final
  interface` no existe). `conErrores/semError21.java`.
- Redefinir (misma firma) un método heredado `final` →
  `ERR_METODO_REDEFINE_FINAL` — chequeo de firmas en la fusión de herencia,
  no toca cuerpo/sentencias de método (esa etapa sigue sin implementar y no
  hace falta para esto). `conErrores/semError22.java`.

**Hecho — wiring de `implements`:** `herenciaOpcional()` ahora captura la
interfaz y la guarda en la clase (antes era un `TODO`, `Clase.interfaces` se
inicializaba pero nada lo poblaba — así que `consolidarInterfaces()` nunca
iteraba nada en ningún test existente). Hoy es `Clase.setInterfaz(Tipo)`:
una sola interfaz (ADR 0005), guardada como `Tipo` (D2).
Detalle de implementación: el chequeo de `nonsealed` se movió de "inmediato
al resolver `extends`" a "al final de `consolidar()`, después de
`consolidarHerencia` **y** `consolidarInterfaces`" — vía un campo
`tieneSupertipoSellado` que cualquiera de los dos marca, para no dar un
falso `ERR_NONSEALED_SIN_PADRE_SEALED` cuando el único supertipo `sealed`
llega por `implements` (ver `semCorrecto09.java`).

**Corrección sobre una afirmación anterior de este documento** (histórica: el
gap se cerró, ver 5.2): este wiring NO activó `ERR_METODO_INTERFAZ_NOIMPLEMENTADO` (como se dijo en una vuelta
previa) — verificado con un caso concreto, sigue sin disparar nunca. La
razón: `Interfaz.metodos` tampoco se puebla nunca (`metodoInterfaz()` sigue
siendo puramente sintáctico, ver el `TODO` en `Interfaz.estaBienDeclarado`),
así que el loop de `Clase.consolidarInterfaces()` que lo chequea siempre
itera sobre una colección vacía. Es un gap preexistente **distinto** del de
`implements`, sin relación con `sealed` — queda documentado en 5.2 (más
abajo) para cuando se diseñen los casos de conflicto de métodos entre
interfaces, que lo necesitan igual.

**Hecho — validación de nombres en `permits`** (`Clase`/`Interfaz.
validarPermitidos`, llamado al final de `consolidar()`, un solo nivel igual
que el resto de los chequeos de `sealed`): cada nombre en `permits` tiene
que ser un subtipo directo real. Dos códigos, igual que `javac` distingue
"cannot find symbol" de "does not extend sealed class ... in permits
clause":
- Nombre no declarado en absoluto → `ERR_TIPO_NO_DECLARADO`.
  `conErrores/semError46.java` (clase).
- Nombre declarado pero que no extiende/implementa directamente →
  `ERR_PERMITS_NO_ES_SUBTIPO`. `conErrores/semError45.java` (clase),
  `semError47.java` (interfaz, satisfecho ni por `extends` de otra interfaz
  ni por `implements` de una clase).

**No implementado / decisión explícita de dejar fuera de alcance por ahora:**
- `sealed` en atributo/método (debería ser negativo — el logro dice que
  `sealed` "solo aplica a clase/interfaz"): no hay caso que probar porque la
  gramática ni siquiera deja escribir `sealed` ahí — no es FIRST de ningún
  `<CuerpoMiembro>`. Sigue pendiente si en algún momento se generaliza dónde
  puede aparecer `sealed` sintácticamente.

### 5.2 Logro 2 — herencia múltiple de interfaces

**Decisión (implementada, spec 0001 ticket 02):** el logro vale sólo entre
interfaces. `class X implements A, B` pasa a ser un error sintáctico, y los
casos que usan `implements` con lista (`sintCorrecto31`, `sintError52`,
`semCorrecto10`, `semCorrecto13`, `semCorrecto15`, `semCorrecto21`,
`semError30`, `semError33`, `semError36`, `semError44`) se reescriben con
una interfaz intermedia (`interface AB extends A, B` +
`class X implements AB`). Uno de ellos pasa a ser el caso de error
sintáctico. Ver `Documentación/adr/0005-herencia-multiple-solo-entre-interfaces.md`.
Lo que sigue es el historial del logro. Las notas **Hoy** dicen qué cambió
con la spec 0001.

**Gap sintáctico — resuelto.** `<HerenciaOpcional>`/`<ExtensionOpcional>`
ahora aceptan una lista de `<TipoReferencia>` separada por coma
(`<ListaInterfaces>`/`<ListaInterfacesResto>`, recursión a derecha —
`AnalizadorSintacticoImpl.listaInterfacesImplementadas()`/
`listaInterfacesExtendidas()` y sus `...Resto()`), tanto para `implements`
en una clase (`class X implements A, B`) como para `extends` en una
interfaz (`interface X extends A, B` — el campo `Interfaz.herencias`, que
ya existía pero nada lo poblaba, ahora sí). El caso que bloqueaba esto era
`sintError52.java` (no `sintError47.java`, que prueba algo distinto — que
`extends` e `implements` sigan siendo mutuamente excluyentes en la misma
clase, eso **no** cambió), reescrito para probar un caso que sigue siendo
inválido (coma final sin otro `<TipoReferencia>` después).
**Hoy** (ADR 0005): `herenciaOpcional()` acepta una sola interfaz tras
`implements` (`Clase.setInterfaz(tipoReferencia())`), y la lista queda sólo
en el `extends` de una interfaz (`listaInterfacesExtendidas()`/
`listaInterfacesExtendidasResto()`). `sintError52` es ahora el caso
sintáctico de `class Malo implements Uno, Dos` (`[Error:,|5]`).

**Hecho — semántica de `sealed`/`permits` entre múltiples padres**
(`Clase.consolidarInterfaces()`/`Interfaz.consolidarHerencia()`): cada
entrada de la lista se chequea de forma **independiente** contra su propio
`permits` — un problema con una entrada (no declarada, ciclo, no permitida)
no aborta el chequeo de las demás, mismo criterio de multi-detección que ya
regía para una sola. `conErrores/semError30.java` (clase implementa dos
`sealed`, permitida por una y no por la otra), `semError31.java` (interfaz
extiende dos, una `sealed` que no la permite). `sinErrores/semCorrecto10.java`
(clase implementa dos `sealed` que la permiten + una normal, las tres
satisfechas), `semCorrecto11.java` (interfaz extiende una `sealed` que la
permite + una normal, declarándose `nonsealed`). Esto cierra, de paso, el
único límite que quedaba del Logro 1: antes `sealed` en una interfaz solo
podía controlar una única clase/interfaz por el lado `implements`/`extends`
— ahora controla la lista completa, igual que en Java real.
**Hoy** la lista independiente es la del `extends` de una interfaz;
`semCorrecto10` y `semError30` pasan por una interfaz intermedia.

**Hecho — `Interfaz.metodos` ya se puebla.** `metodoInterfaz()` construía
antes solo sintaxis y descartaba todo; ahora arma el `Metodo` completo
(Estrategia B pura, sin placeholder — a diferencia de un método de clase,
`<MetodoInterfaz>` no tiene cuerpo que parsear después, así que no hace
falta el parcheo en dos pasadas vía `TablaSimbolos`) y lo registra con
`Interfaz.agregarMetodo(Metodo)`, que además ahora chequea duplicados
(`ERR_METODO_DUPLICADO`, mismo criterio que `Clase.agregarMetodo` — antes
tampoco existía este chequeo para interfaces; **hoy** por clave (nombre,
aridad), `semError77`). Esto activó, por fin,
`ERR_METODO_INTERFAZ_NOIMPLEMENTADO`: existía como código y como chequeo en
`Clase.consolidarInterfaces()` desde antes de esta sesión, pero nunca
disparaba porque iteraba sobre una colección siempre vacía.
`conErrores/semError32.java` (método sin implementar, por fin testeable),
`semError35.java` (dos métodos con la misma firma en una interfaz).
`sinErrores/semCorrecto12.java` (positivo, método sí implementado).

**Hecho — `extends A, A` (interfaz repetida): ya es error**, no se
deduplica en silencio. `Interfaz.agregarHerencia(Tipo)` compara la cabeza
por lexema antes de agregar (el `LinkedHashSet` nunca lo detectaba solo —
mismo motivo de siempre, `Token` no sobrescribe `equals()`/`hashCode()`) y
reporta `ERR_INTERFAZ_DUPLICADA` si ya estaba; `extends Comp<A>, Comp<B>`
también es repetida. `conErrores/semError33.java` (repetida no contigua),
`semError34.java`. El control en el `implements` de una clase
(`Clase.agregarInterfaz`) se retiró con el ADR 0005: con una sola interfaz
no hay repetición posible.

**Hecho — conflicto entre superinterfaces y contrato con otra signatura**
(estado actual, spec 0001 tickets 02, 06 y 11). Los métodos se indexan por
clave (nombre, aridad) y la signatura se compara aparte, con
`Metodo.mismaSignatura()` (tipos de los parámetros en orden y retorno, con
la instanciación genérica aplicada):
- **Conflicto entre superinterfaces.** Al consolidarse, una interfaz copia
  los métodos de cada superinterfaz (`Interfaz.heredarMetodosDe()`). Si dos
  superinterfaces traen la misma clave con distinta signatura, la interfaz
  hereda dos métodos incompatibles: `ERR_METODO_RETORNO_INCOMPATIBLE`
  ("se hereda con signaturas incompatibles") sobre el método de la
  superinterfaz posterior en la lista. Cubre tanto distinto retorno
  (`semError36`, `semError44`) como distintos tipos de parámetros
  (`semError62`, `m(int)` contra `m(char)`: con la clave por aridad ya no es
  sobrecarga). En un diamante llega el mismo método por los dos lados y no
  se compara. `sinErrores/semCorrecto13.java` (dos interfaces exigen la
  misma signatura — sin conflicto).
- **Contrato con otra signatura.** Si la clase tiene la clave pero con otra
  signatura, es `ERR_REDEFINICION_INCOMPATIBLE` sobre el método de la
  clase (`semError37`, `semError64`). Antes `semError37` daba
  `ERR_METODO_RETORNO_INCOMPATIBLE` sobre el método de la interfaz; su
  etiqueta se corrigió (ver 5.6). La regla completa del contrato está en
  5.6 y en "Decisiones: tipos predefinidos, clave y redefinición" de
  `analizador-semantico.md`.

**Hecho — arreglado el bug de sobrecarga real encontrado de paso** (sin
relación directa con `sealed`/interfaces, pero bloqueaba poder testear en
limpio la sobrecarga entre métodos de distintas interfaces de 5.2).
`crearMetodo()` construía el `Metodo` placeholder con `params=[]` y lo
registraba en `Clase.metodos` vía `agregarMetodo()` **antes** de que
`argsFormales()` terminara de parsear — `setParametrosAMetodoActual()`
parcheaba los parámetros reales recién después (Estrategia A, dos pasadas),
pero mutar `metodo.parametros` no reindexa la key ya puesta en el
`HashMap`. El chequeo de firma duplicada en `agregarMetodo()` siempre veía
`nombre()` sin parámetros, sin importar cuántos declarara el método — dos
sobrecargas legítimas (`mover(int)`/`mover(char)`) colisionaban como si
fueran el mismo método.

**Arreglo:** `crearMetodo()` ya no registra el método en la clase — solo
construye el placeholder y lo deja como `metodoActual`
(`tablaSimbolo.setMetodoActual`). El registro real se movió a un método
nuevo, `TablaSimbolos.agregarMetodoActualAClaseActual()`, llamado en cada
uno de los 3 call sites del parser **después** de
`setParametrosAMetodoActual()` — recién ahí la clave del método (entonces
`getFirma()`, hoy `getClave()`) ve los parámetros reales. Esto cierra, por fin, la pregunta abierta #3 de la sección 6
("sobrecarga con firma distinta"): la regla ya es la natural (distintos
parámetros = sobrecarga, nunca duplicado) y ahora funciona de verdad.
`sinErrores/semCorrecto14.java` (sobrecarga dentro de una sola clase),
`semCorrecto15.java` (el caso completo de 5.2 que antes no se podía probar
en limpio: dos interfaces con `mover()` de distinta firma, implementadas
ambas por la misma clase). **Revertido por la clave (nombre, aridad)**
(spec 0001, ticket 06): la sobrecarga es sólo por aridad, así que los dos
casos pasaron a `conErrores/semError86.java` y `semError87.java`.

### 5.3 Logro 3 — métodos genéricos

**Gap sintáctico — resuelto.** `<GenericidadOpcional>` ahora también se
parsea antes del tipo de retorno de un método (`static <T> ...`, o directo
con `<` sin `static` para un método de instancia) y en `<MetodoInterfaz>`.
Es la misma producción que la de clase/interfaz, con **un único parámetro
de tipo** (`REQ-AS-010`). Hubo una vuelta en la que se generalizó a lista
(`<T, U>`); se revirtió para respetar `REQ-AS-010`: `sintError48.java`
(`class Par<T, U>`) y `sintError61.java` (`<T, U> T metodo(...)`) son error
en la `,`. Ver `resources/sintactico/sinErrores/sintCorrecto33.java` a
`sintCorrecto37.java` y `resources/sintactico/conErrores/sintError61.java`/
`sintError62.java`.

Declaración + chequeo de firma — **hecho** (alcance de esta entrega, ver
sección 3.3):

- Método genérico en una clase no genérica — el `T` del método es válido solo
  dentro de su propia firma (`Metodo.parametrosTipoPropios`, sin depender del
  `entornoContenedor` de la clase). Ver `semCorrecto16.java`.
- Método genérico cuyo parámetro de tipo tiene el mismo nombre que el de la
  clase contenedora — **decisión tomada: shadowing permitido, sin error**
  (como en Java real). El entorno vigente para la firma del método es la
  unión clase+método; no se modela "cuál T es cuál" a este nivel de alcance.
  Ver `semCorrecto17.java`.
- Método `static` genérico — mismo mecanismo, solo que además estático. Ver
  `semCorrecto19.java`.

Override de un método genérico heredado — **hecho** (segunda entrega, sobre
la infraestructura de `parametrosTipoPropios` de arriba):

- La primera versión canonicalizaba por posición, como string, los `idGen`
  propios del método (`Metodo.getFirma()`/`getRetornoCanonico()`). La spec
  0001 la reemplazó (tickets 06, 08, 09 y 11; esos métodos ya no existen):
  la clave es `Metodo.getClave()` (nombre/aridad) y la signatura se compara
  con `Metodo.mismaSignatura(otro, vista)`, sobre `Tipo.equals()` (cabeza,
  argumento, dimensiones y origen de cada `idGen`). Los parámetros de tipo
  PROPIOS de cada método se emparejan por **posición** (D6): `<T> T m(T x)`
  y `<U> U m(U x)` tienen la misma signatura, igual que en Java real. Si uno
  de los dos es genérico y el otro no, difieren (`semError80`).
- Con esto, `Clase.chequearRedefinicion()` (llamado desde
  `consolidarConPadre()`) reconoce la redefinición aunque el nombre del
  parámetro de tipo propio cambie, incluyendo `ERR_METODO_REDEFINE_FINAL`
  cuando el heredado es `final`. Ver
  `resources/semantico/sinErrores/semCorrecto20.java`, `semCorrecto28.java`
  y `resources/semantico/conErrores/semError42.java`.
- El conflicto entre superinterfaces (`Interfaz.heredarMetodosDe()`) usa la
  misma comparación, así que dos interfaces que devuelven, cada una, su
  propio parámetro de tipo con nombres distintos no chocan. Ver
  `semCorrecto21.java` (compatible) y `semError44.java` (realmente
  incompatible, sigue detectándose).
- Declarar en la misma clase dos métodos que solo difieren en el nombre de
  su propio parámetro de tipo (`<T> T m(T x)` y `<U> U m(U x)`) da
  `ERR_METODO_DUPLICADO`: tienen la misma clave `m/1`. Ver `semError43.java`.
- **Instanciación genérica — hecha** (spec 0001, tickets 10 y 11). Cuando
  la subclase pasa un argumento a una superclase o interfaz genérica
  (`class B extends A<String>`), la signatura heredada se compara vista
  con ese argumento (`Tipo.sustituir()` con el mapa de instanciación de la
  clase o de la interfaz), también en cadena. Ver 5.6 (`semCorrecto25`,
  `26`, `31`, `34`; `semError61`, `71`, `79`, `88`, `89`).

**Todavía pendiente (fuera de alcance):** la sustitución de tipos en usos
concretos (`Lista<T>` → `Lista<Integer>` al chequear sentencias) es de la
Etapa 4. El argumento genérico de una declaración (`Caja<Foo>` con `Foo`
inexistente) ya se valida (5.5).

### 5.4 Logro 4 — multi-detección de errores semánticos

Este es el único logro que se puede testear **sin** extender la gramática —
conviene priorizarlo primero porque es transversal al resto. Ya tiene 4 casos
concretos de ejemplo (3 de herencia circular en la sección 3.6, uno de
declaraciones repetidas con tres categorías distintas en la 3.5 —
`multiError/semMultiErrorVariasDeclaracionesRepetidas.java`).

Los 4 puntos de abajo ya están **bajados a archivo y verificados contra la
salida real del compilador** (no solo deducidos), en
`resources/semantico/multiError/` — ninguno lleva oráculo `///` porque ese
directorio nunca estuvo wireado a un tester JUnit (confirmado: ningún
`src/test/java/*.java` lo referencia); son ilustrativos, igual que el
`semMultiErrorVariasDeclaracionesRepetidas.java` que ya existía.

- Dos clases con nombre duplicado + una tercera clase con un tipo no
  declarado en el mismo archivo → ambos errores reportados en una sola
  corrida (`ERR_CLASE_DUPLICADA` + `ERR_TIPO_NO_DECLARADO`).
  `multiError/semMultiErrorDuplicadaYTipoNoDeclarado.java`.
- "Descarta ambas entidades": la misma tercera clase de arriba, que usa el
  nombre duplicado como tipo de atributo, confirma indirectamente que
  ninguna de las dos sobrevivió (falla con `ERR_TIPO_NO_DECLARADO` en vez de
  resolver contra cualquiera de las dos) — mismo archivo que el punto
  anterior, no hizo falta exponer la `TablaSimbolos` (pregunta abierta 2
  sigue abierta, pero no era necesaria para esto).
- **Límite — N clases (3, no solo 2) con el mismo nombre.**
  `multiError/semMultiErrorTresClasesMismoNombre.java`. Al bajar este caso a
  archivo se encontró un **bug real**: `TablaSimbolos.insertarClase`
  **borraba** la entrada del mapa al detectar el duplicado, así que con 3
  declaraciones la tercera ya no colisionaba con nada y se reinsertaba
  limpia — con N impar, el nombre "revivía" y quedaba usable; con N par,
  quedaba bien descartado. Arreglado con un `Set<String> nombresInvalidados`
  que, una vez que un nombre dispara `ERR_CLASE_DUPLICADA`, lo mantiene
  invalidado para siempre sin importar cuántas veces más se repita. Ahora,
  con 3 declaraciones: dos `ERR_CLASE_DUPLICADA` (la 2ª y la 3ª) y el nombre
  queda sin resolver, sin importar la paridad.
- Mezcla de error sintáctico + error semántico en el mismo archivo — ambas
  listas se reportan juntas (confirmado: `AnalizadorSemanticoHandlerImpl.
  analizar()` corre `tablaSimbolos.consolidar()` siempre, aun con errores
  sintácticos, igual patrón que `ModuloPrincipalET2` entre léxico y
  sintáctico). `multiError/semMultiErrorSintacticoYSemantico.java`.

### 5.5 Logro 5 — genericidad avanzada (anidados en la declaración)

**Hecho**, en el marco de `REQ-AS-010` (anidados + diamante, un único
parámetro de tipo por clase/interfaz/método). El sintáctico ya aceptaba
`Caja<Lista<Item>>`; ahora el argumento se guarda (`Tipo` recursivo) y cada
nivel se valida en atributos, parámetros y retorno:

- Positivo: `Caja<Lista<Item>>` con todo declarado, en las tres posiciones.
  `sinErrores/semCorrecto18.java`.
- Positivo: `idGen` como argumento anidado resuelto contra el entorno
  vigente (el `T` de la clase en un atributo, el `U` propio de un método en
  su retorno/parámetro), y raw type (`Lista cruda;`) aceptado.
  `sinErrores/semCorrecto22.java`.
- Negativo: `Caja<Foo>` con `Foo` no declarada → `ERR_TIPO_NO_DECLARADO`
  sobre `Foo`. `conErrores/semError48.java` (atributo),
  `semError53.java` (parámetro).
- Negativo: dos niveles, `Caja<Lista<Foo>>` → `ERR_TIPO_NO_DECLARADO` sobre
  `Foo`. `conErrores/semError49.java`.
- Negativo: `idGen` anidado fuera de scope (`Lista<T>` sin `<T>` vigente) →
  `ERR_TIPO_GENERICO_NO_DECLARADO`. `conErrores/semError50.java`.
- Negativo: clase no genérica usada con argumento (`Item<Item>`) →
  `ERR_TIPO_NO_GENERICO` (código nuevo). `conErrores/semError51.java`.
- De paso: el retorno de un método ahora valida su cabeza `idClase`
  (`Foo m()` con `Foo` inexistente). `conErrores/semError52.java`.
- Sintácticos: anidados en atributo/parámetro/retorno y diamante en `new`,
  `sinErrores/sintCorrecto32.java`; diamante en declaración de miembro es
  error, `conErrores/sintError63.java` (los casos de variable local y
  for-each ya estaban en `sintError34`/`sintError35`).
- Multi-detección verificada a mano: un archivo con cabeza inexistente,
  argumento inexistente, `idGen` fuera de scope y clase no genérica con
  argumento reporta todos los errores en la misma corrida (incluidos varios
  sobre la misma línea, uno por nivel).

Desde la spec 0001 (D2) los argumentos de `extends`/`implements` se validan
igual (`semError68`, `69`, `75`; ver 5.6).

**Fuera de alcance:** inferencia del diamante (`Lista<Item> l = new
Lista<>();`, pisa "chequeo de sentencias", sección 4); sustitución de tipos
y compatibilidad/invariancia en inicializadores.

### 5.6 Tipos predefinidos, clave y redefinición

Casos de la spec `Documentación/specs/0001-correccion-etapa3.md` y del
diseño "Decisiones: tipos predefinidos, clave y redefinición" en
`analizador-semantico.md`. **Hecho** (spec 0001, tickets 01–12): todos los
casos de esta sección pasan con su etiqueta definitiva. Además de los casos
que lista la spec (`semCorrecto23`–`28`, `semError54`–`71`), hay uno por
cada historia de usuario con resultado observable que no tenía caso
(`semCorrecto29`–`33`, `semError72`–`85`), y los que agregaron los tickets
06 y 11 (`semCorrecto34`, `semError86`–`89`, al final de la sección). La
columna **Hist.** es el número de historia de la spec.

Los nombres de clase tienen al menos dos letras (`Aa`, `Dd`, `Ii`): una
sola mayúscula es un `idGen` para el léxico, y `class A` es un error
sintáctico.

**Códigos nuevos:**

| Código | Cuándo | Lexema | Descripción |
|---|---|---|---|
| `ERR_HERENCIA_TIPO_INCORRECTO` | Relación con el tipo equivocado (D1): una clase extiende una interfaz, una clase implementa una clase o una interfaz extiende una clase. Se reporta en el control de declaraciones (`TablaSimbolos.chequearCategoriaSupertipo()`), y la consolidación ignora la relación: la clase queda con `Object` como padre efectivo. | el tipo nombrado en la relación | `"Ii" no es una clase` / `"Aa" no es una interfaz` |
| `ERR_CONSTRUCTOR_NOMBRE_INVALIDO` | Un constructor no tiene el nombre de su clase (D3). Se reporta al parsearlo (`Clase.agregarConstructor()`) y no se registra. | el nombre escrito en el constructor | `el constructor "Bb" no tiene el nombre de su clase` |
| `ERR_REDEFINICION_INCOMPATIBLE` | Un método de instancia tiene la clave de uno de instancia heredado (o de uno de la interfaz que implementa la clase) y su signatura no es exactamente igual después de instanciar. También cuando un método de interfaz choca con un método de instancia de `Object`. | nombre del método de la hija, de la clase o de la interfaz | `el método "m" redefine o implementa un método con otra signatura` |
| `ERR_REDEFINE_ESTATICO` | Un descendiente declara un método con la clave de un estático heredado, o declara un estático con la clave de un método de instancia heredado. | nombre del método del descendiente | `el método "m" tiene la clave de un método heredado y uno de los dos es estático` |

**Código retirado:** `ERR_INTERFAZ_NO_DECLARADA` (D7). Un supertipo
inexistente se reporta una sola vez como `ERR_TIPO_NO_DECLARADO`
(`semError04`, `semError84`), y la consolidación saltea la relación sin
reportar.

**Códigos que cambian:**

- `ERR_METODO_DUPLICADO` y `ERR_CONSTRUCTOR_DUPLICADO` comparan la clave
  (nombre, aridad), no los tipos de los parámetros.
- `ERR_METODO_RETORNO_INCOMPATIBLE` compara la signatura completa. Su
  descripción pasa a ser "se hereda con signaturas incompatibles".
- `ERR_METODO_INTERFAZ_NOIMPLEMENTADO` también sale cuando la clase sólo
  tiene un método estático con la clave pedida (`semError83`).
- `ERR_TIPO_NO_DECLARADO`, `ERR_TIPO_NO_GENERICO` y
  `ERR_TIPO_GENERICO_NO_DECLARADO` también se reportan sobre el supertipo y
  su argumento en `extends`/`implements` (D2).
- `ERR_CLASE_DUPLICADA` sobre una predefinida descarta sólo la declaración
  del programa.

**Sin errores:**

| Archivo | Qué prueba | Hist. | Hoy |
|---|---|---|---|
| `sinErrores/semCorrecto23.java` | `Object`, `String` y `System` como tipos de atributos, parámetros y retornos | 5 | pasa |
| `sinErrores/semCorrecto24.java` | `String toString()` redefinido en una clase sin `extends` | 39 | pasa |
| `sinErrores/semCorrecto25.java` | `class Dd extends Base<String>` redefine `T get()` como `String get()`, y `poner(T valor)` como `poner(String otro)` | 43, 44 | pasa |
| `sinErrores/semCorrecto26.java` | cadena `Base<T>` → `Intermedia<U> extends Base<U>` → `Final extends Intermedia<String>`, con `Final` redefiniendo con `String` | 45 | pasa |
| `sinErrores/semCorrecto27.java` | sobrecarga por aridad: `mostrar()` y `mostrar(int n)`, y dos constructores de aridad distinta | 28 | pasa |
| `sinErrores/semCorrecto28.java` | `<V> V id(V x)` redefine `<U> U id(U x)` y cumple el contrato de una interfaz con `<U> U id(U x)` | 48 | pasa |
| `sinErrores/semCorrecto29.java` | `class Animal extends Object` explícito, con `toString` redefinido | 9 | pasa |
| `sinErrores/semCorrecto30.java` | `toString` heredado de `Object` cumple el contrato de `interface Imprimible { String toString(); }` | 14, 64 | pasa |
| `sinErrores/semCorrecto31.java` | `class Caja implements Proveedor<String>` cumple `T obtener()` con `String obtener()` | 59 | pasa |
| `sinErrores/semCorrecto32.java` | un método de interfaz `void debugPrint(int nivel)` no choca con el `debugPrint` estático de `Object` | 63 | pasa |
| `sinErrores/semCorrecto33.java` | herencia sin argumento de un tipo genérico, en `extends` de clase, `implements` y `extends` de interfaz | 24 | pasa |

**Con errores:**

| Archivo | Qué prueba | Código | Etiqueta | Hist. |
|---|---|---|---|---|
| `conErrores/semError54.java` | `class Object {}` | `ERR_CLASE_DUPLICADA` | `[Error:Object\|6]` | 6 |
| `conErrores/semError55.java` | `class Aa extends String {}` | `ERR_HERENCIA_CLASE_FINAL` | `[Error:Aa\|5]` | 8 |
| `conErrores/semError56.java` | `imprimir(int n)` + `imprimir(char c)` | `ERR_METODO_DUPLICADO` | `[Error:imprimir\|9]` | 29 |
| `conErrores/semError57.java` | `int toString()` en una clase | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:toString\|6]` | 40 |
| `conErrores/semError58.java` | `void debugPrint(int x)` en una clase | `ERR_REDEFINE_ESTATICO` | `[Error:debugPrint\|6]` | 51 |
| `conErrores/semError59.java` | `static void m()` en la hija sobre un `void m()` heredado | `ERR_REDEFINE_ESTATICO` | `[Error:m\|11]` | 53 |
| `conErrores/semError60.java` | `void m(int[] a)` heredado y `void m(int a)` en la hija | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:m\|11]` | 42 |
| `conErrores/semError61.java` | `class Dd<T> extends Base` redefine `T get()` con su propio `T` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:get\|13]` | 46 |
| `conErrores/semError62.java` | `interface Xx extends Ii1, Ii2` con `void m(int x)` en `Ii1` y `void m(char c)` en `Ii2` | `ERR_METODO_RETORNO_INCOMPATIBLE` | `[Error:m\|10]` (el `m` de `Ii2`) | 61 |
| `conErrores/semError63.java` | `interface Ii { int toString(); }` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:toString\|6]` | 62 |
| `conErrores/semError64.java` | `class Aa implements Ii` con `void m(char c)`, e `Ii` declara `void m(int x)` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:m\|11]` (el `m` de `Aa`) | 56 |
| `conErrores/semError65.java` | `class Bb extends Ii`, con `Ii` interfaz (antes de la spec: `ClassCastException`) | `ERR_HERENCIA_TIPO_INCORRECTO` | `[Error:Ii\|8]` | 1, 2, 16 |
| `conErrores/semError66.java` | `class Bb implements Aa`, con `Aa` clase | `ERR_HERENCIA_TIPO_INCORRECTO` | `[Error:Aa\|7]` | 17 |
| `conErrores/semError67.java` | `interface Jj extends Aa`, con `Aa` clase | `ERR_HERENCIA_TIPO_INCORRECTO` | `[Error:Aa\|7]` | 18 |
| `conErrores/semError68.java` | `class Bb extends Caja<Nada>` | `ERR_TIPO_NO_DECLARADO` | `[Error:Nada\|8]` | 21 |
| `conErrores/semError69.java` | `class Cc extends Bb<Bb>`, con `Bb` no genérica | `ERR_TIPO_NO_GENERICO` | `[Error:Bb\|8]` | 22 |
| `conErrores/semError70.java` | `Bb()` como constructor dentro de `class Aa` (con `Bb` declarada) | `ERR_CONSTRUCTOR_NOMBRE_INVALIDO` | `[Error:Bb\|9]` | 34 |
| `conErrores/semError71.java` | `class Aa implements Ii` (sin argumento) con `Object obtener()` contra `T obtener()` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:obtener\|11]` | 60 |
| `conErrores/semError72.java` | atributo duplicado antes de `class Bb extends Ii`: el error anterior se sigue imprimiendo (antes de la spec: `ClassCastException`) | `ERR_ATRIBUTO_DUPLICADO` | `[Error:x\|7]` | 3 |
| `conErrores/semError73.java` | `class Aa extends System {}` | `ERR_HERENCIA_CLASE_FINAL` | `[Error:Aa\|4]` | 8 |
| `conErrores/semError74.java` | `class Bb extends Ii` con `void debugPrint(int x)`: tras descartar la relación, `Object` es el padre efectivo | `ERR_REDEFINE_ESTATICO` | `[Error:debugPrint\|10]` | 19 |
| `conErrores/semError75.java` | `class Bb extends Caja<T>`, con `Bb` no genérica | `ERR_TIPO_GENERICO_NO_DECLARADO` | `[Error:T\|8]` | 23 |
| `conErrores/semError76.java` | `Punto(int x)` + `Punto(char c)` | `ERR_CONSTRUCTOR_DUPLICADO` | `[Error:Punto\|9]` | 31 |
| `conErrores/semError77.java` | `void m(int x)` + `void m(char c)` en una interfaz | `ERR_METODO_DUPLICADO` | `[Error:m\|7]` | 33 |
| `conErrores/semError78.java` | `void m(int x)` heredado y `void m(char x)` en la hija | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:m\|11]` | 41 |
| `conErrores/semError79.java` | `class Dd extends Base` (sin argumento) con `Object get()` contra `T get()` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:get\|12]` | 47 |
| `conErrores/semError80.java` | `Object id(Object x)` heredado y `<T> T id(T x)` en la hija | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:id\|12]` | 49 |
| `conErrores/semError81.java` | `static void s()` heredado y `void s()` en la hija | `ERR_REDEFINE_ESTATICO` | `[Error:s\|10]` | 52 |
| `conErrores/semError82.java` | `static void s()` heredado (a través de un intermedio) y `static void s()` en la nieta | `ERR_REDEFINE_ESTATICO` | `[Error:s\|14]` | 52 |
| `conErrores/semError83.java` | `class Aa implements Ii` sólo con `static void m()` | `ERR_METODO_INTERFAZ_NOIMPLEMENTADO` | `[Error:m\|6]` (el `m` de `Ii`, como `semError32`) | 58 |
| `conErrores/semError84.java` | `class Aa implements Nada` | `ERR_TIPO_NO_DECLARADO` | `[Error:Nada\|5]` | 20 |
| `conErrores/semError85.java` | `int f()` + `void f()` | `ERR_METODO_DUPLICADO` | `[Error:f\|10]` | 30 |

Hoy pasan todos. En la línea de base (ticket 01) sólo pasaban `semError84`
y `semError85`, cuyo comportamiento ya era el correcto.

**ADR 0005 (`implements` de una sola interfaz).** Los casos que usaban
`implements A, B` ya se reescribieron con una interfaz intermedia
(`interface AB extends A, B` + `class X implements AB`): `sintCorrecto31`,
`semCorrecto10`, `semCorrecto13`, `semCorrecto15`, `semCorrecto21`,
`semError30`, `semError33`, `semError36` y `semError44`. `sintError52` pasó
a ser el caso sintáctico de `class Malo implements Uno, Dos`
(`[Error:,|5]`). `semError33` prueba ahora una interfaz repetida no
contigua en el `extends` de una interfaz, porque el control de repetidas en
el `implements` de una clase se retira. Hoy pasan los tres que fallaban
en la línea de base: `sintError52` (la gramática ya no acepta la lista),
`semError36` y `semError44` (la interfaz compara entre sí los métodos que
hereda de varias superinterfaces).

**Casos existentes que cambian con la clave (paso 4, hecho):**
`semCorrecto14` (`mover(int)` + `mover(char)` en una clase) y
`semCorrecto15` (lo mismo, heredado de dos interfaces) pasaron a ser
errores por la clave (nombre, aridad). Se movieron a `conErrores/` sin
tocar su código, con la etiqueta corregida y el comentario de cabecera
reescrito (mismas líneas):

| Archivo | Antes | Qué prueba | Código | Etiqueta |
|---|---|---|---|---|
| `conErrores/semError86.java` | `sinErrores/semCorrecto14.java` | `mover(int pasos)` + `mover(char pasos)` en una clase | `ERR_METODO_DUPLICADO` | `[Error:mover\|13]` |
| `conErrores/semError87.java` | `sinErrores/semCorrecto15.java` | lo mismo en `Pato implements Anfibio`, con `Anfibio extends Volador, Nadador` | `ERR_METODO_DUPLICADO` | `[Error:mover\|22]` |

Además, con la clave pasan `semError56`, `semError76` y `semError77`.

**Casos que cambian y casos nuevos con los contratos (ticket 11, hecho):**
`semError37` (`char getValor()` en una clase contra `int getValor()` de su
interfaz) daba `ERR_METODO_RETORNO_INCOMPATIBLE` sobre el método de la
interfaz (`[Error:getValor|6]`). Con la regla del contrato es
`ERR_REDEFINICION_INCOMPATIBLE` sobre el método de la clase, como
`semError64`: etiqueta corregida a `[Error:getValor|10]`, mismo código y
comentario reescrito con las mismas líneas. `semError87` imprime además
`[Error:mover|11]` (el conflicto entre `Volador.mover(int)` y
`Nadador.mover(char)` en `Anfibio`); su etiqueta `|22` sigue apareciendo.
Casos nuevos, por la instanciación a través del `extends` de una interfaz:

| Archivo | Qué prueba | Código | Etiqueta | Hist. |
|---|---|---|---|---|
| `sinErrores/semCorrecto34.java` | `Lista<U> extends Fuente<U>, Origen<U>` (las dos con `T get()`) no da conflicto, y `Caja implements Lista<String>` la cumple con `String get()` y `poner(String)` | — | `[SinErrores]` | 59, 61 |
| `conErrores/semError88.java` | `Caja implements Lista<String>`, con `Lista<U> extends Fuente<U>`, declara `Object get()` contra el `T get()` de `Fuente` | `ERR_REDEFINICION_INCOMPATIBLE` | `[Error:get\|15]` (el `get` de `Caja`) | 56, 59 |
| `conErrores/semError89.java` | `Mixta extends Fuente<String>, Origen<Object>`, las dos con `T get()` | `ERR_METODO_RETORNO_INCOMPATIBLE` | `[Error:get\|12]` (el `get` de `Origen`) | 61 |

Todos los testers de Etapas 1–3 pasan (263 tests al cerrar la spec). Los
casos existentes que cambiaron de resultado por una regla de la Parte I
(`semCorrecto14`/`15` → `semError86`/`87`, y la etiqueta de `semError37`) se
corrigieron en su etiqueta esperada, no en el código, como se anota arriba.

## 6. Preguntas abiertas a resolver antes de escribir casos concretos

Recopiladas de las secciones de arriba, para discutir antes de implementar:

1. ~~Formato del oráculo de error semántico~~ — **resuelto** (sección 2):
   etiqueta de la cátedra `///[Error:<lexema>|<línea>]` (la convención
   anterior `///[ErrorSem:<CODIGO>|<línea>]` se reemplazó el 2026-10-16). Para
   herencia circular, **resuelto también** (sección 3.6): un solo error por
   ciclo, reportado sobre la última clase visitada en orden de declaración
   (requiere `TablaSimbolos.clases` como `LinkedHashMap`).
2. Si `ModuloPrincipalET3` necesita exponer la `TablaSimbolos` final (no solo
   la lista de errores) para que los testers de `sinErrores` puedan afirmar
   sobre el contenido resuelto.
3. **Resuelta.** Regla de sobrecarga para métodos/constructores con el
   mismo nombre y distintos parámetros: la Parte I sólo admite sobrecarga
   por **aridad** (clave nombre/aridad, spec 0001, D4). Distinta cantidad
   de parámetros es sobrecarga válida; la misma cantidad es una declaración
   repetida aunque difieran los tipos (`semError56`, `semError76`). La
   primera resolución (sobrecarga por tipos de parámetro) se revirtió con
   la clave (5.2, 5.6). El bug de `crearMetodo()` (registraba el método en
   `Clase.metodos` antes de que `argsFormales()` terminara de parsear los
   parámetros reales) sigue arreglado — ver 5.2.
4. Si un atributo y un método pueden compartir nombre en la misma clase, y si
   un parámetro de tipo genérico (`idGen`) puede compartir nombre con un
   miembro (sección 3.5).
5. Si hay alguna promoción numérica implícita entre primitivos, o
   compatibilidad es por identidad estricta (sección 3.4).
6. Reglas de chequeo de sentencias en general (sección 4) — hoy no están
   definidas en `analizador-semantico.md`.
7. **Resuelta.** `final` en interfaz: se decidió no implementarlo (Java real
   tampoco lo admite), se sacó de la gramática. Extender una clase `final` y
   redefinir un método `final` heredado: ya tienen chequeo
   (`ERR_HERENCIA_CLASE_FINAL`/`ERR_METODO_REDEFINE_FINAL`, sección 5.1). La
   combinación `sealed`+`final` en una misma clase: sintácticamente
   imposible, no hace falta decidir nada.
8. **Resuelta.** Conflictos entre superinterfaces con la misma clave
   (nombre, aridad): si la signatura completa coincide (después de
   instanciar), no hay conflicto; si no, es error
   (`ERR_METODO_RETORNO_INCOMPATIBLE`, sección 5.2), también cuando sólo
   difieren los tipos de los parámetros. `extends A, A` repetido en una
   interfaz: **resuelta**, es error (`ERR_INTERFAZ_DUPLICADA`). En una
   clase, `implements A, A` ya es error sintáctico (ADR 0005).
9. **Resuelta.** Shadowing entre el `T` de un método genérico y el `T` de su
   clase contenedora: permitido sin error, como en Java real (sección 5.3).

## 7. Próximos pasos propuestos

1. **Hecho** — casos concretos de herencia circular (sección 3.6, 9 archivos:
   6 `conErrores` + 3 `sinErrores`, nombres de clase corregidos a 2+
   caracteres por REQ-AL-04/05 — una sola mayúscula lexa como `idGen`, no
   `idClase`) y de declaraciones repetidas **a nivel declaración** (sección
   3.5, 8 archivos: 7 negativos en `conErrores` +
   `semMultiErrorVariasDeclaracionesRepetidas.java`) están en
   `Código/resources/semantico/{sinErrores,conErrores,multiError}/`, con el
   formato de oráculo de la sección 2. Los casos de variable local
   (`ERR_VARIABLE_LOCAL_DUPLICADA`) que había en esta lista se sacaron — ver
   el aviso de "Fuera de alcance" en 3.5: son chequeo de sentencias, no de
   declaraciones, y vuelven cuando se diseñe esa etapa.
2. **Hecho** — resueltos los problemas de compilación del esqueleto (sección
   0) y `TablaSimbolos.clases` ya es `LinkedHashMap`.
3. **Hecho** — `TesterSemanticoDeCasosSinErrores`/`ConErrores`
   (`Código/src/test/java/`) ya corren contra
   `resources/semantico/{sinErrores,conErrores}/` sobre un pipeline semántico
   real (`ModuloPrincipalET3` → `AnalizadorSemanticoHandlerImpl` →
   `TablaSimbolos.consolidar()`), con los 37 archivos de `conErrores/`
   pasando en verde (los 14 de las secciones 3.5/3.6 + `semError15`/`16` de
   la sección 3.2 — tipo no declarado en atributo y en parámetro — +
   `semError17` — atributo duplicado por herencia, caso nuevo documentado al
   final de 3.5 — + `semError18`-`37` de las secciones 5.1/5.2 — `sealed`/
   `nonsealed`/`final`, clase, interfaz, `implements`, herencia múltiple,
   métodos/duplicados de interfaz, y conflicto de retorno incompatible,
   ver detalle ahí). `sinErrores/` tiene 15 archivos (`semCorrecto01`-`03`
   de control de herencia + `04`-`15` de 5.1/5.2, los últimos dos para el
   fix de sobrecarga real — ver sección 6, punto 3). Pendiente: el tester
   no-parametrizado de `multiError` (análogo a `TesterSintacticoModoPanico`).
4. Cerrar las preguntas abiertas de la sección 6 que sean baratas de decidir
   ahora (2, 3, 4, 5) antes de escribir más casos concretos (tipos no
   declarados fuera de herencia, scope de `idGen`, sobrecarga con firma
   distinta).
5. Seguir bajando a archivos concretos el resto de la sección 3 (registro de
   clases ya cubierto por 3.5, resolución de tipos, scope de `idGen`,
   compatibilidad de `Tipo`), con el mismo patrón ya usado.
6. Implementar y testear en este orden, de menor a mayor dependencia de
   extensiones de gramática todavía no escritas:
   - **Declaraciones básicas** (sección 3, incluidas herencia circular y
     declaraciones repetidas) — no depende de ningún cambio sintáctico.
   - **Logro 4** (multi-error) — ya tiene 4 casos concretos de ejemplo
     (`multiError/`); extender el mismo patrón al resto de la sección 3.
   - **Logro 5** (genéricos anidados) — ya tiene soporte sintáctico completo.
   - **Logro 1** (`sealed`/`final`) — **hecho**, incluyendo `implements` y
     listas de interfaces: gramática completa (`sealed`/`permits`/
     `nonsealed` en clase e interfaz, `final` en clase y método,
     `extends` de interfaz aceptando 2+ interfaces; `implements`, una sola
     desde el ADR 0005) y
     semántica completa, simétrica entre `extends`/`implements` y entre
     cada entrada de una lista
     (`ERR_HERENCIA_NO_PERMITIDA`/`ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR`/
     `ERR_NONSEALED_SIN_PADRE_SEALED`/`ERR_HERENCIA_CLASE_FINAL`/
     `ERR_METODO_REDEFINE_FINAL`, ver 5.1/5.2). `final` en interfaz se
     descartó (decisión, no gap).
   - **Logro 2** (herencia múltiple de interfaces) — **hecho** por
     completo: gramática, `sealed`/`permits` entre múltiples padres,
     `Interfaz.metodos` poblado (`ERR_METODO_INTERFAZ_NOIMPLEMENTADO`/
     `ERR_METODO_DUPLICADO` ya disparan para interfaces), `implements`/
     `extends` repetido (`ERR_INTERFAZ_DUPLICADA`), y conflicto de retorno
     incompatible entre interfaces (`ERR_METODO_RETORNO_INCOMPATIBLE`, ver
     5.2). De paso se encontró y arregló un bug preexistente y no
     relacionado: la sobrecarga real (mismo nombre, distintos parámetros)
     dentro de una sola clase estaba rota — `crearMetodo()` registraba el
     método antes de que se parsearan sus parámetros reales (sección 6,
     punto 3, ya resuelta).
   - **Logro 3** (métodos genéricos) — necesita primero una extensión de la
     gramática sintáctica (ver gap en 5.3), a tratar como una revisión más
     de `analizador-sintactico.md` antes de volver acá.
7. Recién después de tener un primer borrador de "chequeo de sentencias" en
   `analizador-semantico.md` (hoy inexistente), bajar la sección 4 de este
   documento a casos concretos con oráculo.
