---
titulo: Cierre
estado: hecho
spec: ../../0009-variables-locales-clasicas.md
---

# 04: Cierre

**Qué se construye:** la documentación dice que el logro está implementado
y cómo.

**Bloqueado por:** 03.

**Dónde:**
- `etapa2-sintactico/analizador-sintactico.md`: la nota de alcance del
  `for` que dice que `<RestoDeclLocal>` no admite arreglos. El resto de la
  Etapa 2 lo actualizó el ticket 01; se revisa que no quede nada del "valor
  común".
- `etapa4-semantico2/requerimientos.md`:
  - ⚠7: un inicializador por declarador y la lista de declaradores.
  - La fila de `NodoVarLocalClasica` en la tabla de nodos.
  - La nota del logro *For* ("al pie de la letra"): ahora `int i = 0` vale.
  - La nota de la spec 0007 sobre las locales clásicas como contexto con
    tipo esperado.
  - Una sección "Decisiones de la spec 0009", con la diferencia con Java
    (asignación definida, `int x = x;`) para el informe.
  - El "Estado de la implementación" incluye el logro, y el logro sale de
    los pendientes y de la lista de logros con sintaxis pero sin chequeo.
- `etapa4-semantico2/casos-de-prueba.md`: las filas de la local clásica en
  la tabla de convenciones, el índice de casos del logro y el cambio de
  `semIIForError04` a `semIIForCorrecto10`.
- Los comentarios de `NodoVarLocalClasica` y `NodoFor`, si todavía dicen
  algo viejo.
- `adr/0012`: el estado pasa a implementado.
- `CLAUDE.md`: el logro entre los chequeados de Sem2, con su conteo de
  casos, el conteo nuevo de *For* (10 + 26), y fuera de la línea de logros
  cuyos nodos reportan `ERR_NO_SOPORTADO`.
- `testers/guia-testers-sem2.md`: lista de `LOGROS_HABILITADOS`.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [x] Todos los archivos de arriba actualizados.
- [x] Ningún documento dice que la local clásica no se soporta, que da
      `ERR_NO_SOPORTADO` o que el inicializador es "un valor común" para
      la lista.
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación. `analizador-sintactico.md`
ya estaba al día (lo actualizó el ticket 01, nota de alcance del `for`
incluida). En `etapa4-semantico2/requerimientos.md` el logro pasó a
"Chequeado", hay una sección "Decisiones de la spec 0009" (con cuatro notas
para el informe: la asignación definida, `int x = x;`, un error por
sentencia y la falta de coerción), su entrada en "Estado de la
implementación", y salió de los pendientes. También se actualizaron ⚠7, la
fila de `NodoVarLocalClasica` en la tabla de nodos, la lista de no
terminales sin nodo (`<MasDeclaradores>`), la nota del logro *For* (con sus
casos) y la de la spec 0007. En `casos-de-prueba.md`, las filas de la
tabla de convenciones (no declarada, asignación incompatible, rama de
ternario, local duplicada, tipo inválido, declaración como cuerpo e
inicialización del `for`), la recuperación de una local clásica con error,
la nota de los casos de *Ternarios* y "Casos del logro Variables locales
clásicas" con los 26 casos y el paso de `semIIForError04` a
`semIIForCorrecto10`. Los comentarios de `NodoVarLocalClasica` y `NodoFor`
ya los habían dejado al día los tickets 02 y 03. ADR 0012 implementado;
`CLAUDE.md` y `testers/guia-testers-sem2.md` actualizados.
`etapa2-sintactico/logros.md` (la gramática entregada en la Etapa 2) sigue
con `<MasIdsLocal>`: es una foto de esa entrega y no se tocó.
`./test.sh`: Sem2 con 93 casos sin error, 172 con error y 233 de logros,
todo en verde.
