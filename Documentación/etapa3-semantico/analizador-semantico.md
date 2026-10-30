# Analizador Semántico

## Propósito

El analizador semántico toma las estructuras generadas por el sintáctico y:

- Termina de recopilar la información del programa y las relaciones entre entidades.
- Controla que el programa sea semánticamente válido.

## Organización

Se agrupa según el nivel donde se realizan los chequeos:

- Chequeo de declaraciones (Esta etapa)
- Chequeo de sentencias

**Estado actual**: el chequeo de declaraciones ya corre de punta a punta
(`TablaSimbolos.estaBienDeclarado()` y después `TablaSimbolos.consolidar()`,
invocados desde `AnalizadorSemanticoHandlerImpl`) y tiene
tests en `Código/resources/semantico/{sinErrores,conErrores}/` + los
testers `TesterSemanticoDeCasosSinErrores`/`ConErrores`
(`Código/src/test/java/`) — ver `casos-de-prueba.md` secciones
3.2/3.5/3.6 para el detalle de qué cubre cada archivo. El chequeo de sentencias
(variables locales, `ERR_VARIABLE_LOCAL_DUPLICADA`, resolución de
identificadores en expresiones, etc.) **no está implementado ni tiene casos
de prueba todavía** — queda explícitamente para cuando se diseñe esa sección
más abajo; no bajar casos de prueba de sentencias a
`resources/semantico/{sinErrores,conErrores}/` antes de eso.

## Chequeo de declaraciones

Se focaliza solamente en las declaraciones. Es decir, en controlar que toda
declaración sea semánticamente válida y en recopilar la información que falte
de las entidades que se declararon.

En un lenguaje como MiniJava podemos dividir fácilmente el programa en
declaraciones y sentencias:

- Todo lo que está dentro de un bloque es una sentencia (incluyendo el
  bloque), es decir, las cosas que se "ejecutan" en el programa.
- Todo lo que está fuera de un bloque es una declaración:
  - Clases
  - Atributos
  - Métodos

### Actividades principales

1. Chequear que toda entidad fue correctamente declarada.
2. Consolidar la tabla de símbolos.

Estas actividades dependen de las restricciones de declaración del lenguaje.

### Chequear que toda declaración fue correctamente declarada

En general implica chequear que:

- No haya nombres repetidos en el mismo contexto (ej: dos clases con el mismo
  nombre). [Puede realizarse directamente mientras se construye la TS en el
  sintáctico.]
- Todo nombre usado en una declaración haya sido declarado en el contexto
  adecuado (ej: el tipo de un atributo o el retorno de un método).
- Se cumplan las restricciones propias de cada entidad (redefinición,
  contratos de interfaz, constructores, tipos predefinidos): ver
  "Decisiones: tipos predefinidos, clave y redefinición", al final de este
  documento.

