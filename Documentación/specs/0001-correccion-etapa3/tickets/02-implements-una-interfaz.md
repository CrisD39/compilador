---
titulo: implements de una sola interfaz
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 02: `implements` de una sola interfaz (ADR 0005)

**Qué se construye:** una clase implementa como máximo una interfaz:
`class X implements A, B` es un error sintáctico. Una interfaz sigue
pudiendo extender varias, y cuando hereda de dos la misma clave con
retornos incompatibles, se reporta el conflicto (lo que antes se veía
porque la clase implementaba las dos directamente).

**Bloqueado por:** 01.

**Historias:** 25, 26, 27, 70.

- [x] `sintError52` (`implements Uno, Dos`) da `[Error:,|5]`.
- [x] Una clase guarda una sola interfaz implementada. El control de
      interfaz repetida en el `implements` de una clase se retira; en el
      `extends` de una interfaz se mantiene (`semError33`, `semError34`).
- [x] Una interfaz que extiende varias compara entre sí los métodos que
      hereda, con la comparación actual (misma clave, distinto retorno):
      pasan `semError36` y `semError44`.
- [x] Siguen en verde los casos reescritos: `sintCorrecto31`,
      `semCorrecto10`, `13`, `15`, `21`, `semError30`, `33`.
- [x] Ningún caso que pasaba deja de pasar.
