---
titulo: Ternario sin tipo esperado
estado: hecho
spec: ../../0007-ternarios.md
---

# 01: Ternario sin tipo esperado

**Qué se construye:** el ternario deja de dar "no soportado". En todos los
contextos se chequea de abajo hacia arriba: primero la condición, que
tiene que ser `boolean` (si no, `ERR_CONDICION_NO_BOOLEAN` sobre el `?`);
después la rama izquierda y la derecha, ninguna void, aunque lo sean las
dos (si no, `ERR_TERNARIO_RAMA_VOID` sobre el `?`, con un filtro explícito
antes de comparar). Por último, el ternario tiene el tipo de la rama con
la que la otra conforma, y si ninguna conforma con la otra, da
`ERR_TERNARIO_INCOMPATIBLE` sobre el `?`. No se busca un supertipo común.
El null sale de `conformaCon` sin código especial: `null : null` tiene el
tipo null, `null` con una referencia da esa referencia y `null` con un
primitivo es error. El ternario sigue sin ser asignable ni llamable, y
nunca es una condición constante. En este ticket todavía no hay tipo
esperado: `a = c ? new B2() : new C2();` con `a` de tipo `A1` sigue siendo
error.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (alcance), D4 (pasos 1 y 3), D5 (lo que no
cambia), D6 (interacción con otros logros) y D7 (contrato de salida). ADR
0011 (la parte sin tipo esperado). Glosario: *ternario*.

**Dónde:**
- `NodoExpTernaria.chequear`: los pasos 1 y 3 de D4, con todos los
  errores sobre el token `?`.
- `ERR_TERNARIO_RAMA_VOID` y `ERR_TERNARIO_INCOMPATIBLE` en la tabla de
  códigos, con las descripciones de la spec. La condición reusa
  `ERR_CONDICION_NO_BOOLEAN`, cuya descripción ya toma el lexema del token
  ("la condición de ? tiene que ser boolean").
- `TesterLogrosSem2`: agregar `ternarios` a `LOGROS_HABILITADOS`.
- `logros/atributosInicializados/conErrores/semIIAtribInicError15`: se
  reescribe como `int a = true ? 1 : "x";`, con la misma etiqueta
  `[Error:?|7]` y el comentario actualizado. Es obligatorio en este
  ticket: el caso actual (`true ? 1 : 2`) dejaría de dar error.
- Casos nuevos en `logros/ternarios/` (tabla de la spec).

**Criterios:**
- [x] Casos de `logros/ternarios/` en verde: Correcto 01 (existe), 04, 05
      y 07; Error 01 (existe), 02–08, 10–12, 17–20 y 23.
- [x] `semIIAtribInicError15` reescrito, en verde con `[Error:?|7]` y sin
      un segundo error del atributo.
- [x] `class A1 { int x; void m(boolean c) { x = c ? 1 : 2; } }` da
      `[SinErrores]` con el jar.
- [x] Ningún caso base de `semantico2/{sinErrores,conErrores}` ni de las
      Etapas 1–3 cambia de etiqueta.
- [x] Siguen en verde los demás logros habilitados.
- [x] `./test.sh` en verde.

**Nota de implementación:** el caso Error 19 usa `c ? a : b;` (ramas
`int`) como sentencia suelta, y no `c ? m() : n();` como decía la tabla de
la spec. `NodoSentenciaExpresion` tipa la expresión antes de preguntar si es
una sentencia válida, así que con dos ramas void el error es
`ERR_TERNARIO_RAMA_VOID` sobre el `?` (lo que ya prueba Error 04) y no el
`;`. Cambiar ese orden movería etiquetas de casos base. La tabla de la spec
se corrigió.
