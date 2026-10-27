---
titulo: Revisión de la spec 0006 contra la implementación
spec: ../0006-super.md
fecha: 2026-10-27
rama: spec0006 (e78071d)
---

# Revisión de la spec 0006 contra la implementación

Se revisaron los cuatro commits de la spec (`c0a06b4~1..e78071d`): el logro
*Super!*, sus tickets y el código que tocan en `Model/semantico`. No se
cambió código.

**Estado de los testers:** `./test.sh` da verde en todos (léxico 14 + 24,
sintáctico 38 + 63, pánico 4, semántico 37 + 104, Sem2 92 + 171, logros
154). Se pasaron además por el jar unos 25 programas propios: cadenas
genéricas (`B<T> extends A<T>`, `C extends B<String>`), `super.x++`,
`super.m().m()`, `super(...)` fuera de lugar, constructores private y el
contexto previo a la construcción. Ninguno da un resultado incorrecto.

No hay errores de corrección. Lo que sigue es un comportamiento a decidir
(§1), limpieza de código (§2) y errores de documentación (§3).

---

## 1. Comportamiento a decidir

1.1. **`super(...)` con una superclase heredada sin argumento.** La
   excepción de herencia del ADR 0007 permite `class Bb extends Aa` con
   `Aa<T>` genérica. Entonces el parámetro `T` del constructor
   `Aa(T t)` se conserva (spec 0001, D5: no se reemplaza por `Object`), y
   ningún argumento concreto conforma con él:
   ```java
   class Aa<T> { Aa(T t){} }
   class Bb extends Aa { Bb(){ super(5); } }      // [Error:super|2]
   class Bb extends Aa { Bb(){ super(null); } }   // [SinErrores]
   ```
   No es un problema propio de `super`. Lo mismo pasa con un método
   heredado: `class Bb extends Aa { void p(){ m(5); } }` con
   `void m(T t)` en `Aa` da `[Error:m|2]`, y también `super.m(5)`. En Java
   las tres compilan: `T` se borra a `Object` y solo hay un warning
   *unchecked*. Esa diferencia ya está decidida para la Etapa 3. Lo que
   falta es que la Etapa 4 la deje asentada: ningún caso de
   `resources/semantico2/` cubre el uso de un miembro heredado sin
   argumento, y la spec 0006 no lo menciona.

   **A decidir:** mantenerlo (y agregar casos que lo fijen, más una línea
   en `etapa4-semantico2/requerimientos.md`) o tratar el parámetro
   conservado como `Object` en la Etapa 4. Lo segundo cambiaría la regla
   D5 de la spec 0001 y amerita un ADR.

## 2. Limpieza de código

Ninguno de estos puntos cambia la salida hoy.

2.1. **La superclase se busca dos veces.**
   `NodoLlamadaSuperConstructor.chequear()` (línea 53) hace
   `(Clase) tabla.buscarClase(tipoSuperclase.getToken().getLexema())`,
   pero la consolidación ya la dejó resuelta en `Clase.getPadreEfectivo()`
   (`Clase.java:61`, que también da `Object` sin `extends`). El cast sin
   chequear solo es seguro por el invariante D5 de la spec 0006 (sin
   errores de declaración no corre el chequeo): si se rompe, sale una
   `ClassCastException` en vez de un error.

2.2. **`chequearConstructor` recibe la clase y su tipo.**
   `ChequeoLlamada.chequearConstructor(tabla, clase, tipo, …)`
   (`ChequeoLlamada.java:46`) recibe dos parámetros que dicen lo mismo:
   la clase siempre es la entidad del tipo. Los dos llamadores la calculan
   antes (`NodoCreacionObjeto.java:58` y `NodoLlamadaSuperConstructor.java:60`).
   Se puede derivar adentro con `tabla.entidadDe(tipo)`
   (`TablaSimbolos.java:245`). Si se hace junto con 2.1, desaparece la
   búsqueda por nombre en `NodoLlamadaSuperConstructor`.

2.3. **La marca del contexto previo a la construcción no se anida.**
   `TablaSimbolos.setContextoPrevioAConstruccion(boolean)`
   (`TablaSimbolos.java:451`) se pone en `true` y el `finally` de
   `NodoLlamadaSuperConstructor` (línea 62) la vuelve a `false`, en vez de
   restaurar el valor anterior. Hoy no se anida (`super(...)` solo va
   primero en un constructor, y las lambdas no se chequean). Pero si
   algún día se anida un chequeo que también la usa, el `finally` interno
   la apaga mientras el externo sigue chequeando argumentos, y `this` o un
   atributo suelto pasan sin error. Guardar y restaurar el valor previo
   cuesta una línea.

