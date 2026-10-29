# El postfijo vale como asignación en la inicialización y la actualización del for

**Estado:** aceptado e implementado (2026-10-29, al revisar la interacción
de los logros cerrados de la Etapa 4).

El logro *For* dice que en el for clásico "el primer componente [es] una
asignacion o una declaracion var [...] y el tercero una asignación", y así
se implementó: `for (int i = 0; i < n; i++)` era `ERR_FOR_ACTUALIZACION`.
Java admite ahí cualquier expresión-sentencia (JLS §14.14.1): una
asignación, `i++`/`i--`, una llamada o un `new`.

Se decide que **un postfijo (`i++`, `i--`) cuenta como asignación en la
inicialización y en la actualización del for**. Una llamada y un `new`
siguen siendo error. La regla queda "una asignación o un postfijo" en las
dos puntas (en la inicialización, además, un `var` o una local clásica).

El logro *For* se puede hacer sin *Postfijos*, y en MiniJava base no existe
`++`: por eso el logro nombra sólo la asignación. Con *Postfijos*
implementado, `i++;` ya es una sentencia válida en un bloque, y rechazarla
en el for era una inconsistencia, además de rechazar la forma canónica del
for de Java. Es el mismo razonamiento por el que la spec 0009 admitió la
local clásica en la inicialización.

## Detalle

- `NodoFor.esAsignacionOPostfijo()` mira sólo la raíz de la expresión:
  tiene que ser un `NodoExpAsignacion` o un `NodoExpPostfija`. Así,
  `i++ + 1`, `(i++)` y `-i++` no son postfijos y dan
  `ERR_FOR_ACTUALIZACION` (o `ERR_FOR_INICIALIZACION`), igual que en Java.
- El postfijo se chequea con `NodoExpPostfija.chequear()`, como en
  cualquier sentencia: operando `int` (`ERR_OPERADOR_INCOMPATIBLE`) y
  destino asignable (`ERR_DESTINO_NO_ASIGNABLE`), con los errores sobre el
  operador. `i++--` pasa el filtro del for y falla ahí.
- El parser no cambia: las dos cláusulas ya se leían como `<Expresion>`.
  Tampoco cambia el flujo (`NodoFor.salida()` sólo mira la condición).

## Opciones consideradas

- **Sólo asignación, al pie de la letra del logro.** Se descartó por la
  inconsistencia con `i++;` como sentencia.
- **Postfijo sólo en la actualización.** Es donde se usa siempre, pero deja
  las dos cláusulas con reglas distintas sin ninguna razón.
- **Cualquier expresión-sentencia, como Java.** Contradice el texto del
  logro, que dice "asignación", y no lo pide nadie.

## Consecuencias

- `semIIForError02` (`i++` en la actualización) pasó a `semIIForCorrecto11`.
  Casos: `semIIForCorrecto11`–`12` y `semIIForError28`–`31`.
- Una llamada o un `new` en la actualización siguen siendo error, a
  diferencia de Java. El informe de la Etapa 4 tiene que decirlo.