**Chequeo de corrección.** La idea es que quede desligado de la
construcción de la TS (que es lo que cubre el EDT de la sección "Acciones
semánticas sobre la gramática original"): la construcción arma los objetos y
los registra; el chequeo de corrección corre **después**, recorriendo la
`TablaSimbolos` ya consolidada (no el árbol de parseo) — reglas que necesitan
todo resuelto de antemano, como herencia circular, compatibilidad de firmas
al overridear un método heredado, o que los tipos referenciados efectivamente
existan. Es lo que hacen `Chequeable.estaBienDeclarado()` /
`TablaSimbolos.estaBienDeclarado()` + `TablaSimbolos.consolidar()` en el código.

**Estado actual (ya implementado, vía `estaBienDeclarado(TablaSimbolos,
Map<String, OrigenParametroTipo>)` en
`Clase`/`Atributo`/`Metodo`/`Constructor`/`Interfaz`/`Tipo`):** nombres repetidos
(clase/atributo/método/constructor/parámetro, incluida la colisión de un
atributo redeclarado por herencia en `Clase.consolidarConPadre`), herencia
circular, y que el tipo de un atributo, parámetro o retorno (`idClase`)
exista en la tabla — incluidos sus argumentos genéricos anidados (Logro 5,
ver abajo). Los supertipos de `extends`/`implements` se guardan como `Tipo`
y se validan igual (D2), junto con su categoría (D1). **Scope de `idGen` —
implementado (sin jerarquía `Tipo` completa, ver abajo):** `Clase`/`Interfaz`
guardan su parámetro de tipo
(`parametrosTipo`, 0 o 1 elemento: un único parámetro, `REQ-AS-010`) y
`Metodo` el suyo propio (`parametrosTipoPropios`, mismo límite; soporta
métodos genéricos como `<T> T metodo(T x)`, independientes del genérico de la
clase — ver `casos-de-prueba.md` sección 5.3).
`Chequeable.estaBienDeclarado` recibe el entorno de `idGen` vigente en vez
de depender de estado transitorio en `TablaSimbolos`: un mapa de cada nombre
a la entidad que lo declara (`OrigenParametroTipo`, que implementan `Clase`,
`Interfaz` y `Metodo`). `Clase`/`Interfaz` construyen el suyo a partir de
`parametrosTipo` y lo propagan a supertipos/atributos/constructores/métodos;
`Metodo` lo copia y le agrega sus propios `parametrosTipoPropios` antes de
chequear su tipo de retorno y delegar en cada `Parametro`. Un método puede
redeclarar el mismo nombre que su clase/interfaz contenedora (shadowing) sin
error: el del método pisa al del contenedor, como en Java.
`Tipo.estaBienDeclarado` le deja a cada `idGen` en alcance su **origen**
(`Tipo.getOrigen()`), y así el `T` de una clase y el `T` de otra son tipos
distintos. Un `idGen` que no está en el entorno vigente (tipo de atributo,
parámetro, retorno o argumento de un supertipo) reporta
`ERR_TIPO_GENERICO_NO_DECLARADO`. **Redefinición, contratos e instanciación
genérica — implementados** (spec 0001): ver "Decisiones: tipos
predefinidos, clave y redefinición", al final de este documento.
**En la Etapa 4:** la jerarquía `Tipo` completa
(`TipoPrimitivo`/`TipoReferencia`/`TipoGenerico`/`TipoArreglo`) no se hizo:
`Tipo` sigue siendo una sola clase recursiva que guarda cabeza, argumento,
dimensiones y origen (spec 0003, D5). La sustitución de tipos en usos
concretos (`Lista<T>` → `Lista<Integer>` al chequear sentencias) se hizo con
`Tipo.sustituir(Map<OrigenParametroTipo, Tipo>)` (spec 0003, D10). Ver
`casos-de-prueba.md` secciones 3.2/3.5/3.6/5.3/5.6 para el detalle de qué
está cubierto por archivo de test.

## Tabla de símbolos: por qué no usar Singleton

La tabla de símbolos necesita ser "global" en el sentido de que hay una única
instancia viva durante la compilación de un programa. Eso no requiere el
patrón Singleton, y de hecho conviene evitarlo:

- **Acopla todo el analizador a estado estático global.** Usar
  `TablaSimbolos.getInstance()` desde cualquier lado esconde la dependencia:
  ya no se ve en la firma de los métodos que una clase necesita la tabla, y
  cualquier parte del código puede leerla o modificarla sin pasar por una
  interfaz clara.
- **Rompe el testing.** Los tests unitarios de chequeo semántico correrían
  todos en la misma JVM compartiendo el mismo estado estático. Sin un
  `reset()` manual entre tests, una corrida contamina a la siguiente.
- **No soporta múltiples análisis en la misma ejecución.** Si en algún
  momento se necesita analizar más de un archivo/programa en la misma
  corrida (por ejemplo, un batch de tests), el singleton obliga a resetear
  el estado global entre corridas en vez de simplemente crear una tabla
  nueva por análisis.
- **El argumento de eficiencia del profesor** probablemente apunta a que la
  implementación clásica de libro de un Singleton "seguro" usa
  `synchronized` en `getInstance()`, agregando overhead de sincronización
  innecesario en un compilador que corre en un solo hilo.

### Alternativa: instancia única pasada por referencia (sin Singleton)

En vez de acceso estático global, se logra el mismo efecto (una sola tabla
viva por compilación) mediante inyección de dependencia explícita:

1. Se instancia **una** `TablaSimbolos` en el punto de entrada del análisis
   (análogo a cómo `ModuloPrincipalET2` en
   `Código/src/View/ModuloPrincipalET3.java` instancia una única vez el
   `AnalizadorSintacticoHandler` y lo usa para toda la corrida).
2. Esa instancia se pasa **por referencia** a quien la necesite: al
   visitor/chequeador semántico, como parámetro de constructor o de método
   (`chequear(TablaSimbolos tabla)`).
3. Al ser un objeto común (no estático), cada corrida de análisis puede
   crear la suya propia. Esto es ideal para tests unitarios, donde cada test
   instancia su propia tabla sin pisarse con otros.

Mismo efecto práctico que un Singleton (una sola instancia por compilación),
sin el acoplamiento oculto ni los problemas de testing.

## Creación de la Tabla de Simbolos en MiniJava

<Clase> ::= class idC<Herencia>{<Miembro>}
<Herencia>::= extends idC | e

Declaración de clase: ¿Qué debemos hacer?

Dos estrategias:

- Delegando la inserción de los elementos en profundidad. La idea es que en cada
producción inserto el elemento vinculado a esa producción inserto el elemento vinculado a esa producción en la 
estructura correspondiente de la TS. [Requiere llevar cuenta de la clase actual, metodo actual,etc.]
- Retornando el/los elementos a insertar La udea es que en cada producción retorno el elemento vinculado a esa producción.
Es responsabilidad de quien contiene ese elemento.

**Comparando las dos estrategias:**

- **Estrategia A (delegar la inserción en profundidad).** El parser mantiene
  un objeto "abierto" (ej. `claseActual`) como estado compartido — variable
  de instancia del parser, o algo Singleton-like — y cada producción hija lo
  escribe directamente a medida que va reconociendo piezas
  (`claseActual.agregarMetodo(...)` apenas termina de parsear un método).
  Nadie retorna nada "hacia arriba": el efecto ya ocurrió como side-effect
  mientras el parser bajaba.
  - Ventaja: parece más simple de escribir al principio (no hay que ir
    devolviendo objetos por toda la cadena de `return`s).
  - Desventaja: es el mismo problema que ya se descartó para `TablaSimbolos`
    en "por qué no usar Singleton" — acopla todo a estado mutable
    compartido, rompe el testing de cada método en aislamiento, y es fácil
    de romper si algún método se olvida de "cerrar"/restaurar el estado al
    volver de la recursión (ej: un miembro terminando agregado a la clase
    equivocada por un bug de orden de llamadas).
- **Estrategia B (retornar el elemento).** Cada `analizar<NoTerminal>()`
  devuelve el objeto que construyó, como variable local de quien lo llama.
  Nadie escribe directo en un `Clase`/`Metodo` que no le pertenece: el
  objeto se arma completo y recién se conecta con su contenedor cuando ese
  contenedor lo recibe como valor de retorno.

**Decisión: se usa la Estrategia B** para ir creando las instancias
(`Atributo`/`Metodo`/`Constructor`/`Clase`) y conectándolas entre sí. Ver el
ejemplo concreto, con código, en "Ejemplo concreto: cómo se asocian tipo +
nombre al construir el objeto" (dentro de la sección EDT).

El contexto que igual hace falta pasar hacia abajo para poder construir bien
(`entGen` para resolver `idGen`, la propia `tabla` para buscar `idClase`)
**se pasa por parámetro explícito** a los métodos que lo necesitan, nunca
como estado mutable compartido (variable de instancia del parser o
Singleton) — mismo argumento que en "Tabla de símbolos: por qué no usar
Singleton".

`claseActual` **no hace falta en absoluto durante la construcción**: lo
único para lo que se hubiera usado (chequear que el nombre del constructor
coincida con el de la clase) es un chequeo de corrección, y esa categoría de
chequeo quedó explícitamente diferida (ver "TODO — Chequeo de corrección"
más arriba). Alcanza con que `Constructor` guarde el token de su propio
nombre — la comparación contra el nombre de la `Clase` que lo contiene se
hace después, cuando corra ese chequeo diferido sobre la `TablaSimbolos` ya
armada, no durante el parseo.

## Tipo Primitivo VS Tipo Referencia.

¿Como representar los tipos?
Los tipos primitivos y referencia son diferentes semánticamente y por lo tanto deben 
modelarse por separado (Los tipos primitivos NO son clases).

Por otra parte es importante modelarlos de manera uniforme para diseñar adecuadamente las entidades que lo utilizan
(métodos y varibales). Esto lleva a la necesidad de implementar una clase (jerarquía) `Tipo`,
en vez de seguir guardando el tipo como el `Token` crudo que devuelve el léxico
(que es lo que hacen hoy `Atributo.java`/`Parametro.java`).

### Por qué un `Token` no alcanza

Un `Token` es la representación **sintáctica** (lexema + línea + `TokenType`,
útil para el mensaje de error), no la representación **semántica** (qué
significa ese texto una vez resuelto):

- **Genéricos.** `<TipoReferencia> ::= idClase <TipoGenericoOpcional>`. Con
  `Lista<Item> a; Lista<Otro> b;`, `a` y `b` arrancan con el mismo token
  `idClase` (`"Lista"`) pero son tipos incompatibles. El resultado de
  `<TipoGenericoOpcional>` no cabe en el token — hay que cargarlo aparte, y en
  cuanto se hace eso ya se está reconstruyendo un objeto `Tipo`, aunque no se
  lo llame así.
- **Arreglos.** `<DimensionesOpcionales> ::= [ ] <DimensionesOpcionales> | ϵ`.
  `int` e `int[][]` arrancan con el mismo token `PR_INT`; la cantidad de
  `[ ]` se acumula consumiendo tokens adicionales, no es un campo de `Token`.
  Guardar solo el primer token pierde si era arreglo y de qué dimensión.
- **Referencias sin resolver.** Para chequear herencia/compatibilidad hace
  falta la `Clase` ya resuelta (para poder recorrer `extends`), no el nombre
  como string. Guardando solo el token `idClase` habría que volver a buscar en
  la `TablaSimbolos` por lexema cada vez que se necesite comparar o subtipar,
  en vez de resolver una sola vez.
- **Comparación de tipos.** `Token` no tiene noción de "soy compatible con".
  Sin una clase `Tipo`, cada chequeo (asignación, retorno, parámetro de
  llamada) terminaría comparando `tipo.getLexema().equals(otro.getLexema())`
  a mano, duplicando la lógica de compatibilidad en cada lugar en vez de
  tenerla centralizada en un solo método (`Tipo.esCompatibleCon(Tipo)`).

### Por qué un primitivo no es una `Clase`

La gramática ya distingue tres categorías de tipo base, no dos:

```
<TipoBase> ::= <TipoPrimitivo> | <TipoReferencia> | idGen
<TipoPrimitivo> ::= boolean | char | int
<TipoReferencia> ::= idClase <TipoGenericoOpcional>
```

`Clase` (y la `TablaSimbolos`, un `HashMap<String, Clase>`) modela algo
puntual: una entidad **declarada por el programador**, con nombre, que puede
heredar y tiene atributos/métodos que hay que chequear. Ningún primitivo
cumple esto: `boolean`/`char`/`int` no los declara nadie (son palabras
reservadas del léxico, no `idClase`), no tienen atributos ni métodos, no
heredan y no pueden aparecer en `<HerenciaOpcional>`. Modelarlos como
instancias de `Clase` obligaría a forzar campos que nunca se usan
(`atributos`/`metodos` siempre vacíos, `estaBienDeclarada()` sin nada que
validar).

### Diseño: jerarquía `Tipo`

> **No se implementó:** `Tipo` quedó como una sola clase, con
> `conformaCon`, `Tipo.NULL` y `Tipo.VOID` (spec 0003, D5). Lo de abajo es
> el boceto original.

Una interfaz o clase abstracta `Tipo`, con una implementación por cada forma
en que puede resolverse `<TipoBase> <DimensionesOpcionales>`:

- **`TipoPrimitivo`** — conjunto cerrado y fijo (`boolean`, `char`, `int`).
  No busca nada en la `TablaSimbolos`; alcanza con un enum o tres constantes.
- **`TipoReferencia`** — envuelve la `Clase` (o interfaz) ya resuelta desde la
  `TablaSimbolos`, más el `Tipo` resuelto de `<TipoGenericoOpcional>` si lo
  hay. Acá sí hay que buscar `idClase` y dar error semántico si no fue
  declarada.
- **`TipoGenerico`** — para `idGen`: no es primitivo ni referencia, es una
  variable de tipo válida solo dentro del scope de la clase/método que la
  declaró (`<GenericidadOpcional>`). Se resuelve contra el entorno de
  genéricos vigente, no contra la `TablaSimbolos`.
- **`TipoArreglo`** — envuelve cualquiera de los tres anteriores más la
  cantidad de dimensiones acumuladas en `<DimensionesOpcionales>`.

El paso de `Token` (sintáctico) a `Tipo` (semántico) ocurre una sola vez, al
procesar `<Tipo>` durante el chequeo de declaraciones, y ese `Tipo` resuelto es
lo que se guarda en `Atributo`/`Metodo`/`Parametro` de ahí en más — no el
`Token` original (que puede seguir guardándose aparte solo si hace falta para
reportar la línea de un error).

**Estado actual**: la jerarquía no existe todavía (queda para la Etapa 4).
Hay una sola clase `Tipo`, recursiva, que guardan `Atributo`, `Parametro`,
el retorno de `Metodo` y los supertipos de `Clase`/`Interfaz`. Guarda:
- la cabeza (el `Token`: primitivo, `idClase` o `idGen`);
- el argumento genérico, a su vez un `Tipo` (`<InstanciadoOParametrico>`);
- las **dimensiones** (`int[][]` = 2): el parser ya no las descarta
  (`tipo()` arma el tipo base y le aplica `conDimensiones()`). Sólo el tipo
  de nivel superior las tiene;
- en un `idGen`, su **origen**: la clase, interfaz o método que lo declara,
  resuelto en el control de declaraciones (`getOrigen()`).

`Tipo.estaBienDeclarado` valida cada nivel: `idClase` contra la tabla
(`ERR_TIPO_NO_DECLARADO`, `ERR_TIPO_NO_GENERICO`) e `idGen` contra el
entorno vigente (`ERR_TIPO_GENERICO_NO_DECLARADO`). `Tipo.equals()` compara
cabeza, argumento (recursivo), dimensiones y origen (por identidad): es la
igualdad que usa la comparación de signaturas. `sustituir()` y
`reemplazarParametros()` devuelven vistas sin modificar el original (ver
"Instanciación genérica al comparar").

### Reglas de compatibilidad que dependen de esto

La separación no es solo prolijidad: cambia las reglas de chequeo de tipos de
la fase de sentencias.

- **Primitivos**: compatibilidad por identidad simple (¿es el mismo
  primitivo?). Sin subtipado entre `int`/`char`/`boolean` (salvo que la
  cátedra indique una regla puntual de promoción numérica, a confirmar), y
  `null` **no** es asignable a un primitivo.
- **Referencias**: compatibilidad por **subtipado**, recorriendo
  `extends`/`implements` en `Clase`; `null` sí es asignable a cualquier tipo
  referencia.
- **Genéricos (`idGen`)**: dentro del scope donde está vigente se comporta
  como un tipo opaco (solo igual a sí mismo, salvo bounds), y hay que
  instanciarlo al resolver un uso concreto del tipo parametrizado.


## EStructura de la Tabla de Símbolos
En algunos lenguajes hay partes de la tabla que se terminan de 
consolidar en el chequeo de declaraciones
- Vinculando a entidades que dependen de otras entidades que se declaran mas adelante

En los lengujaes orientados a objetos cuando tenemos herencia no conocemos todos los métodos que tiene una clase hasta que no
procesamos a sus ancestros. En esots lenguajes una vez finalizado el analisis sintactico se consolidan las tabla de metodos de las clases.
(Voy a utilizar esta ultima).


### Acciones semánticas sobre la gramática original

Estrategia elegida (la segunda de las dos que se plantearon en "Creación de la
Tabla de Símbolos en MiniJava"): cada producción **retorna** el objeto que
construye (`.decl`, `.tipo`, etc.) y es quien la contiene el que decide dónde
insertarlo — no hay estado global tipo "clase actual" mutándose a mano.

Dos atributos heredados se pasan explícitamente entre corchetes `[...]`
cuando una producción los necesita:

- `tabla` — la `TablaSimbolos` de la corrida (inyectada, no Singleton — ver
  "Tabla de símbolos: por qué no usar Singleton"). Se usa para insertar
  clases/interfaces y para resolver `idClase` contra una `Clase` ya conocida.
- `entGen` — nombre del parámetro de tipo genérico vigente (viene de
  `<GenericidadOpcional>` de la clase/interfaz que contiene la producción;
  `ϵ` si esa clase/interfaz no declaró ninguno). Hace falta para decidir, en
  `<TipoBase>` / `<InstanciadoOParametrico>`, si un `idGen` suelto es el
  parámetro de tipo válido en ese scope o un error semántico (ver "Diseño:
  jerarquía `Tipo`").

**Por qué dos pasadas sobre `<ListaClases>`.** `<TipoReferencia>` necesita
buscar `idClase` en `tabla`, pero MiniJava permite referencias hacia adelante
(una clase puede usar como tipo de atributo una clase declarada más abajo en
el archivo — ver "EStructura de la Tabla de Símbolos"). Con una sola pasada,
al procesar la primera clase del archivo la tabla todavía no tendría cargadas
las que aparecen después. Se resuelve con dos recorridos de `<ListaClases>`:
la Pasada 1 sólo registra nombres (para que existan todas las entradas), la
Pasada 2 resuelve cuerpos completos (ahí sí `tabla` ya está completa a nivel
de nombres).

#### Pasada 1 — registrar nombres

Sólo se evalúan estas acciones; el resto de cada producción (herencia,
miembros, tipos) no se procesa todavía en esta pasada.

```
<ListaClases> ::= <Clase> <ListaClases> | <Interfaz> <ListaClases> | ϵ

<Clase>    ::= class idClase ...
               { c := new Clase(idClase.lexema)
               ; tabla.insertarClase(c)          // error semántico si el nombre ya existe
               }

<Interfaz> ::= interface idClase ...
               { i := new Interfaz(idClase.lexema)
               ; tabla.insertarClase(i)          // mismo espacio de nombres que las clases
               }
```

#### Pasada 2 — resolver herencia, atributos y métodos

```
<Clase> ::= class idClase
              { c := tabla.buscarClase(idClase.lexema) }        // ya existe, viene de la Pasada 1
            <GenericidadOpcional>
              { c.paramTipo := GenericidadOpcional.nombre
              ; entGen := GenericidadOpcional.nombre }           // heredado al resto de la producción
            <HerenciaOpcional>[tabla, entGen]
              { c.padre     := HerenciaOpcional.tipoExtends
              ; c.interfaz  := HerenciaOpcional.tipoImplements }
            { <ListaMiembros>[tabla, entGen]
              { (atributos, metodos, constructores) := clasificar(ListaMiembros.miembros)
                // clasificar separa por tipo dinámico (Atributo/Metodo/Constructor) y
                // chequea nombres repetidos entre atributos y claves repetidas entre
                // métodos — la clave es (nombre, aridad): ver "Clave y signatura"
                // en "Decisiones: tipos predefinidos, clave y redefinición"
              ; c.atributos     := atributos
              ; c.metodos       := metodos
              ; c.constructores := constructores
              }
            }

<GenericidadOpcional> ::= < idGen > { .nombre := idGen.lexema }
                        |  ϵ        { .nombre := ϵ }

<HerenciaOpcional> ::= extends <TipoReferencia>[tabla, entGen]
                          { .tipoExtends := TipoReferencia.tipo ; .tipoImplements := ϵ }
                      | implements <TipoReferencia>[tabla, entGen]
                          { .tipoExtends := ϵ ; .tipoImplements := TipoReferencia.tipo }
                      | ϵ
                          { .tipoExtends := ϵ ; .tipoImplements := ϵ }

<Interfaz> ::= interface idClase
                 { i := tabla.buscarClase(idClase.lexema) }
               <GenericidadOpcional>
                 { i.paramTipo := GenericidadOpcional.nombre
                 ; entGen := GenericidadOpcional.nombre }
               <ExtensionOpcional>[tabla, entGen]
                 { i.extiende := ExtensionOpcional.tipo }
               { <ListaMetodosInterfaz>[tabla, entGen]
                 { i.metodos := ListaMetodosInterfaz.metodos }
               }

<ExtensionOpcional> ::= extends <TipoReferencia>[tabla, entGen] { .tipo := TipoReferencia.tipo }
                       | ϵ                                      { .tipo := ϵ }

<ListaMiembros> ::= <Miembro>[tabla, entGen] <ListaMiembros>[tabla, entGen]
                       { .miembros := Miembro.decl :: ListaMiembros(2).miembros }   // cons
                   | ϵ
                       { .miembros := [] }

<Miembro> ::= <Atributo>[tabla, entGen]                    { .decl := Atributo.decl }
            | <Metodo>[tabla, entGen]                      { .decl := Metodo.decl }
            | <Constructor>[tabla, entGen]                 { .decl := Constructor.decl }

<Atributo> ::= <Tipo>[tabla, entGen] idMetVar ;
                 { .decl := new Atributo(idMetVar.lexema, Tipo.tipo, idMetVar.linea) }

<Metodo> ::= <ModificadorOpcional> <TipoMetodo>[tabla, entGen] idMetVar <ArgsFormales>[tabla, entGen] <Bloque>
               { .decl := new Metodo(idMetVar.lexema, TipoMetodo.tipo,
                                      ModificadorOpcional.esEstatico,
                                      ArgsFormales.parametros, idMetVar.linea)
                 // <Bloque> no se resuelve en esta etapa — ver "Chequeo de sentencias"
               }

<MetodoInterfaz> ::= <TipoMetodo>[tabla, entGen] idMetVar <ArgsFormales>[tabla, entGen] ;
                        { .decl := new Metodo(idMetVar.lexema, TipoMetodo.tipo,
                                               /* abstracto, sin cuerpo */ true,
                                               ArgsFormales.parametros, idMetVar.linea) }

<ListaMetodosInterfaz> ::= <MetodoInterfaz>[tabla, entGen] <ListaMetodosInterfaz>[tabla, entGen]
                              { .metodos := MetodoInterfaz.decl :: ListaMetodosInterfaz(2).metodos }
                          | ϵ
                              { .metodos := [] }

<Constructor> ::= public idClase <ArgsFormales>[tabla, entGen] <Bloque>
                     { .decl := new Constructor(idClase, ArgsFormales.parametros)
                       // idClase se guarda en el propio Constructor — la
                       // comparación contra el nombre de la clase contenedora
                       // la hace Clase.agregarConstructor() (D3, ver "Constructores"
                       // en "Decisiones: tipos predefinidos, clave y redefinición")
                       // <Bloque> no se resuelve en esta etapa — ver "Chequeo de sentencias"
                     }

<ModificadorOpcional> ::= static { .esEstatico := true }
                         | ϵ     { .esEstatico := false }

<TipoMetodo> ::= <Tipo>[tabla, entGen] { .tipo := Tipo.tipo }
               | void                  { .tipo := TIPO_VOID }   // centinela, no es un Tipo de valor

<Tipo> ::= <TipoBase>[tabla, entGen] <DimensionesOpcionales>
             { .tipo := if DimensionesOpcionales.dim = 0
                        then TipoBase.tipo
                        else new TipoArreglo(TipoBase.tipo, DimensionesOpcionales.dim) }

<TipoBase> ::= <TipoPrimitivo>               { .tipo := TipoPrimitivo.tipo }
             | <TipoReferencia>[tabla, entGen] { .tipo := TipoReferencia.tipo }
             | idGen
                 { if idGen.lexema = entGen
                   then .tipo := new TipoGenerico(entGen)
                   else error("tipo genérico no declarado en este contexto: " + idGen.lexema, idGen.linea)
                 }

<DimensionesOpcionales> ::= [ ] <DimensionesOpcionales> { .dim := 1 + DimensionesOpcionales(2).dim }
                           | ϵ                           { .dim := 0 }

<TipoReferencia> ::= idClase <TipoGenericoOpcional>[tabla, entGen]
                        { clase := tabla.buscarClase(idClase.lexema)
                        ; if clase = null
                          then error("clase no declarada: " + idClase.lexema, idClase.linea)
                        ; .tipo := new TipoReferencia(clase, TipoGenericoOpcional.tipo)  // puede ser ϵ
                        }

<TipoPrimitivo> ::= boolean { .tipo := TipoPrimitivo.BOOLEAN }
                  | char    { .tipo := TipoPrimitivo.CHAR }
                  | int     { .tipo := TipoPrimitivo.INT }

<TipoGenericoOpcional> ::= < <InstanciadoOParametrico>[tabla, entGen] > { .tipo := InstanciadoOParametrico.tipo }
                          | ϵ                                           { .tipo := ϵ }

<InstanciadoOParametrico> ::= idGen
                                 { if idGen.lexema = entGen
                                   then .tipo := new TipoGenerico(entGen)
                                   else error("tipo genérico no declarado: " + idGen.lexema, idGen.linea)
                                 }
                             | idClase
                                 { clase := tabla.buscarClase(idClase.lexema)
                                 ; if clase = null
                                   then error("clase no declarada: " + idClase.lexema, idClase.linea)
                                 ; .tipo := new TipoReferencia(clase, ϵ)
                                 }

<ArgsFormales> ::= ( <ListaArgsFormalesOpcional>[tabla, entGen] )
                      { .parametros := ListaArgsFormalesOpcional.parametros }

<ListaArgsFormalesOpcional> ::= <ListaArgsFormales>[tabla, entGen] { .parametros := ListaArgsFormales.parametros }
                               | ϵ                                 { .parametros := [] }

<ListaArgsFormales> ::= <ArgFormal>[tabla, entGen]
                           { .parametros := [ArgFormal.decl] }
                       | <ListaArgsFormales>[tabla, entGen] , <ArgFormal>[tabla, entGen]
                           { .parametros := ListaArgsFormales(1).parametros ++ [ArgFormal.decl] }

<ArgFormal> ::= <Tipo>[tabla, entGen] idMetVar
                  { .decl := new Parametro(idMetVar.lexema, Tipo.tipo) }
```

**Después de las dos pasadas, sobre todas las clases ya resueltas** (no es
parte de la gramática, es un recorrido aparte sobre `tabla.clases`): se
consolida la tabla de métodos efectiva de cada clase (propios + heredados,
resolviendo redefiniciones), como ya se anotó en "EStructura de la Tabla de
Símbolos" ("una vez finalizado el análisis sintáctico se consolidan las
tablas de métodos de las clases").

### De la gramática original a la LL(1)

Cómo se traslada cada acción de arriba a la gramática factorizada que
realmente recorre el parser (`analizador-sintactico.md`, "Gramática Expandida
(Logros)"), según qué le pasó a cada no terminal:

- **No terminales solo factorizados** (la producción original quedó repartida
  en dos o más no terminales, pero sin agregarse recursión): la acción se
  mueve tal cual al punto donde en la LL(1) se termina de reconocer el mismo
  fragmento. Ej.: la acción de `<Atributo>` (crear el objeto `Atributo`) pasa a
  vivir en la rama correspondiente de `<RestoMiembro>`, que es donde la LL(1)
  recién sabe, por el lookahead, que no era un método.
- **No terminales a los que se les eliminó recursión a izquierda**
  (`<ListaArgsFormales>` es el caso de esta sección): el atributo sintetizado
  que antes se armaba "hacia arriba" por la recursión a izquierda pasa a ser un
  atributo **heredado con acumulador** que se arrastra a derecha por la cadena
  `<ListaArgsFormalesResto>`, y recién se sintetiza (se cierra la lista) al
  llegar al caso base `ϵ`.
- **No terminales nuevos sin contraparte en la gramática original**
  (`<CuerpoMiembro>`, `<TrasIdClaseMiembro>`, `<SentIdClase>`, etc.): no
  cargan significado propio, son solo el punto donde el parser consumió el
  token que decide la alternativa. La acción que en la gramática original era
  un `if` explícito (p. ej. "¿es `<Atributo>` o `<Metodo>`?" en `<Miembro>`)
  en la LL(1) sale gratis: ya viene decidido por **cuál** alternativa de
  `<CuerpoMiembro>` fue la que matcheó.
- **`tabla` y `entGen`** se siguen pasando igual, como parámetros de los
  métodos `analizar<NoTerminal>(...)` del parser — no cambia nada de esto al
  factorizar, porque factorizar no mueve en qué producción hace falta cada
  atributo heredado, solo en qué no terminal literal vive esa producción
  ahora.

### Ejemplo concreto: cómo se asocian tipo + nombre al construir el objeto

La sección anterior explica en general cómo trasladar cada acción a la
gramática LL(1). Acá va el mecanismo concreto para el caso que más cuesta
ver: `<CuerpoMiembro>` reconoce el tipo y el nombre de un miembro, pero
todavía no sabe si es un atributo o un método — esa decisión (y por lo tanto
la construcción del objeto) la termina tomando `<RestoMiembro>`, un no
terminal más abajo en la cadena de factorización.

Es la Estrategia B (ver "Creación de la Tabla de Simbolos en MiniJava")
funcionando en la práctica: como nadie puede escribir en un objeto ajeno ni
guardar "lo que venía viendo" en un campo compartido, cada método solo puede
**devolver** lo que ya construyó y **recibir como parámetro** lo que le
falta. El objeto final se arma recién en el método que junta la última pieza
que le faltaba.

**Caso 1 — `int total;` (atributo) vs `int suma(int a) {...}` (método).**
Los dos arrancan igual (`<TipoPrimitivo> <DimensionesOpcionales> idMetVar
...`); recién se distinguen mirando el token que sigue al nombre.

```java
private Chequeable cuerpoMiembro() {
    ...
    } else if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
        Token tipoTok = tipoPrimitivo();      // (A) consigue el tipo
        dimensionesOpcionales();
        Token nombre = tokenActual;           // (B) consigue el nombre
        match(TokenType.ID_MET_VAR);
        return restoMiembro(tipoTok, nombre); // (C) empaqueta A+B y los pasa
    }
}

private Chequeable restoMiembro(Token tipoTok, Token nombreTok) {
    // tipoTok/nombreTok ya llegaron por parámetro — no hay que "ir a buscarlos"
    if (actualEs(TokenType.PUNTO_COMA)) {
        match(TokenType.PUNTO_COMA);
        return new Atributo(nombreTok, tipoTok);   // (D) recién acá se construye
    } else if (actualEs(TokenType.PAR_A)) {
        List<Parametro> params = argsFormales();
        bloque();
        return new Metodo(nombreTok, tipoTok, false, params);
    }
}
```

`cuerpoMiembro()` no construye nada — no puede, todavía no sabe qué es. Solo
junta `tipoTok`/`nombre` y se los entrega como argumentos a `restoMiembro()`,
que es quien tiene la última pieza (el token siguiente) y por eso es el único
lugar donde aparece `new Atributo(...)` / `new Metodo(...)`.

**Caso 2 — `Persona p;` (atributo de tipo clase) vs `Persona(int x) {...}`
(constructor).** Mismo mecanismo, un nivel más adentro, vía
`trasIdClaseMiembro(Token idClaseTok)`:

```java
private Chequeable cuerpoMiembro() {
    ...
    } else if (actualEs(TokenType.ID_CLASE)) {
        Token idClaseTok = tokenActual;
        match(TokenType.ID_CLASE);
        return trasIdClaseMiembro(idClaseTok);
    }
}

private Chequeable trasIdClaseMiembro(Token idClaseTok) {
    if (actualEs(TokenType.PAR_A)) {
        // "(" pegado al idClase → es CONSTRUCTOR; idClaseTok es el nombre
        List<Parametro> params = argsFormales();
        bloque();
        return new Constructor(idClaseTok, params);
    } else {
        // si no, idClaseTok era el TIPO de un atributo/método
        tipoGenericoOpcional();
        dimensionesOpcionales();
        Token nombre = tokenActual;
        match(TokenType.ID_MET_VAR);
        return restoMiembro(idClaseTok, nombre);   // MISMO restoMiembro() del Caso 1
    }
}
```

`restoMiembro()` es el mismo método en los dos casos: no le importa si el
tipo vino de `<TipoPrimitivo>` o de `idClase`, una vez que lo recibió como
parámetro ya no necesita saber de dónde salió.

**Regla general:** el objeto se construye en el método más profundo que sea
el primero en tener disponibles *todas* las piezas que ese constructor
necesita. Todo lo recolectado antes de ese punto viaja como parámetro de un
método a otro — nunca como campo del parser. Así se ve, en código, la
Estrategia B: si en cambio se hubiera usado la Estrategia A, `cuerpoMiembro()`
habría necesitado escribir `tipoTok`/`nombre` en algún lado compartido (un
campo del parser, o mutar un objeto a medio construir) para que
`restoMiembro()` los "encontrara" ahí — exactamente el acoplamiento que se
buscó evitar.

### Cuidado al pasar de `void` a un tipo de retorno: todo camino tiene que retornar

Java exige que un método que no es `void` **garantice, mirando solo el
código**, que cualquier camino de ejecución termine en un `return` o un
`throw`. No alcanza con que la gramática esté cubierta a mano (que las
ramas de un `if`/`else if` cubran todo el `FIRST` del no terminal) — el
compilador no razona sobre eso, solo ve el código literal. Si algún camino
se le escapa, tira `error: missing return statement`, y esto pega justo en
los métodos con cadenas largas de `if`/`else if` como `cuerpoMiembro()`.

Hay dos patrones, según si el valor a devolver depende de qué rama se tomó:

**1. El valor depende de la rama** (`cuerpoMiembro()`, `restoMiembro()`,
`trasIdClaseMiembro()`): cada rama necesita su propio `return`, **incluida
la rama de error/default**. "No tener nada que retornar" no existe en
Java — si conceptualmente no hay objeto, se retorna `null` explícitamente:

```java
private Chequeable cuerpoMiembro() {
    if (actualEs(TokenType.PR_STATIC)) {
        ...
        return new Metodo(...);
    } else if (actualEs(TokenType.PR_VOID)) {
        ...
        return new Metodo(...);
    } else if (actualEn(PRIMEROS_TIPO_PRIMITIVO)) {
        ...
        return restoMiembro(tipoTok, nombre);
    } else if (actualEs(TokenType.ID_GEN)) {
        ...
        return restoMiembro(tipoTok, nombre);
    } else if (actualEs(TokenType.ID_CLASE)) {
        ...
        return trasIdClaseMiembro(idClaseTok);
    } else {
        error("un miembro de clase (\"static\", \"void\" o un tipo)");
        return null;   // rama que en la práctica no debería alcanzarse
                        // (listaMiembros() ya filtra por PRIMEROS_MIEMBRO
                        // antes de llamar acá), pero el compilador no lo sabe.
    }
}
```

Ese `else` final con `return null;` es lo que deja conforme al compilador.
Y ese `null` no rompe nada río arriba: en `listaMiembros()` se agrega igual
a la lista, y en `clase()` el `instanceof Atributo`/`instanceof
Metodo`/`instanceof Constructor` da `false` para `null` en los tres casos —
se ignora solo. Es el comportamiento que conviene en modo pánico: un
miembro mal formado no aporta nada a la clase, pero tampoco corta la
construcción con una excepción.

**2. El valor NO depende de la rama** (`tipoPrimitivo()`, `tipoBase()`): si
el `if`/`else if` solo hace `match()` (efecto de lado) y lo que hay que
devolver ya se tenía antes de entrar al chequeo, alcanza con sacar el
`return` de adentro de cada rama y dejar **uno solo, al final, afuera del
`if`**:

```java
private Token tipoPrimitivo() {
    Token t = tokenActual;              // ya lo tengo, no depende de la rama
    if (actualEs(TokenType.PR_BOOLEAN)) match(TokenType.PR_BOOLEAN);
    else if (actualEs(TokenType.PR_CHAR)) match(TokenType.PR_CHAR);
    else if (actualEs(TokenType.PR_INT)) match(TokenType.PR_INT);
    else error("\"boolean\", \"char\" o \"int\"");
    return t;                            // un solo return, cubre todos los casos
}
```

Acá ni hace falta un `else` que retorne: como el `return t;` queda afuera
del `if`-chain, el compilador lo ve como código que se ejecuta siempre,
pase lo que pase adentro.

**Cómo elegir:** si el objeto a devolver es distinto en cada rama, patrón 1
(return por rama + `else` final con `return null;`). Si es el mismo dato
para todas las ramas y el `if`-chain solo decide cómo consumirlo, patrón 2
(un solo `return` al final).

## Chequeo de sentencias

> **Reemplazado:** el diseño vigente del chequeo de sentencias está en
> `etapa4-semantico2/requerimientos.md` y en la spec 0003. Esta sección
> describe el estado del código antes de la Etapa 4.

### Por qué no se puede resolver como las declaraciones

`bloque()`/`listaSentencias()`/`sentencia()` (en `AnalizadorSintacticoImpl.java`)
hoy son puramente sintácticos: `void`, solo consumen tokens y validan la
gramática, no construyen nada. Por eso `Metodo.variablesLocales` está
declarado pero nunca se puebla.

A diferencia de clases/métodos/atributos, el chequeo de "¿esta variable usada
fue declarada?" **no admite dos pasadas**. Una clase puede usar como tipo de
atributo una clase declarada más abajo en el archivo (hay forward reference,
por eso Pasada 1 + Pasada 2). Una variable local **no**: en Java/MiniJava solo
es visible desde su declaración en adelante dentro de su bloque.

```java
int x = y; // error: y no existe todavía, no importa si se declara después
int y = 5;
```

Esto no es un detalle de implementación sino una regla del lenguaje: no hay
"hoisting" de locales. El chequeo tiene que ser **una sola pasada, en el
orden exacto del código fuente**, llevando el conjunto de nombres visibles
"hasta este punto" — no se puede consolidar de antemano como se hizo con
`tabla.clases`.

### Hace falta un AST de `Sentencia`/`Bloque`

> **Reemplazado:** el diseño vigente de los nodos del AST (nombres,
> jerarquía, campos y ambigüedades) está en
> `etapa4-semantico2/requerimientos.md`, sección "NODOS". El boceto de abajo
> queda como antecedente. La estrategia de construcción de la subsección
> siguiente sigue valiendo.

Para poder recorrer el cuerpo de un método *después* de parsearlo (el
chequeo de corrección corre sobre estructuras ya armadas, no durante el
parseo — mismo criterio que el resto de "Chequeo de corrección" más arriba),
hay que dejar de descartar el bloque y construir un árbol:

```java
public interface Sentencia { }
public class Bloque implements Sentencia { List<Sentencia> sentencias; }
public class DeclaracionLocal implements Sentencia { Token nombre; Tipo tipo; Expresion inicializador; }
public class SentenciaExpresion implements Sentencia { Expresion expr; }
public class SentenciaIf implements Sentencia { Expresion cond; Sentencia entonces; Sentencia sino; /* null si no hay else */ }
public class SentenciaWhile implements Sentencia { Expresion cond; Sentencia cuerpo; }
public class SentenciaReturn implements Sentencia { Expresion valor; /* null si es void */ }
```

Y `Metodo`/`Constructor` guardan ese `Bloque` (`private Bloque cuerpo;`) en vez
de que `bloque()` lo tire.

### Estrategia de construcción: Estrategia B anidada, con un único punto de attach

El código actual (`crearClase`/`crearMetodo` en `AnalizadorSintacticoImpl.java`,
vía `tablaSimbolo.setClaseActual`/`setMetodoActual`) ya usa **Estrategia A**
para dejar la `Clase`/`Metodo` "abierta" como placeholder — contradice la
decisión original de este documento, pero es un estado de transición
razonable: Pasada 1 crea el objeto vacío para poder referenciarlo, y se va
parchando (`tipoRetorno`, `params`) a medida que Pasada 2 lo resuelve. El
parche en sí también pasa por delegación, igual que `agregarMetodoAClaseActual`
hace con `claseActual`: el parser llama
`tablaSimbolo.setTipoRetornoAMetodoActual(tipo)` /
`tablaSimbolo.setParametrosAMetodoActual(params)` en vez de mutar
directamente la variable local `metodo` devuelta por `crearMetodo()` — de
hecho `crearMetodo()` ya ni devuelve el `Metodo`, es `void`, porque nada en el
parser necesita esa referencia local.

`Atributo`/`Parametro`/`Constructor` no siguen este patrón de
placeholder-y-parche: se construyen completos en una sola línea (su tipo ya
es un `Token` resuelto localmente antes de llamar al constructor, Estrategia
B pura) y se adjuntan encadenando `tablaSimbolo.getClaseActual().agregarX(...)`
— no tienen un "actual" al que delegar, porque no hay nada que parchar
después.

Para el cuerpo del método **no** sirve extender esa misma idea a cada
sentencia (es decir, no hacer `metodoActual.agregarSentencia(nodo)` como
side-effect desde adentro de `sentencia()`). En cuanto hay anidamiento
(`if`/`while`/`for`/`{ }` anidado) se pierde la estructura:

```java
void foo() {
    if (cond) {
        int x = 5;   // si esto se "agrega" directo a metodoActual...
    }
    // ...queda al mismo nivel que el if en la lista del método, y se
    // pierde el dato de que x sólo era visible dentro del if.
}
```

La regla es la misma que para `cuerpoMiembro()`/`restoMiembro()`: **dentro de
la recursión de sentencias siempre se `return`**, y es el contenedor
inmediato (`SentenciaIf`, `SentenciaWhile`, el `Bloque` que envuelve) quien
recibe la pieza como parámetro de construcción — nunca un estado compartido.

```java
private List<Sentencia> listaSentencias() {
    if (actualEn(PRIMEROS_SENTENCIA)) {
        Sentencia s = sentencia();
        List<Sentencia> resto = listaSentencias();
        resto.add(0, s);
        return resto;
    }
    return new ArrayList<>();
}

private Bloque bloque() {
    match(TokenType.LLAVE_A);
    List<Sentencia> sentencias = listaSentencias();
    match(TokenType.LLAVE_C);
    return new Bloque(sentencias);
}

private Sentencia sentenciaIf() {
    match(TokenType.PR_IF);
    match(TokenType.PAR_A);
    Expresion cond = expresion();
    match(TokenType.PAR_C);
    Sentencia entonces = sentencia();
    Sentencia sino = elseOpcional();
    return new SentenciaIf(cond, entonces, sino);
}
```

`metodoActual` solo entra en juego **una vez**, en el borde exterior, cuando
`bloque()` ya devolvió el `Bloque` raíz completo (con todo el anidamiento
resuelto adentro) y se vuelve a `cuerpoMiembro()`/`restoMiembro()`/
`trasIdClaseMiembro()`:

```java
} else if (actualEs(TokenType.PAR_A)) {
    Metodo metodo = crearMetodo(nombreMiembro, false); // ya hace setMetodoActual()
    argsFormales();
    Bloque cuerpo = bloque();
    metodo.setCuerpo(cuerpo);   // único attach, al final — no sentencia por sentencia
```

### `Entorno`: scope para variables locales

> **Reemplazado:** no hay clase `Entorno`. Las locales viven en una pila de
> bloques de `TablaSimbolos` (`abrirBloque`/`cerrarBloque`, `buscarVariable`;
> spec 0003, D6).

Separado de `TablaSimbolos` (que resuelve clases/tipos globales), hace falta
una estructura más chica para resolver nombres locales — una pila/cadena de
scopes:

```java
public class Entorno {
    private Map<String, Tipo> visibles = new HashMap<>();
    private Entorno padre; // scope contenedor; null en la raíz del método

    public Tipo buscar(String nombre) {
        if (visibles.containsKey(nombre)) return visibles.get(nombre);
        return padre != null ? padre.buscar(nombre) : null; // null => no declarada
    }

    public boolean declarar(String nombre, Tipo tipo) {
        if (visibles.containsKey(nombre)) return false; // ya existe en ESTE scope
        visibles.put(nombre, tipo);
        return true;
    }
}
```

### Algoritmo: recorrido en orden sobre el `Bloque`

Corre **después** de que Pasada 2 consolidó `tabla` (clases, herencia,
atributos/métodos resueltos) — igual que el resto de "Chequeo de corrección":

1. Entorno inicial del método: atributos de `miClase` (propios + heredados —
   esos sí son visibles sin importar el orden textual, porque son
   declaraciones de clase, no locales) + los parámetros del método.
2. Recorrer `cuerpo.sentencias` **en orden**:
   - `DeclaracionLocal`: chequear el inicializador contra el entorno
     **actual** (sin la variable nueva todavía — así se detecta `int y = y;`);
     después `entorno.declarar(nombre, tipo)` — si devuelve `false`, error de
     nombre repetido en el mismo scope.
   - Sentencia con expresiones (asignación, llamada, condición de
     `if`/`while`/`for`, `return`): cada identificador usado se resuelve con
     `entorno.buscar(nombre)`; `null` ⇒ error semántico "variable no
     declarada".
   - `Bloque` anidado o cuerpo de `if`/`while`/`for`: crear un entorno hijo
     (`new Entorno(entornoActual)` como padre), recorrer con ese hijo, y
     descartarlo al salir — una variable declarada adentro no se filtra
     afuera del bloque que la contiene.

## Logros (Etapa 3)

1. **Sobrecarga — `sealed` y `final` (E3).** El compilador permite usar los
   modificadores `sealed` y `final` como en Java. `sealed` aplica a clases
   **e interfaces**. `final` aplica a clases y métodos — a diferencia del
   enunciado original de este logro, se decidió **no** admitir `final` en
   interfaces, porque Java real tampoco lo admite (ver "Decisión" más
   abajo).

   **Estado actual (clases):** `sealed`, `permits`, `nonsealed` y `final`
   son palabras clave (`TokenType`/`TablaPalabrasClave`), cada una como
   alternativa separada al principio de `<ListaClases>`
   (`AnalizadorSintacticoImpl.sealed()`/`nonSealed()`/`finalClase()`/
   `permitidosSealed()`/`permitidosSealedResto()`). Como arrancan con
   palabras clave distintas, `sealed final class X` no es ni siquiera
   parseable — la gramática excluye por construcción esa combinación
   contradictoria, sin chequeo semántico aparte. **Mensaje de error
   dedicado para esto** (`sealed()`/`nonSealed()`/`finalClase()`, vía
   `errorModificadorRepetido()`): al consumir el primer modificador, si lo
   que sigue es OTRO de `sealed`/`nonsealed`/`final` en vez de
   `class`/`interface` (repetido como `sealed sealed class X`, o combinado
   como `final sealed final class X`), se reporta un único error claro
   ("no se puede repetir ni combinar sealed/nonsealed/final") sin
   sincronizar — el modificador sobrante queda tal cual para que la
   recursión de `listaClases()` lo vuelva a tratar como modificador en la
   próxima vuelta, y la declaración real que sigue se termina parseando
   bien. Antes de esto, `match(PR_CLASS)` fallaba con el error genérico de
   siempre y el modo pánico se comía toda la declaración real (hasta el
   próximo `{`) como si fuera basura. En `final` sobre método se
   agregó `<FinalOpcional>` entre `<Visibilidad>` y `<CuerpoMiembro>`
   (`finalOpcional()`), threadeado hasta `Metodo.setFinal`; en atributo o
   constructor es error sintáctico (no están en el logro).

   **Estado actual (interfaces):** `sealed`/`nonsealed`/`permits` ya
   también aplican a `<Interfaz>` (`AnalizadorSintacticoImpl.sealedInterfaz()`/
   `nonSealedInterfaz()`/`permitidosSealedInterfaz()`/
   `permitidosSealedRestoInterfaz()` — mismo código que la versión de
   clase, duplicado sobre `Interfaz` en vez de `Clase`, igual criterio que
   ya usa `Interfaz.consolidarHerencia` respecto de
   `Clase.consolidarHerencia`). `final` en interfaz sigue sin existir (ver
   "Decisión" abajo), así que la exhaustividad de `permits` en interfaces es
   de **dos vías** (`sealed`/`nonsealed`), no de tres.

   **Estado actual (`implements`):** wireado por primera vez — `herenciaOpcional()`
   ahora captura la interfaz y la guarda en la clase (antes era un `TODO` sin
   implementar; `Clase.interfaces` se inicializaba pero nada lo poblaba).
   Hoy es `Clase.setInterfaz(Tipo)`, con una sola interfaz (ADR 0005). Con esto,
   `sealed` en una interfaz controla **ambos lados** por igual, como en Java
   real: a quién puede extenderla otra interfaz, y a qué clases puede
   implementarla — Java no distingue entre "subtipo por extends" y "subtipo
   por implements", ambos son subtipos directos sujetos al mismo `permits`.

   Semánticamente, todo en `Clase.consolidarHerencia`/`consolidarInterfaces`/
   `consolidarConPadre` y su espejo en `Interfaz.consolidarHerencia` (mismos
   códigos de error, reusados tal cual entre clase e interfaz, y entre
   `extends`/`implements` — igual que ya pasa con
   `ERR_HERENCIA_CICLICA`/`ERR_TIPO_NO_DECLARADO`):
   - Subclase/subinterfaz/implementación de una `sealed`: debe figurar en su
     `permits`, si no `ERR_HERENCIA_NO_PERMITIDA` — chequeo de **un solo
     nivel**.
   - Subclase/subinterfaz/implementación *permitida* por una `sealed`: debe
     declararse `final` (solo clases), `sealed` o `nonsealed` — un
     `class`/`interface` liso, aunque esté en la lista, ya es
     `ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR` (exhaustividad real de Java).
   - `nonsealed` sin ningún supertipo directo `sealed` real (ni por
     `extends` ni por `implements`) → `ERR_NONSEALED_SIN_PADRE_SEALED`. En
     `Clase` esto ya no se decide en el momento de resolver `extends`: un
     campo `tieneSupertipoSellado` se marca `true` desde
     `consolidarHerencia` (si el padre es `sealed`) **o** desde
     `consolidarInterfaces` (si alguna interfaz implementada es `sealed`), y
     recién al final de `consolidar()` se decide si corresponde el error —
     así una clase `nonsealed` que solo tiene un supertipo `sealed` por
     `implements` (sin ningún `extends`) no es un falso positivo.
   - Extender una clase `final` → `ERR_HERENCIA_CLASE_FINAL`, sin
     excepción (no hay `permits` que valga para una `final`; no aplica a
     interfaces porque `final interface` no existe).
   - Redefinir (misma clave) un método heredado que era `final` →
     `ERR_METODO_REDEFINE_FINAL`. Chequeo de **declaraciones**, en la fusión de
     herencia — no toca cuerpos/sentencias de método (esa etapa sigue sin
     implementar y no hace falta para esto).

   **Decisión — `final` en interfaz:** se quitó de la gramática. Java real
   no permite declarar una interfaz `final` (contradice su propósito: una
   interfaz existe para ser implementada). Escribir `final interface X {}`
   cae en el error sintáctico normal de `clase()` esperando `"class"` y
   encontrando `"interface"` — no hace falta un caso especial.

   **Hecho — validación de `permits`** (`Clase.validarPermitidos`/
   `Interfaz.validarPermitidos`, llamado al final de `consolidar()`): cada
   nombre en `permits` tiene que corresponder a un subtipo DIRECTO real,
   igual que `javac`. Dos casos, dos códigos distintos (como en Java real:
   "cannot find symbol" es un error diferente de "class X does not extend
   sealed class Y in permits clause"):
   - El nombre no corresponde a ninguna clase/interfaz declarada →
     `ERR_TIPO_NO_DECLARADO` (mismo código que cualquier otro tipo no
     declarado).
   - El nombre existe pero no extiende/implementa esta clase/interfaz
     directamente → `ERR_PERMITS_NO_ES_SUBTIPO` (código nuevo). Para una
     interfaz, el subtipo puede ser otra interfaz que la extiende o una
     clase que la implementa; para una clase, solo otra clase que la
     extiende (una interfaz no puede `extends` de una clase).
   Ver `casos-de-prueba.md` §5.1 para el detalle y los casos
   de test (`semError45`/`semError46`/`semError47`).

2. **Herencia múltiple (E3).** El compilador permite herencia múltiple de
   interfaces, como en Java.

   **Decisión (implementada, spec 0001 ticket 02):** el logro vale sólo
   entre interfaces (`interface X extends A, B`). Una clase implementa como
   máximo una interfaz, así que `class X implements A, B` vuelve a ser un
   error sintáctico, como en la gramática base. `Clase` guarda una sola
   interfaz (`Clase.interfaz`) y el control de interfaz repetida queda sólo
   en el `extends` de una interfaz. Al consolidarse, una interfaz copia los
   métodos de sus superinterfaces (`Interfaz.heredarMetodosDe()`) y reporta
   `ERR_METODO_RETORNO_INCOMPATIBLE` si dos de ellas traen la misma clave
   con distinta signatura; la clase compara su contrato contra esa vista
   completa. Los párrafos "Estado actual" que siguen son el historial del
   logro: lo que dicen sobre la lista en `implements`, la firma y el
   conflicto de retorno describe el estado anterior a la spec 0001. Ver
   `Documentación/adr/0005-herencia-multiple-solo-entre-interfaces.md` y la
   sección "Decisiones: tipos predefinidos, clave y redefinición".

   **Estado actual:** `<HerenciaOpcional>` (`implements` en una clase) y
   `<ExtensionOpcional>` (`extends` en una interfaz) ya aceptan una lista de
   2+ interfaces separada por coma (`<ListaInterfaces>`/
   `<ListaInterfacesResto>`, recursión a derecha —
   `AnalizadorSintacticoImpl.listaInterfacesImplementadas()`/
   `listaInterfacesExtendidas()`). `extends`+`implements` combinados en la
   misma clase siguen siendo mutuamente excluyentes (sin cambios ahí). El
   chequeo de `sealed`/`permits`/exhaustividad del Logro 1 ya opera sobre
   cada entrada de la lista de forma independiente — un problema con una
   entrada no aborta el chequeo de las demás (mismo criterio de
   multi-detección, Logro 4). Esto fue, de paso, lo que cerró el único
   límite que le quedaba al Logro 1 (antes `sealed` en una interfaz solo
   controlaba una única clase/interfaz del otro lado).

   **Estado actual (métodos de interfaz):** `Interfaz.metodos` ya se
   puebla — `metodoInterfaz()` construía antes solo sintaxis y descartaba
   todo; ahora arma el `Metodo` completo (sin placeholder: `<MetodoInterfaz>`
   no tiene cuerpo que parsear después, a diferencia de un método de clase)
   y lo registra con `Interfaz.agregarMetodo(Metodo)`, que chequea
   duplicados igual que `Clase.agregarMetodo` (`ERR_METODO_DUPLICADO`, hoy
   por clave). Esto activó, por fin,
   `ERR_METODO_INTERFAZ_NOIMPLEMENTADO` — existía como chequeo desde antes,
   pero nunca disparaba porque iteraba sobre una colección siempre vacía.
   `extends A, A` (interfaz repetida) ya es error
   (`ERR_INTERFAZ_DUPLICADA`, en `Interfaz.agregarHerencia`) — no se
   deduplica más en silencio. En una clase, `implements A, A` es hoy un
   error sintáctico (ADR 0005).

   **Estado actual (conflicto entre superinterfaces):** con la clave
   (nombre, aridad), dos superinterfaces que traen la misma clave con
   distinta signatura (otro retorno u otros tipos de parámetros) son un
   conflicto: `Interfaz.heredarMetodosDe()` reporta
   `ERR_METODO_RETORNO_INCOMPATIBLE` sobre el método de la superinterfaz
   posterior. Que la clase declare la clave con otra signatura que la de su
   interfaz ya no es este código: es `ERR_REDEFINICION_INCOMPATIBLE` (ver
   "Interfaces" en "Decisiones: tipos predefinidos, clave y redefinición").

   **Arreglado — bug preexistente de sobrecarga real, encontrado de paso.**
   La sobrecarga real (mismo nombre, distintos parámetros) dentro de una
   sola clase estaba rota: `crearMetodo()` registraba el placeholder (con
   `params=[]`) en `Clase.metodos` antes de que `argsFormales()` terminara
   de parsear los parámetros reales, así que el chequeo de firma duplicada
   siempre veía `nombre()` sin parámetros y disparaba `ERR_METODO_DUPLICADO`
   por error entre dos sobrecargas legítimas. `crearMetodo()` ya no
   registra nada — solo deja el placeholder como `metodoActual`; el
   registro real se movió a `TablaSimbolos.agregarMetodoActualAClaseActual()`,
   llamado después de `setParametrosAMetodoActual()` en los 3 call sites
   del parser, cuando la clave (hoy `getClave()`) ya ve los parámetros
   reales. Ver
   `casos-de-prueba.md` §5.2 y §6 punto 3 para el detalle y
   los casos de test.

3. **Métodos genéricos (E3).** El compilador permite declarar métodos
   genéricos, como en Java. Las reglas de redefinición para estos métodos
   siguen las mismas que en Java.

   **Estado actual — hecho.** Un método guarda su propio parámetro de tipo
   (`Metodo.parametrosTipoPropios`), válido sólo en su signatura. Al
   comparar dos signaturas (`Metodo.mismaSignatura()`), los parámetros de
   tipo propios se emparejan por posición (D6): `<V> V id(V x)` redefine
   `<U> U id(U x)`, y un método genérico y uno no genérico con la misma
   clave tienen signaturas distintas. Ver `casos-de-prueba.md` §5.3.

4. **Multi-detección de errores semánticos (E3).** El compilador no finaliza
   la ejecución ante el primer error semántico en una declaración, sino que
   se recupera y es capaz de reportar otros errores que tenga el programa
   fuente en la misma corrida. Como mínimo se espera que el compilador pueda
   continuar con el análisis de la siguiente declaración y que, cuando hay
   nombres repetidos, descarte ambas entidades.

   **Estado actual — hecho.** El mecanismo base ya estaba implementado desde
   antes (la lista de errores se acumula sin abortar; `AnalizadorSemantico
   HandlerImpl.analizar()` corre `tablaSimbolos.estaBienDeclarado()` y
   `consolidar()` siempre, aun
   con errores sintácticos, y `ModuloPrincipalET3` junta ambas listas) — lo
   que faltaba era solo bajar a archivo los casos límite. Al hacerlo
   (`resources/semantico/multiError/`) se encontró y arregló un bug real:
   con **3 o más** declaraciones del mismo nombre, `TablaSimbolos.
   insertarClase` **borraba** la entrada al detectar el duplicado, así que
   una declaración impar más allá de la segunda ya no colisionaba con nada
   y se reinsertaba limpia — "resucitando" un nombre que debía quedar
   descartado para siempre. Fix: un `Set<String> nombresInvalidados` que,
   una vez que un nombre dispara `ERR_CLASE_DUPLICADA`, lo mantiene
   invalidado sin importar cuántas veces más se repita. Ver
   `casos-de-prueba.md` §5.4.

5. **Genericidad avanzada.** El compilador controla que los tipos genéricos
   anidados sean utilizados correctamente en la declaración.

   **Estado actual — hecho**, dentro del marco de la Genericidad Avanzada de
   la Etapa 2 (`REQ-AS-010`): anidados y notación diamante, **un único
   parámetro de tipo** por clase/interfaz (y por método, misma producción
   `<GenericidadOpcional>`). En una vuelta anterior se había generalizado a
   lista (`<T, U>`); se revirtió para respetar `REQ-AS-010`.

   - `Tipo` pasó a ser recursivo (`token` + `argumento`, también `Tipo`),
     igual que `<InstanciadoOParametrico>`. El parser
     (`tipoGenericoOpcional()`/`instanciadoOParametrico()`/`tipoReferencia()`/
     `trasIdClaseMiembro()`) ya no descarta el argumento. `Atributo`,
     `Parametro` y el retorno de `Metodo` guardan un `Tipo` y delegan el
     chequeo en `Tipo.estaBienDeclarado`.
   - Cada nivel se valida: `idClase` contra la tabla (`ERR_TIPO_NO_DECLARADO`),
     `idGen` contra el entorno vigente (`ERR_TIPO_GENERICO_NO_DECLARADO`), y
     una clase/interfaz **no** genérica usada con argumento
     (`ERR_TIPO_NO_GENERICO`, código nuevo, el *"does not take parameters"*
     de `javac`). Raw type (`Lista l;` siendo genérica) se acepta, como en
     Java. El argumento se recorre aunque la cabeza falle (multi-detección).
   - De paso, el retorno de un método ahora valida su propia cabeza
     `idClase` (antes solo atributos y parámetros lo hacían).
   - La clave de un método no mira los tipos (es nombre/aridad), así que
     `m(Lista<A>)` y `m(Lista<B>)` en la misma clase son una declaración
     repetida. La signatura sí compara el argumento: redefinir uno con el
     otro es `ERR_REDEFINICION_INCOMPATIBLE`.
   - Diamante: válido solo en `new` (sintáctico, sin cambios); en una
     declaración de miembro, variable local o for-each es error sintáctico.

   Desde la spec 0001, los argumentos genéricos en `extends`/`implements`
   también se validan (D2), y la sustitución se usa para comparar
   signaturas heredadas (ver "Instanciación genérica al comparar").

   **Fuera de alcance:** la sustitución de tipos en usos concretos,
   compatibilidad/invariancia en inicializadores, inferencia del diamante
   (sentencias). Ver `casos-de-prueba.md` §5.5.

## Aclaraciones:
- Cuando el problema se da por un nombre repetido, el token es el que fue declarado
posteriormente desde el punto de visat sintáctico.
- Cuando un método esta mal redefinido el token corresponde con su nombre en la clse descendiente
  (el qu esta mal redefinido).
- Cuando hay un token refiriendo a un tipo no declarado, usar el lexema y linea de ese token y linea de ese token para 
reportar el error
- CUando no hay token claramente asociado a la clase del problema (por ejemplo un meotdo de interface n implmentado) usar el token del nombre de la entidad
que tiene el problema (en el ejemplo sería de la clase descendiente

## Convenciones y consideraciones
### Mensaje de analisis exitoso
CUando no se detectan errores en el analisis el modulo principal debería
mostrar un mensaje adecuado y luego el código de exito [Sin errores]

Class C1{
}

-> 

Compilación Exitosa 
[SinErrores]

## Diseño de la estructura de la Tabla de Simbolso
- Indentificar las entidades declarables
  - la idea es que cada entidad declrable serpa una clase
  - luego si una entidad puede contener otras entidades tendrablas/hasshs con esas entidades.
  
Chequeo de declaraciones
- COntrol de la correctitud de declaración de todos los elementos de la TS
- Consolidación de la TS

Estas tareas se pueden diseñar como funciones/metodos directamente sobre la TS y las clases de las entidades 

Para diseñar esta tareas la clave es valernos de la
Delegación y las Responsabilidades de cada entidad
● Por Ejemplo, para el control de correctitud
– La idea es que cada entidad controla lo que le corresponde (su
responsabilidad) y solicita a cada entidad que contiene que se
controle (delega el control)


Los controles de variables locales deben realizarse en el chequeo de sentencais

## Decisiones: tipos predefinidos, clave y redefinición

Diseño cerrado en una sesión de `/grill-me` (2026-10-17). **Implementado**
por la spec `Documentación/specs/0001-correccion-etapa3.md` (tickets 01–12,
2026-10-20), con las decisiones D1–D7 de `plan-correccion-etapa3.md`. Cierra
los desvíos respecto del enunciado (§3–§7 de la Parte I): la clave de los
métodos, los estáticos en la herencia, los tipos predefinidos, la
instanciación genérica al comparar y las relaciones mal formadas. Los
términos son los de `Documentación/GLOSARIO.md` (**Clave**, **Signatura**,
**Redefinición**, **Contrato de interfaz**, **Tipo predefinido**,
**Constructor por defecto**). Lo que sigue describe lo que hace el código.

### Tipos predefinidos

- `TablaSimbolos` registra `Object`, `String` y `System` en su constructor,
  con un método privado `registrarPredefinidas()`, antes de analizar el
  programa. Son objetos `Clase` comunes con un flag `predefinida`
  (`isPredefinida()`) y tokens sintéticos (el lexema real, el `TokenType`
  que corresponde y línea 0). Una subclase `ClasePredefinida` duplicaría
  lógica sin aportar nada: `String` y `System` tienen que heredar de
  `Object` como cualquier clase. `TablaSimbolos.getObject()` devuelve la
  `Object` predefinida (nunca es `null`).
- Miembros, según el §7:
  - `Object`: `static void debugPrint(int i)` y `String toString()`.
  - `String`: hereda de `Object` y no declara métodos propios.
  - `System`: hereda de `Object` y declara `static int read()`,
    `static void printB(boolean b)`, `static void printC(char c)`,
    `static void printI(int i)`, `static void printS(String s)`,
    `static void println()`, `static void printBln(boolean b)`,
    `static void printCln(char c)`, `static void printIln(int i)` y
    `static void printSln(String s)`.
- `String` y `System` son clases finales; `Object` no. `Object` y `String`
  tienen constructor por defecto y `System` no tiene ninguno. El enunciado
  no dice nada sobre esto y se siguió a Java: ver
  `Documentación/adr/0004-predefinidas-siguen-a-java.md`.
- **Redeclaración.** `class String {}` reporta `ERR_CLASE_DUPLICADA` sobre
  la declaración del programa (su lexema y su línea). Se descarta sólo esa
  declaración, la predefinida se conserva y el nombre no se invalida. Es
  una excepción a la regla general de `insertarClase`, que descarta las dos
  declaraciones: si se aplicara, `String` desaparecería y cada
  `printS(String)` produciría errores en cascada.
- **En `consolidar()`.** El paso 1 (`estaBienDeclarado`) saltea las
  predefinidas, porque no las escribió el usuario. El paso 2 las consolida
  como a cualquier clase, y así `String` y `System` reciben `toString` y
  `debugPrint`. Como están primeras en el `LinkedHashMap`, se consolidan
  antes que las del programa.

### Herencia implícita de `Object`

"Una clase sin relación explícita extiende `Object`" (§4), y una clase que
implementa una interfaz también es subtipo de `Object`. El campo
`Clase.herencia` queda crudo (`null` si no hay `extends`), porque
`esSubclaseDirecta` y la validación de `permits` lo leen así. El padre
efectivo se resuelve en `consolidarHerencia` y queda en
`Clase.getPadreEfectivo()`: si `herencia == null` y la clase no es `Object`,
el padre es `Object`. Lo mismo pasa cuando la relación escrita se descarta
porque nombra un tipo inexistente (D7) o una interfaz (D1). Si el parser
pusiera un token `Object` sintético, `class A` y `class A extends Object`
quedarían indistinguibles para los chequeos de `sealed`.
`getPadreEfectivo()` es `null` sólo en `Object` y en las clases que están en
un ciclo de herencia (o dependen de uno).

### Relaciones entre tipos declarados

- **Herencia como `Tipo` (D2).** `Clase.herencia`, `Clase.interfaz` y cada
  entrada de `Interfaz.herencias` son un `Tipo` (cabeza más argumento
  opcional), tal como se escribió. Se validan en el control de
  declaraciones, en el entorno genérico de la clase o interfaz que declara
  la relación, con la misma validación que cualquier tipo referencia:
  `ERR_TIPO_NO_DECLARADO` (`extends Caja<Nada>`), `ERR_TIPO_NO_GENERICO`
  (`extends Bb<Bb>` con `Bb` no genérica) y `ERR_TIPO_GENERICO_NO_DECLARADO`.
  Heredar sin argumento de un tipo genérico es válido (§5): es la única
  posición que admite un genérico sin argumento
  (`Tipo.estaBienDeclaradoComoSupertipo()`), y sólo en la cabeza.
- **Sin tipos raw (spec 0002, ADR 0007).** En cualquier otra posición
  (atributo, parámetro, retorno, tipo base de arreglo, argumento genérico,
  también el de una herencia), una entidad genérica sin argumento da
  `ERR_TIPO_SIN_ARGUMENTO` sobre el nombre del tipo. Reemplaza al ADR 0001,
  que los aceptaba en las declaraciones.
- **Supertipo inexistente (D7).** Se reporta una sola vez, como
  `ERR_TIPO_NO_DECLARADO`, en el control de declaraciones. La consolidación
  saltea la relación sin reportar. `ERR_INTERFAZ_NO_DECLARADA` **se retiró**:
  ya no existe en el código.
- **Relación con el tipo equivocado (D1).** `ERR_HERENCIA_TIPO_INCORRECTO`
  cubre tres variantes: una clase que extiende una interfaz, una clase que
  implementa una clase y una interfaz que extiende una clase. Lo reporta
  `TablaSimbolos.chequearCategoriaSupertipo()`, llamado desde
  `Clase/Interfaz.estaBienDeclarado()` (paso 1 de `consolidar()`), así sale
  una sola vez aunque la entidad termine marcada en un ciclo. El lexema y la
  línea son los del tipo nombrado; la descripción dice qué se esperaba
  (`"Ii" no es una clase`, `"Aa" no es una interfaz`), con el campo
  `detalle` de `ErrorSemantico`, que nunca va en la etiqueta. La
  consolidación ignora la relación: la clase queda con `Object` como padre
  efectivo y se sigue chequeando lo demás.
- **Herencia múltiple sólo entre interfaces** (ADR 0005). Una clase
  implementa como máximo una interfaz; `class X implements A, B` es un
  error sintáctico.

### Clave y signatura

- **Clave (nombre, aridad).** `getClave()` (antes `getFirma()`) en
  `Metodo`, `Constructor` y `Atributo` devuelve `nombre/aridad` en un
  método, la aridad en un constructor y el nombre en un atributo. Los mapas
  de `Clase` e `Interfaz` se indexan con ella. Dos métodos del mismo tipo
  declarado con la misma clave son siempre una declaración repetida, aunque
  difieran los tipos de los parámetros o el retorno: `imprimir(int)` +
  `imprimir(char)` da `ERR_METODO_DUPLICADO` sobre la segunda. Lo mismo
  pasa con los constructores (`ERR_CONSTRUCTOR_DUPLICADO`). Las
  descripciones hablan de "la misma cantidad de parámetros" (D4).
- **Signatura.** `Metodo.mismaSignatura(otro, vista)` compara la cantidad de
  parámetros, la cantidad de parámetros de tipo propios, los tipos de los
  parámetros en orden y el retorno (`void` sólo es igual a `void`). Los
  nombres de los parámetros no cuentan. `vista` se aplica a los tipos de
  **los dos** métodos antes de comparar: es la instanciación genérica (ver
  abajo). Hace falta en los dos lados porque ambos métodos pueden venir de
  ancestros genéricos distintos: en `interface Lista<U> extends Fuente<U>,
  Origen<U>`, el `T get()` de `Fuente` y el de `Origen` son la misma
  signatura sólo si los dos pasan a `U`. `mismaSignatura(otro)` es la
  versión sin vista.
- **Igualdad de `Tipo`.** Dos tipos son iguales si coinciden la cabeza, el
  argumento (recursivo), las **dimensiones** y, en un parámetro de tipo, el
  origen. `Tipo` guarda las dimensiones (el parser ya no las descarta), así
  que `int[]` e `int` son distintos. La jerarquía completa de `Tipo` (ver
  "Diseño: jerarquía `Tipo`") queda para la Etapa 4.
- **Parámetros de tipo por origen.** Un `Tipo` de parámetro de tipo
  (`ID_GEN`) guarda la entidad que lo declara (clase, interfaz o método),
  resuelta en el control de declaraciones, y la igualdad compara nombre y
  origen (§5.1: "conservar su declaración de origen, además del nombre").
  En `class D<T> extends Base`, el `T get()` heredado de `Base<T>` devuelve
  el `T` de `Base`, que no es el `T` de `D`.
- **Parámetros de tipo de método (D6).** Son la excepción: al comparar dos
  signaturas se emparejan por posición. Dentro de `mismaSignatura`, después
  de la vista, cada `idGen` cuyo origen es `otro` se renombra al parámetro
  de tipo de la misma posición en `this`. Así `<V> V id(V x)` y
  `<U> U id(U x)` tienen la misma signatura, y un método genérico y uno no
  genérico con la misma clave difieren.

### Redefinición y estáticos

`Clase.consolidarConPadre()` recorre los métodos del padre (ya consolidado,
así que trae también los de sus ancestros). Si la hija no tiene la clave, el
método se copia, también si es estático: en la Etapa 4, `A.debugPrint(3)`
tiene que encontrarlo en `A`. Si la tiene, `chequearRedefinicion()` aplica
una sola regla por método, en este orden, y reporta sobre el método de la
hija:

1. **Estáticos** (`ERR_REDEFINE_ESTATICO`, §6.2): si el heredado es
   estático, ningún descendiente declara otro método con su clave, sea
   estático o de instancia (así, `void debugPrint(int x)` es un error en
   cualquier clase); y un estático no puede tener la clave de un método de
   instancia heredado.
2. **Final** (`ERR_METODO_REDEFINE_FINAL`): si el heredado es `final`, no se
   mira la signatura.
3. **Redefinición exacta** (`ERR_REDEFINICION_INCOMPATIBLE`): si no, la
   signatura del propio tiene que ser exactamente la del heredado, vista
   con la instanciación de la hija. Por ejemplo, `int toString()` en
   cualquier clase es un error.

### Instanciación genérica al comparar

- `Tipo.sustituir(Map<OrigenParametroTipo, Tipo> instanciacion)` se adelanta
  de Etapa 4 (decisión Q18 de `etapa4-semantico2/requerimientos.md`) y en
  Etapa 3 se usa **sólo para comparar** signaturas: en la redefinición, en
  los contratos de interfaz y en el conflicto entre superinterfaces.
  Devuelve una vista: cada `idGen` cuyo origen tiene entrada en el mapa se
  reemplaza por el argumento (sumando dimensiones: `T[]` con `T := int` es
  `int[]`), a cualquier profundidad. La clave del mapa es el origen (el tipo
  que declara el parámetro), no el nombre: alcanza porque cada tipo declara
  a lo sumo un parámetro. Los miembros copiados a la hija no se reescriben
  (§5.1: "estas vistas no deben modificar las declaraciones originales").
  Se apoya en `Tipo.reemplazarParametros()`, que también usa D6.
- **Composición en cadena.** Al consolidar, cada clase arma su mapa
  `ancestro → argumento visto desde acá` (`Clase.getInstanciacion()`) con
  `Tipo.componerInstanciacion(mapaDelPadre, padre, argumento)`: agrega
  `padre → argumento` y sustituye en cada entrada del mapa del padre. Con
  `Base<T>`, `Intermedia<U> extends Base<U>` y
  `Final extends Intermedia<String>`, en `Final` queda
  `{Intermedia → String, Base → String}`. El argumento sólo cuenta si el
  padre resuelto es genérico.
- Una interfaz arma el suyo igual (`Interfaz.getInstanciacion()`), uniendo
  lo que compone cada superinterfaz (`Interfaz.unirInstanciacion()`). Si un
  mismo ancestro genérico llega por dos caminos con argumentos distintos
  (`Dd extends Bb, Cc`, con `Bb extends Aa<String>` y
  `Cc extends Aa<Object>`), es `ERR_HERENCIA_ARGUMENTOS_DISTINTOS` sobre la
  interfaz que los une, una vez por interfaz. La spec no fijaba una regla:
  antes ganaba el último padre, y el resultado dependía del orden del
  `extends`. Se eligió la más conservadora, la de Java, que no deja heredar
  dos parametrizaciones del mismo tipo. El ancestro en conflicto queda sin
  entrada en el mapa (su parámetro se conserva, como en D5), así ninguna
  comparación depende del orden. Los métodos se heredan recién después de
  unir todas las ramas. Un camino sin argumento no deja entrada y no choca
  con uno que sí la tiene.
- **Herencia sin argumento (D5).** Si el ancestro no tiene entrada, su
  parámetro se conserva y no se reemplaza por `Object`, como pide el §5:
  `Object get()` no redefine el `T get()` de `Base` heredado sin argumento.

### Interfaces

- **Contrato** (`Clase.consolidarInterfaces()`). La vista es la del
  argumento de `implements` compuesto con el mapa de la interfaz
  (`componerInstanciacion(interfaz.getInstanciacion(), interfaz,
  argumento)`), así cubre sus superinterfaces. No hace falta unirle el mapa
  de la clase: la gramática no deja escribir `extends` e `implements`
  juntos, así que una clase con interfaz tiene a `Object` de padre. Para
  cada método de la interfaz (propios y heredados):
  1. Si la clase no tiene la clave (propia o heredada), o sólo tiene un
     estático con ella: `ERR_METODO_INTERFAZ_NOIMPLEMENTADO` sobre el
     método de la interfaz. Un estático no cumple un contrato (§6.2).
  2. Si la clave es la de un método de instancia de `Object` (`toString`),
     se saltea: el resultado ya lo deciden la interfaz contra `Object`
     (abajo) y la clase contra `Object`, su padre efectivo (la
     redefinición). Repetirla daba un segundo error idéntico sobre el
     método de la clase (`semError94`), o uno sobre un token sintético de
     la línea 0 si la clase heredaba el `toString` de `Object`. Así
     `toString` heredado cumple `String toString()`.
  3. Si la signatura difiere: `ERR_REDEFINICION_INCOMPATIBLE` sobre el
     método de la clase.
- **Conflicto entre superinterfaces.** Al consolidarse, una interfaz copia
  los métodos de sus superinterfaces (`Interfaz.heredarMetodosDe()`; un
  método propio con la misma clave no se pisa).
  `ERR_METODO_RETORNO_INCOMPATIBLE` compara la signatura completa, vista
  con el mapa de la interfaz, y su descripción es "se hereda con
  signaturas incompatibles". Cubre `interface X extends I1, I2` cuando `I1`
  e `I2` traen la misma clave con otra signatura, y se reporta sobre el
  método de la segunda. En un diamante llega el mismo método por los dos
  lados y no se compara.
- **Redefinición en una interfaz.** Si la interfaz declara un método con
  una clave heredada, lo redefine con la regla de la redefinición de
  clases: la signatura instanciada tiene que ser exactamente igual, y si
  no, `ERR_REDEFINICION_INCOMPATIBLE` sobre el método de la interfaz
  (`semError91`). Se reporta una vez por método aunque varios padres
  traigan la clave. El propio oculta a los heredados: entre ellos ya no se
  comparan, como en Java.
- **Contra `Object`** (`Interfaz.heredarDeObject()`, spec 0002). Como en
  Java, una interfaz contiene los métodos **de instancia** de `Object`.
  Al final de su consolidación, después de heredar de sus superinterfaces,
  copia los que no tiene (el mismo objeto `Metodo` de `Object`, así que un
  diamante no es conflicto). Un propio con esa clave lo redefine, con la
  regla de la redefinición y sin vista: `interface I { int toString(); }`
  da `ERR_REDEFINICION_INCOMPATIBLE` sobre el método de la interfaz, una
  sola vez aunque un padre ya traiga el `toString` de `Object`.
  `debugPrint` es estático y no pasa, como en Java. El contrato de una
  clase saltea los métodos copiados de `Object`: la clase siempre los
  tiene por su padre efectivo. Hasta la spec 0002 la interfaz sólo
  comparaba y no copiaba.
- **Descripción.** `ERR_REDEFINICION_INCOMPATIBLE` dice "el método x
  redefine o implementa un método con otra signatura": cubre la
  redefinición, el contrato y la comparación contra `Object`.

### Constructores

- **Nombre (D3).** Un constructor cuyo nombre no es el de su clase da
  `ERR_CONSTRUCTOR_NOMBRE_INVALIDO` con su lexema y su línea (§6.3). Lo
  reporta `Clase.agregarConstructor()` al parsearlo, y el constructor no se
  registra: no entra al control de duplicados ni al de parámetros.
- **Por defecto.** Si una clase del programa no tiene constructores
  registrados, recibe uno sin parámetros y con cuerpo vacío (§6.3), con el
  token del nombre de la clase (así la línea es la de su declaración):
  `Clase.agregarConstructorPorDefectoSiFalta()`, llamado en el paso 1 de
  `TablaSimbolos.consolidar()`, antes de su `estaBienDeclarado`. Un
  constructor con nombre inválido no cuenta. `Object` y `String` lo
  reciben al registrarse; `System` no. Los constructores no se heredan.
  `Clase.getConstructores()` los expone, de sólo lectura, para la Etapa 4.

### Casos de prueba y verificación

Los casos están listados en `casos-de-prueba.md`, en "Tipos predefinidos,
clave y redefinición" (§5.6), y todos pasan. Al cerrar la spec, todos los
testers de Etapas 1–3 dan 263 tests en verde. Los casos existentes que
cambiaron de resultado por una regla de la Parte I se corrigieron en su
etiqueta esperada, no en el código: `semCorrecto14`/`15` pasaron a
`semError86`/`87` (clave por aridad) y `semError37` cambió de etiqueta
(contrato con otra signatura).

El code review de la spec agregó `semError90`–`94`, `semCorrecto35` y
`semCorrecto36` ("Casos del code review" en `casos-de-prueba.md` §5.6), y
los testers pasan a dar 270 tests. Uno de esos casos es sintáctico: un
parámetro sin tipo (`void m(int a, )`) dejaba un `Parametro` con tipo
`null` (`tipo()` devuelve `null` tras un error sintáctico), y el control de
declaraciones, que corre aun con errores sintácticos, terminaba con una
`NullPointerException`. `Parametro.estaBienDeclarado()` ahora saltea ese
tipo, como `Tipo` con un argumento `null`.

## Desvíos respecto del glosario y el enunciado

Lugares donde el código o los textos de diseño contradicen
`Documentación/GLOSARIO.md` o el enunciado
(`enunciados/MiniJava-Semantica-Completa-2026.pdf`). Quedan anotados para
corregirlos después. Las desviaciones
**deliberadas** no van acá: tienen su ADR en `Documentación/adr/` (por
ejemplo, `0001-tipos-raw-en-declaraciones.md`).

Los desvíos de comportamiento que había anotados acá (clave de método,
métodos estáticos y redefinición, tipos predefinidos) se corrigieron con la
spec 0001: ver "Decisiones: tipos predefinidos, clave y redefinición".
Quedan sólo los de nombres y textos.

**Nombres y textos:**

1. **Pasadas.** Este documento habla de "Pasada 1" y "Pasada 2" (sección
   "Acciones semánticas sobre la gramática original") y de "chequeo de
   corrección". El glosario usa **Construcción de la tabla**, **Control de
   declaraciones** y **Consolidación**. La "Pasada 2" de este documento es
   parte de la construcción de la tabla, no del control de declaraciones.
2. **`consolidar()` hace más que consolidar.** En `TablaSimbolos` ya está
   separado: `estaBienDeclarado()` corre el control de declaraciones y
   `consolidar()` sólo la consolidación. Pero `Clase.consolidar()` hace parte del control de
   declaraciones (circularidad, `sealed`/`final`, contratos de interfaz)
   junto con la consolidación, porque esos controles necesitan recorrer la
   herencia.
3. **"Clase" para referirse a los dos.** `TablaSimbolos.clases`,
   `insertarClase`/`buscarClase`, `EntidadDeclarada` y `ERR_CLASE_DUPLICADA`
   se usan también para interfaces. El glosario dice **Tipo declarado** y no
   usa "clase" como sinónimo de interfaz.
4. **"Unidad actual" en Sem2.** `etapa4-semantico2/requerimientos.md`
   llama "unidad actual" a los tres campos (método, constructor y atributo
   inicializado). Según el glosario, un atributo inicializado no es una
   **Unidad**; lo que agrupa a los tres es el **Contexto de chequeo**.