2.4. **La marca cubre más de lo que dice la D9.** La D9 de la spec dice
   que la marca está prendida "mientras chequea sus argumentos", pero
   envuelve todo `chequearConstructor` (resolución por aridad y
   visibilidad incluidas). El comentario de las líneas 54–57 lo admite.
   Hoy da lo mismo, porque solo los argumentos la miran, pero un chequeo
   futuro dentro de `chequearConstructor` correría en el contexto
   equivocado. Se resuelve pasando la marca solo alrededor de
   `chequearArgumentos` (por ejemplo con un parámetro).

2.5. **La regla "el objeto actual no está disponible" está repetida.**
   Hay cuatro chequeos `enContextoPrevioAConstruccion()`, uno en cada
   nodo, cada uno junto a su `enContextoEstatico()`: `NodoThis.java:30`,
   `NodoSuper.java:37`, `NodoAccesoVar.java:69` y `NodoLlamadaMetodo.java:50`.
   Un nodo nuevo que use el receptor implícito tiene que acordarse de los
   dos, y si olvida el nuevo ningún test falla. Un helper en
   `TablaSimbolos` (por ejemplo `exigirObjetoActual(token, codigoEstatico)`)
   deja la regla en un solo lugar. Conviene hacerlo junto con 2.3.

2.6. **`ErrorSemantico.esSuper()` decide el texto por el lexema.**
   `ERR_ARGUMENTO_INCOMPATIBLE`, `ERR_CONSTRUCTOR_NO_ENCONTRADO` y
   `ERR_ACCESO_PRIVADO` (`ErrorSemantico.java:104`, `108` y `134`) cambian
   la descripción si el lexema es `"super"` (`esSuper()`, línea 150). La
   clase ya tiene el campo `detalle` para las variantes que el lexema solo
   no distingue (línea 20). Si otro camino reporta uno de esos códigos
   sobre un token `super` por otro motivo, sale el texto de `super(...)`.
   El cambio solo toca la descripción, no la etiqueta, así que no cambia
   ningún caso de prueba.

2.7. **`Clase.getTipoSuperclase()` crea un `Object` en cada llamada.**
   Sin `extends`, la línea 267 hace
   `new Tipo(tablaSimbolos.getObject().getNombre())` cada vez que se
   evalúa un acceso con `super` o un `super(...)`. El costo es mínimo, y
   se puede usar `getPadreEfectivo().comoTipo()` (`Clase.java:84`) o
   guardar el valor.

## 3. Documentación

3.1. **Falta una coma en el índice.** `Documentación/README.md`, fila
   `adr/`: "la 0008 (el logro de caminos de retorno reemplaza a §12.5) la
   0009 (…)". Falta la coma antes de "la 0009".

3.2. **El comentario de `ChequeoVisibilidad` está desactualizado.** El
   encabezado (`ChequeoVisibilidad.java:9`) enumera "los seis accesos a un
   miembro (x, m(), e.x, e.m(), C.m() y new C())", pero `super(...)`
   también llega a `exigirAcceso` por `chequearConstructor` (por ejemplo,
   `ERR_ACCESO_PRIVADO` sobre `super` en `semIISuperError16`). El
   encabezado de `CLAUDE.md` ("the six member accesses") tiene el mismo
   problema. Si se hace 2.6 antes, revisar también el comentario de
   `ErrorSemantico` sobre `ERR_ACCESO_PRIVADO`.

## Propuesta de tickets

Para pasar por `/to-tickets`:

1. **Decidir y fijar el uso de miembros con herencia sin argumento**
   (§1.1). Necesita la decisión del usuario antes de empezar. Sin
   bloqueos.
2. **Resolver la superclase una sola vez** (§2.1, §2.2, §2.7). Sin
   bloqueos.
3. **Contexto previo a la construcción en un solo lugar** (§2.3, §2.4,
   §2.5). Bloqueado por 2, porque los dos tocan
   `NodoLlamadaSuperConstructor.chequear()` y `chequearConstructor`.
4. **Descripciones de `super(...)` con `detalle`** (§2.6). Sin bloqueos.
5. **Documentación** (§3.1, §3.2). Bloqueado por 4 si se quiere un solo
   repaso de los comentarios.

## Casos de prueba manuales

Se corrieron con `java -jar out/compilador.jar` y no se agregaron a
`resources/`. En todos, `Aa<T>` es genérica y `Bb extends Aa` hereda sin
argumento:

| Programa | Salida |
|---|---|
| `Aa(T t){}` + `Bb(){ super(5); }` | `[Error:super|2]` (argumentos no conforman) |
| `Aa(T t){}` + `Bb(){ super(null); }` | `[SinErrores]` |
| `void m(T t){}` + `void p(){ super.m(5); }` | `[Error:m|2]` |
| `void m(T t){}` + `void p(){ m(5); }` | `[Error:m|2]` |
