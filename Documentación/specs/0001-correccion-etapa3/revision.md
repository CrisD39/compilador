---
titulo: Revisión de la spec 0001 contra la implementación
spec: ../0001-correccion-etapa3.md
fecha: 2026-10-21
rama: correccion-etapa3 (b6e0a40)
---

# Revisión de la spec 0001 contra la implementación

Se revisaron la spec, sus 12 tickets, el código de `Model/semantico` y las
acciones semánticas de `AnalizadorSintacticoImpl`. Se contrastó todo con la
Parte I y con el documento completo (`MiniJava-Semantica-Completa-2026.pdf`,
v3.0). No se cambió código.

**Estado de los testers:** los 270 tests de Etapas 1–3 dan verde (léxico
14 + 24, sintáctico 37 + 63, pánico 4, semántico 34 + 94). Se pasaron todos
los `.java` de `resources/` por el jar y ninguno termina con excepción. Se
probaron además unos 30 programas rotos a propósito, y ninguno hace caer al
compilador.

Las pruebas manuales usadas acá están en la sección final ("Casos de
prueba manuales"). Ninguna se agregó a `resources/`.

---

## 1. Lo que pide la spec y falta implementar

Las 72 historias de la spec están cubiertas. Cada caso que nombran los
tickets existe y da la etiqueta esperada. Las reglas se probaron también
fuera de los casos del repositorio (arreglos instanciados `T[]` → `String[]`,
clase genérica que implementa `Prov<U>`, D6 en interfaces, shadowing de `T`
en métodos genéricos, estáticos a través de un abuelo, `System`/`Object`
redeclarados como clase o interfaz), y dan el resultado correcto.

Quedan pendientes estos puntos de documentación:

1.1. **Diagramas `.png` desactualizados.** Son del 2026-10-07. El
   `.puml` se actualizó el 2026-10-21. El ticket 12 ya lo dice ("no se
   regeneraron"), pero el ticket está marcado como hecho.

1.2. **La spec describe una API que no existe.** La spec ("Instanciación
   genérica") y `etapa4-semantico2/requerimientos.md` (líneas 190 y 377)
   hablan de `Tipo.sustituir(nombreParam, argumento)`. El código usa
   `Tipo.sustituir(Map<OrigenParametroTipo, Tipo>)`, que reemplaza por
   **origen** y no por nombre. La implementación es la correcta, porque el
   §5.1 pide conservar el origen. El riesgo es que la Etapa 4 se escriba
   siguiendo el documento y sustituya por nombre.

## 2. Lo que exige el enunciado y ninguna spec cubre

La spec 0001 toma la Parte I como referencia. El documento completo agrega
controles de **declaración**: su §8 los ubica en la segunda pasada de la
Etapa 3.

2.1. **Parámetro de tipo de la clase en el encabezado de un método estático
   (§10.4, §8).** El enunciado dice: "Se rechaza el uso del parámetro de
   tipo de la clase en encabezados y cuerpos de métodos estáticos. La
   prohibición comprende su aparición directa y dentro de tipos de arreglo
   o argumentos genéricos". Hoy se acepta:

   ```java
   class Caja<T> {
       static T identidad(T valor) { }   // debería ser error sobre T
       static void poner(Caja<T> c) { }  // ídem
       static T[] arr() { }              // ídem
   }
   ```
   Salida actual: `[SinErrores]`. Lo documenta
   `adr/0006-parametro-de-tipo-de-clase-en-estaticos.md` (propuesto) y lo
   prueban `semError95`–`97` (fallan hasta implementarlo) y `semCorrecto37`.
   Lo implementa el ticket 13 de la spec 0001 (hecho: el ADR quedó
   aceptado y los cuatro casos pasan).
   `semIIError32` sólo prueba el **cuerpo** (`new T[10]`). El cambio
   probable es que `Metodo.estaBienDeclarado()` no reciba el entorno de la
   clase cuando el método es estático. Ojo con `semCorrecto19`
   (`static <T> T identidad(T x)`): ahí el `T` es propio del método y
   tiene que seguir siendo válido.

2.2. **Tipos raw (§3, §5.2).** El documento completo los prohíbe, y también
   un argumento que nombra un genérico sin su propio argumento
   ("clase o interfaz existente **no genérica**"). Hoy `Caja crudo;` y
   `Caja<Caja> dato;` dan `[SinErrores]`. El ADR 0001 decide
   deliberadamente aceptar `Caja dato;`, así que no es un error. Pero el ADR
   no menciona el caso del argumento (`Caja<Caja>`), y la spec justifica el
   desvío con "la Parte I §3 los admite", sin citar el documento completo,
   que dice lo contrario. Conviene que el ADR diga explícitamente si cubre
   también el argumento.

## 3. Lo implementado que nadie pidió

3.1. **`ERR_HERENCIA_ARGUMENTOS_DISTINTOS` y la redefinición dentro de
   interfaces** (commit `ec36953`, posterior al cierre de la spec). Una
   interfaz que redeclara un método heredado con otra signatura da
   `ERR_REDEFINICION_INCOMPATIBLE` (`semError91`). Un ancestro genérico que
   llega por dos caminos con argumentos distintos da un código nuevo
   (`semError92`, `93`). Las dos reglas son razonables por la regla de oro
   (Java hace lo mismo) y están documentadas en `casos-de-prueba.md` y
   `analizador-semantico.md`. Pero no están en la spec ni en ningún ticket:
   la spec dice "Todos los tickets están hechos" y no las registra. Hay que
   agregarlas a la spec (historia y ticket) o decidir explícitamente que
   quedan fuera.

3.2. **Restos sin uso.** Nada los usa, y el AST está fuera del alcance de
   la spec:
   - `Model/semantico/NodoSentencia.java`: una interfaz vacía. Entró con el
     commit de línea de base `c4789e7`, que además agregó skills,
     `CLAUDE.md` y otros archivos.
   - `Metodo.variablesLocales` y `Metodo.miClase`: campos de la Etapa 3
     original que nunca se asignan, con comentarios del tipo "No sé como
     hacer…".

   No rompen nada, pero en `Metodo` se leen como diseño vigente.

## 4. Lo que parece hecho pero está mal

4.1. **Dos casos `multiError` no prueban lo que dicen.**
   `semMultiErrorCicloMasClaseSana.java` y
   `semMultiErrorDosCiclosIndependientes.java` usan `class A`, `class B`,
   `class X` y `class Y`. Según el léxico de 2026, una mayúscula sola es un
   `idGen`, así que el compilador da **errores sintácticos**
   (`se esperaba "idClase" y se encontró idGen ("A")`), no ciclos de
   herencia. Las etiquetas "esperadas" de sus comentarios (`[Error:B|7]`,
   `[Error:B|10]`, `[Error:Y|12]`) aparecen por casualidad, porque el error
   sintáctico cae en la misma línea y con el mismo lexema.
   `casos-de-prueba.md` (línea 408) afirma que el segundo da "dos
   `ERR_HERENCIA_CICLICA` (líneas 10 y 12)". Ningún tester corre
   `multiError/`, y por eso nadie lo notó. Hay que renombrar las clases a
   dos letras (`Aa`, `Bb`, …).

4.2. **El conflicto entre superinterfaces se reporta sobre el método del
   padre, y puede repetirse.** `Interfaz.heredarMetodosDe()` reporta
   `ERR_METODO_RETORNO_INCOMPATIBLE` sobre el método del padre posterior
   en la lista de `extends`, no sobre la interfaz que los combina. Pasan dos
   cosas:

   ```java
   interface Uno { int m(); }
   interface Dos { char m(); }        // línea 3
   interface Xx extends Uno, Dos { }
   interface Yy extends Uno, Dos { }
   ```
   La salida es `[Error:m|3]` **dos veces**, una por `Xx` y otra por `Yy`.
   Las dos líneas son idénticas y ninguna nombra a la interfaz que tiene el
   problema. La declaración señalada (`Dos.m`) está bien escrita.

   Esto choca con la historia 4 ("cada error de declaración se reporte una
   sola vez") y con la convención del `CLAUDE.md` ("the lexeme is the name
   of the entity that triggers the error"). Es una decisión anterior a la
   spec (`semError36`: "Se reporta sobre el getValor de Beta"), pero la
   spec no la revisó. Si se mantiene, conviene registrarla en un ADR. Si se
   cambia a la interfaz que combina (`[Error:Xx|4]`), hay que corregir las
   etiquetas de `semError36`, `44` y `62`.

4.3. **Comentario que contradice la gramática.** El comentario de
   `Interfaz.heredarMetodosDe()` da como ejemplo
   `"extends A<int>, B<int>"`, pero la gramática no admite argumentos
   primitivos (documento completo §3). Es menor, pero confunde a quien lea
   el código para la Etapa 4.

## Casos de prueba manuales

Se corrieron con `java -jar out/compilador.jar` y no se agregaron a
`resources/`. Dan lo esperado (sin hallazgo):

| Programa | Salida |
|---|---|
| `class Cc<U> implements Prov<U> { U get() {} }` | `[SinErrores]` |
| `interface Ii { <T> T id(T x); }` + `class Cc implements Ii { <U> U id(U x) {} }` | `[SinErrores]` |
| `Base<T> { T[] get() }` + `Dd extends Base<String> { String[] get() }` | `[SinErrores]` |
| `Base<T> { <T> T m(T x) }` + `Dd extends Base<String> { <U> U m(U x) }` | `[SinErrores]` |
| ídem con `String m(String x)` en `Dd` | error sobre `m` (genérico contra no genérico) |
| `interface Object {}` + `class Aa { int toString() {} }` | duplicada + redefinición incompatible |
| `class Aa { static void m() {} }`, `Bb extends Aa`, `Cc extends Bb { static void m() {} }` | `ERR_REDEFINE_ESTATICO` sobre `Cc.m` |
| `class Aa implements Ii { void debugPrint(int x) {} }` | `ERR_REDEFINE_ESTATICO` |
| `interface Ii { String toString(); }` + `class Cc implements Ii { static String toString() {} }` | estático + no implementado |
| `interface Kk<W> extends Ii<W>, Jj<W>` (diamante con el mismo argumento) | `[SinErrores]` |

Con hallazgo: 2.1 (`static T identidad(T valor)`), 2.2 (`Caja<Caja>`) y
4.2 (dos interfaces que combinan el mismo par).
