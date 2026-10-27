---
titulo: Cierre
estado: hecho
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
- [x] Todos los archivos de arriba actualizados.
- [x] Queda anotado para el informe de la Etapa 4 que el logro reemplaza
      esa parte de §12.5 y que CP-SII-38 ya no da `[SinErrores]`.
- [x] Queda anotado para el informe que, a diferencia de Java, sólo los
      literales `true` y `false` son condiciones constantes (ADR 0009), y el
      ADR 0009 figura donde se listan las decisiones de la etapa.
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación. En
`etapa4-semantico2/requerimientos.md` se agregaron "Decisiones de la spec
0004" (ADR 0008 y 0009, con la subsección "Para el informe de la Etapa 4")
y la entrada del logro en "Estado de la implementación"; la nota de casos
base que chocan con logros queda sólo para *Floats y coerción*. En
`casos-de-prueba.md`, la nota "Pendiente de confirmar" pasó a "Casos
mudados al logro de caminos de retorno". También se actualizó
`testers/guia-testers-sem2.md` (lista de `LOGROS_HABILITADOS`). La
documentación deja claro que no se evalúa ninguna expresión para decidir
si un código es alcanzable (ADR 0009). `./test.sh`: Sem2 con 92 casos sin
error, 171 con error y 107 de logros, todo en verde.
