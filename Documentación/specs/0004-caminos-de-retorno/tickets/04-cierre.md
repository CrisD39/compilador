---
titulo: Cierre
estado: ready-for-agent
spec: ../../0004-caminos-de-retorno.md
---

# 04: Cierre

**Qué se construye:** la documentación dice que el logro está implementado
y cómo.

**Bloqueado por:** 03.

**Dónde:**
- `etapa4-semantico2/requerimientos.md`: el "Estado de la implementación"
  incluye el logro.
- `etapa4-semantico2/casos-de-prueba.md`: los dos códigos nuevos en la tabla
  de convenciones, el índice sin los tres casos mudados y sin la nota
  "Pendiente de confirmar" sobre §12.5.
- `adr/0008-caminos-de-retorno-reemplaza-base.md`: pasa a implementado.
- `CLAUDE.md`: el logro figura entre los implementados de Sem2.
- `Documentación/README.md`: la cuenta de casos base de
  `etapa4-semantico2/`.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [ ] Todos los archivos de arriba actualizados.
- [ ] Queda anotado para el informe de la Etapa 4 que el logro reemplaza
      esa parte de §12.5 y que CP-SII-38 ya no da `[SinErrores]`.
- [ ] `./test.sh` en verde.
