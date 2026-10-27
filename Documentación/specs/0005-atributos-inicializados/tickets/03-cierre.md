---
titulo: Cierre
estado: hecho
spec: ../../0005-atributos-inicializados.md
---

# 03: Cierre

**Qué se construye:** la documentación dice que el logro está implementado
y cómo.

**Bloqueado por:** 02.

**Dónde:**
- `etapa4-semantico2/requerimientos.md`: el "Estado de la implementación"
  incluye el logro y lo saca de los pendientes.
- `etapa4-semantico2/casos-de-prueba.md`: el índice de casos del logro.
- Spec 0003: la tabla de D11 ya no tiene la fila "atributo inicializado"
  (o una nota que diga que la reemplazó la spec 0005).
- `CLAUDE.md`: el logro figura entre los chequeados de Sem2, con su conteo
  de casos, y la lista de sintaxis que da `ERR_NO_SOPORTADO` ya no incluye
  los atributos inicializados.
- `testers/guia-testers-sem2.md`: lista de `LOGROS_HABILITADOS`.
- `Documentación/README.md`: el índice incluye la spec 0005.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [x] Todos los archivos de arriba actualizados.
- [x] Ningún documento dice que un atributo inicializado da "no
      soportado".
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación. En
`etapa4-semantico2/requerimientos.md` el logro pasó a "Chequeado", tiene su
entrada en "Estado de la implementación" y salió de los pendientes. En
`casos-de-prueba.md` se agregó "Casos del logro Atributos inicializados",
con los 21 casos y sus etiquetas. La tabla D11 de la spec 0003 perdió la
fila del atributo inicializado, con una nota que lo explica. `CLAUDE.md`,
`testers/guia-testers-sem2.md` y `Documentación/README.md` actualizados.
`./test.sh`: Sem2 con 92 casos sin error, 171 con error y 128 de logros,
todo en verde.
