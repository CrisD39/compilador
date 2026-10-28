---
titulo: Cierre
estado: hecho
spec: ../../0007-ternarios.md
---

# 04: Cierre

**Qué se construye:** la documentación dice que el logro está implementado
y cómo.

**Bloqueado por:** 03.

**Dónde:**
- `etapa4-semantico2/requerimientos.md`: una sección "Decisiones de la
  spec 0007" (con la diferencia con Java de las hermanas sin tipo
  esperado, para el informe); el "Estado de la implementación" incluye el
  logro y lo saca de los pendientes; *Ternarios* sale de la lista de
  logros con sintaxis pero sin chequeo y de la nota de *Atributos
  inicializados* que lo nombra entre la sintaxis de otros logros.
- `etapa4-semantico2/casos-de-prueba.md`: los tres códigos nuevos y
  `ERR_CONDICION_NO_BOOLEAN` sobre `?` en la tabla de convenciones, y el
  índice de casos del logro.
- El comentario de `NodoExpTernaria`: deja de decir que no se soporta.
- `adr/0011`: el estado pasa a implementado.
- `CLAUDE.md`: el logro figura entre los chequeados de Sem2, con su conteo
  de casos, y sale de la línea de logros cuyos nodos reportan
  `ERR_NO_SOPORTADO`.
- `testers/guia-testers-sem2.md`: lista de `LOGROS_HABILITADOS`.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [x] Todos los archivos de arriba actualizados.
- [x] Ningún documento dice que el ternario no se soporta o que da
      `ERR_NO_SOPORTADO`.
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación. En
`etapa4-semantico2/requerimientos.md` el logro pasó a "Chequeado", hay una
sección "Decisiones de la spec 0007" (con cuatro notas para el informe: las
hermanas sin tipo esperado, el `?` como lexema, la falta de boxing y de
promoción numérica, y la condición sin evaluar), su entrada en "Estado de
la implementación" y salió de los pendientes; la nota de la spec 0005 sobre
la sintaxis de otros logros en un inicializador ya no nombra al ternario, y
el boceto de `Atributo.chequearInicializador` usa `chequearConTipoEsperado`.
En `casos-de-prueba.md`, `?` en la fila de la condición no boolean, los tres
códigos nuevos, `semIIAtribInicError15` reescrito y "Casos del logro
Ternarios" con los 30 casos y sus etiquetas. El comentario de
`NodoExpTernaria` ya lo había actualizado el ticket 01. ADR 0011
implementado; `CLAUDE.md` y `testers/guia-testers-sem2.md` actualizados.
`./test.sh`: Sem2 con 93 casos sin error, 172 con error y 186 de logros,
todo en verde.
