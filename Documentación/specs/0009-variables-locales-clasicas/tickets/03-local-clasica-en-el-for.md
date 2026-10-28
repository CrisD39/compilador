---
titulo: Local clásica en la inicialización del for
estado: ready-for-agent
spec: ../../0009-variables-locales-clasicas.md
---

# 03: Local clásica en la inicialización del `for`

**Qué se construye:** `for (int i = 0; i < 10; i = i + 1)` y
`for (A1 a = null; a == null; a = new A1())` compilan, como en Java. El
`for` acepta como inicialización una local clásica, además de un `var` y
una asignación. La local queda en el scope propio del `for`, como el `var`,
y se chequea con las reglas del ticket 02. Una llamada sigue dando
`ERR_FOR_INICIALIZACION`.

**Bloqueado por:** 02.

**Decisiones:** de la spec, D8 (for).

**Dónde:**
- `NodoFor.chequear`: la condición sobre la inicialización.
- `logros/for/conErrores/semIIForError04` pasa a
  `logros/for/sinErrores/semIIForCorrecto10`, con el comentario reescrito.
- `logros/variablesLocalesClasicas/sinErrores/semIIVarLocalCorrecto10`
  (tabla de la spec).

**Criterios:**
- [ ] `semIIForCorrecto10` y `semIIVarLocalCorrecto10` en verde.
- [ ] El logro *For* queda con 10 casos correctos y 26 con error, todos en
      verde.
- [ ] Siguen en verde los demás logros habilitados y los casos base.
- [ ] `./test.sh` en verde.
