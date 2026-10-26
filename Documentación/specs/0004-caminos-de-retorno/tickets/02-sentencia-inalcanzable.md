---
titulo: Sentencia inalcanzable
estado: ready-for-agent
spec: ../../0004-caminos-de-retorno.md
---

# 02: Sentencia inalcanzable

**Qué se construye:** una sentencia a la que no llega ninguna ejecución, en
un método o en un constructor, da `ERR_SENTENCIA_INALCANZABLE` sobre su
primer token y después se chequea igual. Los errores salen en orden de
programa, intercalados con los errores de tipos, y una inalcanzable no
arrastra otras en cascada (recuperación como en javac).

**Bloqueado por:** 01.

**Decisiones:** de la spec, D3 (después de una inalcanzable se sigue con
recuperando; el cuerpo de un bucle con condición `false` es inalcanzable),
D5 (reporte intercalado: se registra, no se lanza), D7 (primer token de
cada sentencia) y D8 (una local clásica inalcanzable da dos errores).

**Dónde:**
- `NodoSentencia`: el primer token de la sentencia según la tabla de D7.
  `NodoBloque` guarda la `{` y `NodoSentenciaVacia` el `;` (campos nuevos,
  los pasa el parser). En `NodoSentenciaExpresion` el token se deriva por la
  izquierda de la expresión, sin campos nuevos.
- `NodoBloque.chequear`: lleva el estado mientras recorre; si una sentencia
  llega muerta, primero registra el error, después la chequea y sigue con
  recuperando.
- `NodoWhile` y `NodoFor`: si la condición es la constante `false`, después
  de chequearla registran el error sobre el primer token del cuerpo.
- `ERR_SENTENCIA_INALCANZABLE` en la tabla de códigos con su descripción.

**Criterios:**
- [ ] Casos de `logros/caminosRetornoSinCodigoMuerto/` en verde: Correcto
      06, 08 y 09; Error 07–10, 12–17 y 21.
- [ ] `semIIRetornoError19` y `semIIRetornoError20` suman al final la
      etiqueta `var` y quedan como dice la spec.
- [ ] `conErrores/semIIError104` se borra de la base y pasa a ser
      `semIIRetornoError16`, con las etiquetas `var` y `z`.
- [ ] `logros/for/sinErrores/semIIForCorrecto04`: el segundo `for` de `m2`
      pasa a su propio método y el caso sigue sin errores.
- [ ] `./test.sh` en verde. `TesterConErroresSem2` tiene un caso menos. Las
      Etapas 1–3 no cambian, tampoco en modo pánico.

**Nota (del ticket 01):** los casos Error 07–10, 12–17 y 21 están en
`logros/caminosRetornoSinCodigoMuerto/pendientes/conErrores/`; vuelven a
`conErrores/` con este ticket. En los casos 19 y 20, la línea 3 es un
comentario que ocupa el lugar de la etiqueta `var`: se reemplaza por la
etiqueta.
