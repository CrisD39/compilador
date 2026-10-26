---
titulo: Falta de retorno
estado: hecho
spec: ../../0004-caminos-de-retorno.md
---

# 01: Falta de retorno

**Qué se construye:** un método no void cuyo cuerpo termina normalmente da
`ERR_FALTA_RETORNO` sobre su nombre, antes que los errores de su cuerpo. El
análisis de flujo calcula los tres estados completos (vivo, muerto,
recuperando), pero en este ticket todavía no reporta sentencias
inalcanzables. Sólo se reconocen como condiciones constantes los literales
`true` y `false` (con paréntesis o sin) y la condición vacía del `for`.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (siempre activo en ET4), D2 (sólo métodos no
void; las inalcanzables, más adelante, también en constructores), D3
(estados y reglas de salida de cada sentencia) y D4 (falta de retorno sólo
si el cuerpo sale vivo, calculada antes de chequear el cuerpo). ADR 0008.

**Dónde:**
- La salida de cada sentencia según el esquema de D3. Las reglas son
  estructurales: no miran tipos ni la tabla de símbolos, así que se pueden
  calcular antes del chequeo del cuerpo.
- `Clase.chequearSentencias`: en cada método no void, calcular la salida del
  cuerpo y, si es vivo, registrar `ERR_FALTA_RETORNO` antes de chequearlo.
  Una salida recuperando no se reporta (D4).
- `ERR_FALTA_RETORNO` en la tabla de códigos con su descripción.
- `TesterLogrosSem2`: agregar `caminosRetornoSinCodigoMuerto` a
  `LOGROS_HABILITADOS`.
- Mudanza de los dos casos sin error de la base que tienen métodos sin
  retorno (ver Criterios).

**Criterios:**
- [x] Casos de `logros/caminosRetornoSinCodigoMuerto/` en verde: Correcto
      01 (existe), 02, 03 y 07; Error 01 (existe), 02–06 y 18.
- [x] `sinErrores/semIICorrecto31` se borra de la base y pasa a ser
      `semIIRetornoError19`, con las etiquetas `sinRetorno`, `parcial`.
- [x] `sinErrores/semIICorrecto72` (CP-SII-38) se borra de la base y pasa a
      ser `semIIRetornoError20`, con las etiquetas `sinRetornoDeclarado`,
      `parcial` y un comentario que dice de dónde viene.
- [x] Los casos 19 y 20 quedan **sin** la etiqueta `var` de la
      inalcanzable: la agrega el ticket 02.
- [x] `semIIError104` y `logros/for/sinErrores/semIIForCorrecto04` no
      cambian y siguen en verde: después de `return` o de `for (…; ; …)` el
      estado es muerto o recuperando, nunca vivo.
- [x] `./test.sh` en verde. `TesterSinErroresSem2` tiene dos casos menos.
      Las Etapas 1–3 no cambian.

**Notas de la implementación:** los casos de los tickets 02 y 03 ya estaban
escritos en la carpeta del logro y, con el logro habilitado, dejaban
`./test.sh` en rojo. Los que todavía fallan esperan en
`logros/caminosRetornoSinCodigoMuerto/pendientes/{conErrores,sinErrores}/`,
que `TesterLogrosSem2` no lee: Error 07–10, 12–17 y 21 (ticket 02), Error 11 y
Correcto 04 (ticket 03). Correcto 05, 06, 08 y 09 ya pasan y quedaron en su
lugar. En los casos 19 y 20, la línea de la etiqueta `var` es por ahora un
comentario, para no correr la numeración. `./test.sh`: Sem2 con 92 casos
sin error, 172 con error y 94 de logros, todo en verde.
