---
titulo: Documentación de la revisión
estado: hecho
spec: ../../0006-super.md
---

# 09: Documentación de la revisión

**Qué se construye:** los documentos y comentarios que la revisión
encontró desactualizados dicen lo que hace el código después de 05–08.

**Bloqueado por:** 08 (un solo repaso de los comentarios).

**Origen:** `revision.md`, §3.1 y §3.2.

**Dónde:**
- `Documentación/README.md`: la coma que falta en la fila `adr/`, antes de
  "la 0009".
- El encabezado de `ChequeoVisibilidad`: además de los seis accesos a un
  miembro, `super(...)` llega a la exigencia de acceso por la resolución
  del constructor (`ERR_ACCESO_PRIVADO` sobre `super`, como en
  `semIISuperError16`).
- `CLAUDE.md`: lo mismo donde dice "the six member accesses".
- Los comentarios de `ErrorSemantico` sobre `ERR_ACCESO_PRIVADO`, después
  de 08.

**Criterios:**
- [x] Todo lo de arriba corregido.
- [x] `revision.md` dice qué ticket resolvió cada punto.
- [x] La spec y los tickets 05–09 en `hecho`, con la cuenta de tests de
      `./test.sh`.
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación y comentarios. El
encabezado de `ChequeoVisibilidad` y el comentario de `exigirAcceso` dicen
que `super(...)` llega por `ChequeoLlamada.chequearConstructor()`, con la
superclase como receptor y el error sobre `super`; `CLAUDE.md` dice lo mismo.
Los comentarios de `ErrorSemantico` sobre `ERR_ACCESO_PRIVADO` (el de
`detalle` y el del `case`) ya estaban bien después de 08 y no se tocaron.
`revision.md` tiene una sección "Resolución" con el ticket de cada punto, y
la spec lista los tickets 05–09 y un cierre de la revisión. `./test.sh`:
léxico 14 + 24, sintáctico 38 + 63 + 4 de modo pánico, semántico 37 + 104,
Sem2 con 93 casos sin error, 172 con error y 156 de logros, todo en verde.
