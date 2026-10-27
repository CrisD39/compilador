---
titulo: Documentación de la revisión
estado: ready-for-agent
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
- [ ] Todo lo de arriba corregido.
- [ ] `revision.md` dice qué ticket resolvió cada punto.
- [ ] La spec y los tickets 05–09 en `hecho`, con la cuenta de tests de
      `./test.sh`.
- [ ] `./test.sh` en verde.
