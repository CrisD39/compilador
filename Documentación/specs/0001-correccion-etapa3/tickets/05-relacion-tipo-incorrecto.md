---
titulo: Relación con el tipo equivocado
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 05: Relación con el tipo equivocado (D1)

**Qué se construye:** el compilador ya no se cae cuando una clase extiende
una interfaz. Una clase que extiende una interfaz, una clase que implementa
una clase y una interfaz que extiende una clase dan
`ERR_HERENCIA_TIPO_INCORRECTO` sobre el tipo nombrado. La relación se
descarta, la clase queda con `Object` como padre y se siguen reportando los
demás errores.

**Bloqueado por:** 03, 04.

**Historias:** 1, 2, 3, 16, 17, 18, 19.

- [x] Pasan `semError65` (hoy `ClassCastException`), `semError66` y
      `semError67`.
- [x] Pasa `semError72`: un error anterior al de la relación se sigue
      imprimiendo.
- [x] La descripción dice qué se esperaba ("Ii no es una clase",
      "Aa no es una interfaz").
- [x] Ningún caso que pasaba deja de pasar.
