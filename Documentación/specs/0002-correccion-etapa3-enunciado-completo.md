---
titulo: Corrección de Etapa 3 según el enunciado completo
estado: hecho
origen: revisión del enunciado completo (2026-10-23)
fecha: 2026-10-23
---

# Corrección de Etapa 3 según el enunciado completo

## Problem Statement

La spec 0001 corrigió el control de declaraciones tomando como referencia
la "Semántica de MiniJava – Parte I. Declaraciones". El documento completo
("Semántica de MiniJava – Documento completo", v3.0) dice en su §1 que
define los controles de las **Etapas 3 y 4**, y que retoma la Parte I "con
las aclaraciones de genericidad y contexto estático definidas en este
documento". Comparado con ese documento, el compilador se comporta distinto
en dos puntos de la Etapa 3:

- **Acepta tipos raw.** `Caja dato;`, `Caja obtener()`, `void poner(Caja c)`,
  `Caja[] arr` y `Lista<Caja> x` dan `[SinErrores]`. El §5.2 ("Ausencia de
  tipos raw") los prohíbe fuera de `extends`/`implements`, el §3 prohíbe un
  genérico sin argumento como argumento de otro, y el §8 ubica el control
  en la segunda pasada de declaraciones. El ADR 0001 los aceptaba
  apoyándose en la Parte I §3; el ADR 0007 lo reemplaza.
- **Las interfaces no tienen los métodos de `Object`.** Una interfaz sólo
  compara sus métodos contra los de `Object`, pero no los incorpora. Como
  en Java, una interfaz contiene implícitamente los métodos de instancia
  públicos de `Object`, así que `i.toString()` con `i` de tipo interfaz es
  válido (§10.2: "Las interfaces permiten seleccionar sus métodos
  declarados y heredados"). La Etapa 4 va a buscar los métodos de un
  receptor interfaz en la tabla consolidada, y hoy no encontraría
  `toString`.

## Solution

El control de declaraciones rechaza todo uso de una entidad genérica sin
argumento fuera de la cláusula de herencia o implementación, con un error
sobre el nombre del tipo. La consolidación de una interfaz incorpora los
métodos de instancia de `Object` que la interfaz no redefine. Ningún error
de los que ya se reportaban cambia ni se duplica.

Lo que corresponde a la Etapa 4 (receptores arreglo y parámetro de tipo,
logros que cambian casos base) se documenta en el diseño de la Etapa 4,
sin código.

## User Stories

**Tipos raw**

1. Como programador MiniJava, quiero que `Caja dato;` con `Caja<T>`
   genérica dé un error sobre `Caja`, porque MiniJava no tiene tipos raw
   (§5.2).
2. Como programador MiniJava, quiero el mismo error en el retorno de un
   método (`Caja obtener()`).
3. Como programador MiniJava, quiero el mismo error en un parámetro de
   método o de constructor.
4. Como programador MiniJava, quiero el mismo error en el tipo base de un
   arreglo (`Caja[] arr`).
5. Como programador MiniJava, quiero el mismo error cuando un genérico
   sin argumento es el argumento de otro (`Lista<Caja>`), como dice el §3.
6. Como programador MiniJava, quiero que el argumento de una herencia
   también se controle (`class Bb extends Caja<Lista>` es error sobre
   `Lista`), porque la excepción de herencia sólo cubre la cabeza.
7. Como programador MiniJava, quiero poder seguir heredando sin argumento
   (`class B extends Caja`, `implements I`, `interface J extends I`), como
   permite el §5.
8. Como programador MiniJava, quiero que la descripción diga que el tipo
   genérico necesita un argumento de tipo, para entender el error.
9. Como programador MiniJava, quiero que cada uso raw sea un error aparte
   (Logro 4), para ver todos en una corrida.
10. Como programador MiniJava, quiero que un tipo raw en un método estático
    se reporte igual que en uno de instancia.

**Interfaces y `Object`**

11. Como programador MiniJava, quiero que toda interfaz contenga los
    métodos de instancia de `Object` (`toString`), para poder llamarlos
    sobre un receptor de tipo interfaz en la Etapa 4.
12. Como programador MiniJava, quiero que los estáticos de `Object`
    (`debugPrint`) no pasen a las interfaces, porque las interfaces no
    tienen estáticos y un `void debugPrint(int)` propio sigue siendo
    válido.
13. Como programador MiniJava, quiero que `String toString();` declarado en
    una interfaz siga siendo válido.
14. Como programador MiniJava, quiero que `int toString();` en una
    interfaz siga dando un único error de redefinición incompatible,
    también si la interfaz extiende otra.
15. Como programador MiniJava, quiero que una interfaz que extiende dos
    interfaces no reporte conflicto por el `toString` que llega por las
    dos.
16. Como programador MiniJava, quiero que una clase que implementa una
    interfaz no necesite declarar `toString`, porque lo hereda de `Object`.
17. Como programador MiniJava, quiero que una clase con
    `static String toString()` que implementa una interfaz reporte sólo
    el error de estático, sin un error extra de método no implementado en
    la línea 0.

**Proceso**

18. Como desarrollador, quiero una línea de base antes del primer cambio y
    todos los testers de Etapas 1–3 en verde al final de cada ticket.
19. Como desarrollador de la Etapa 4, quiero que las decisiones que
    surgieron de esta revisión y la tocan queden escritas en su diseño.
20. Como desarrollador, quiero que la documentación de la Etapa 3 y la spec
    0001 dejen de describir los tipos raw como aceptados.

## Implementation Decisions

**Tipos raw (ADR 0007).**
- La validación de un tipo referencia distingue dos usos: como tipo de una
  declaración (atributo, parámetro, retorno, arreglo) y como supertipo de
  una relación de herencia o implementación. Sólo el segundo admite una
  cabeza genérica sin argumento.
- El argumento genérico se valida siempre sin esa excepción, en los dos
  usos.
- Código nuevo, `ERR_TIPO_SIN_ARGUMENTO`: "el tipo genérico X necesita un
  argumento de tipo". El lexema y la línea son los del nombre del tipo
  (`Caja`), igual que `ERR_TIPO_NO_GENERICO`.
- Un tipo no declarado no da además este error, y un tipo no genérico con
  argumento sigue dando `ERR_TIPO_NO_GENERICO`.

**Interfaces y `Object`.**
- Al consolidar una interfaz, después de heredar de sus superinterfaces,
  se incorporan los métodos de instancia de `Object` cuya clave la
  interfaz no tiene. Se comparte el mismo objeto `Metodo` de `Object`.
- La comparación de un método propio contra `Object` (historias 62 y 63 de
  la spec 0001) pasa a ese mismo paso. Si el método propio ya se reportó
  como redefinición incompatible contra el heredado de una superinterfaz,
  no se vuelve a reportar.
- El contrato de una clase saltea los métodos de la interfaz que son los
  de `Object`: la clase los tiene siempre por su padre efectivo, y su
  redefinición ya la controla la regla de la redefinición.
- Reemplaza la decisión de la spec 0001 "La interfaz no copia los miembros
  de `Object`".

**Contrato de salida.** No cambia.

## Testing Decisions

Igual que la spec 0001: se observa sólo la salida del compilador, a través
de los testers parametrizados del semántico de declaraciones.

**Casos nuevos:**
- Con errores, `semError98`–`104`, un uso raw por caso: atributo, retorno,
  parámetro de método, parámetro de constructor, tipo base de arreglo,
  argumento genérico y argumento de la herencia.
- Sin errores, `semCorrecto38`: herencia sin argumento en `extends`,
  `implements` y extensión de interfaces.
- Sin errores, `semCorrecto39`: interfaces con `toString` heredado de
  `Object`, redeclarado con la misma signatura y llegado por dos
  superinterfaces, y una clase que implementa sin declararlo.

**Casos existentes:** `semCorrecto22` deja de declarar `Lista cruda;`. Es
el único caso de las Etapas 2–4 con un tipo raw fuera de la herencia.

**Sin tester:** que un error no se duplique. El tester sólo busca la
etiqueta, así que se verifica a mano contando las etiquetas en la salida
(interfaz con `int toString()` que extiende otra; clase con
`static String toString()` que implementa una interfaz).

## Out of Scope

- Todo el chequeo de sentencias de la Etapa 4. Esta revisión deja anotado
  en `etapa4-semantico2/requerimientos.md`:
  - receptor arreglo con los métodos de instancia de `Object`;
  - receptor de tipo parámetro de tipo sin miembros;
  - logros que cambian el resultado de casos base (pendiente);
  - tipos raw en locales, `for`/`foreach` y `new`.
- Los ejemplos de atributos `static` de `requerimientos.md` y los nodos de
  inicializadores de arreglo quedan como están.

## Further Notes

- Documento de referencia: "Semántica de MiniJava – Documento completo"
  (v3.0, 2026-10-05), que incluye a la Parte I. Ante lo no especificado,
  Java.
- Decisiones registradas: ADR 0007 (reemplaza al 0001).
- El resto de los ADR se revisó contra el documento completo y no cambia:
  0004 (lo no especificado sigue a Java), 0005 (el §4 lo confirma) y 0006
  (sale del §10.4).

## Tickets

En `0002-correccion-etapa3-enunciado-completo/tickets/`, uno por archivo.

| # | Ticket | Bloqueado por | Estado |
|---|---|---|---|
| 01 | Línea de base | — | hecho |
| 02 | Sin tipos raw (ADR 0007) | 01 | hecho |
| 03 | Interfaces con los métodos de instancia de `Object` | 01 | hecho |
| 04 | Cierre de documentación | 02, 03 | hecho |

Están todos hechos. Al cerrar, los testers de Etapas 1–3 dan 283 tests en
verde (léxico 14 + 24, sintáctico 37 + 63, pánico 4, semántico 37 + 104).
