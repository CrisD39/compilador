---
titulo: Tipos predefinidos y Object implícito
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 04: Tipos predefinidos y `Object` implícito (ADR 0004)

**Qué se construye:** `Object`, `String` y `System` existen antes de
analizar el programa, con los métodos del §7. Se pueden usar como tipos sin
declararlos. Toda clase sin `extends` tiene a `Object` como padre efectivo
y hereda `toString` y `debugPrint`.

**Bloqueado por:** 01.

**Historias:** 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15.

- [ ] Pasan `semCorrecto23` (predefinidas como tipos) y `semCorrecto29`
      (`extends Object` explícito).
- [ ] `class Object {}` da clase duplicada sobre la declaración del
      programa, y la predefinida se conserva (`semError54`).
- [ ] `String` y `System` son finales: pasan `semError55` y `semError73`.
- [ ] `Object` es el padre efectivo de toda clase sin `extends`, incluidas
      las que implementan una interfaz. Lo que el programa escribió no
      cambia: `sealed`/`permits` siguen distinguiendo `class A` de
      `class A extends Object`.
- [ ] Las predefinidas no pasan por el control de declaraciones, pero sí se
      consolidan, y van primero.
- [ ] Ningún caso que pasaba deja de pasar.
