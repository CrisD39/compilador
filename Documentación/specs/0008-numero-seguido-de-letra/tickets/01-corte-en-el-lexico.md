---
titulo: Corte en el léxico
estado: hecho
spec: ../../0008-numero-seguido-de-letra.md
---

# 01: Corte en el léxico

**Qué se construye:** un literal numérico termina donde deja de valer su
estructura y lo que sigue arranca un token nuevo, sin error léxico.
Después de `dígitos .`, una letra que no sea `e`/`E`/`f`/`F`/`d`/`D` (o un
`_`) hace que el `.` no sea parte del número: se emite `intLiteral` y
después `punto`. `4.metodo1()` pasa a dar un error sintáctico sobre el `.`.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (regla general), D2 (dígitos seguidos de
`.`), D3 (token en reserva), D4 (lo que no cambia) y D5 (contrato de
salida).

**Dónde:**
- `AnalizadorLexicoImpl`:
  - `estadoEntero`, `estadoDigitosExponente` y `cerrarLiteralFloat`: ante
    una letra o `_`, cierran el literal en vez de llamar a
    `consumirSufijoInvalidoDeNumero`, que se borra con su mensaje.
  - `estadoParteDecimal`: si no llegó ningún dígito después del `.` y el
    carácter es una letra que no sea `e`/`E`/`f`/`F`/`d`/`D`, o un `_`,
    arma `LIT_INT` con los dígitos (sin el `.`) y deja `PUNTO` en reserva.
  - El token en reserva: un campo, que `startAnalizar` y `nextToken`
    devuelven antes de llamar a `estadoInicial()`. Comentario con el porqué
    (no hay `unread` en `SourceManager`).
- Casos léxicos (`resources/conErrores/` → `resources/sinErrores/`):
  `lexConErrores13`, `15`, `16`, `17` y `18` se mudan como casos de tokens
  con la partición nueva; `lexConErrores14` queda con `[Error:_|2]`.
- Casos léxicos nuevos en `resources/sinErrores/`: `3.f`, `3.e5`, `3.14fx`,
  `3e10x`, `4.metodo1`, `4.foo`.
- Casos sintácticos nuevos en `resources/sintactico/conErrores/`
  (`sintError64` en adelante): `x = 4.metodo1();` → `[Error:.|L]`,
  `x = 345a;` → `[Error:a|L]`, `3.a` en una expresión.

**Criterios:**
- [x] `4.metodo1` da los tokens `4`, `.`, `metodo1` y ningún error léxico.
- [x] El archivo del reporte en ET4 da un solo error, sintáctico, sobre el
      `.`.
- [x] `3.f`, `3.14f`, `3.e5` y `3e10f` siguen siendo un único
      `floatLiteral`.
- [x] `3e`, `4.e` y `1234567890` siguen siendo errores léxicos.
- [x] Ya no existe el mensaje "un literal numérico no puede ir seguido
      inmediatamente de una letra o guion bajo".
- [x] `./test.sh` en verde.
