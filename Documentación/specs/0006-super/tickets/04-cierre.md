---
titulo: Cierre
estado: ready-for-agent
spec: ../../0006-super.md
---

# 04: Cierre

**Qué se construye:** la documentación dice que el logro está implementado
y cómo.

**Bloqueado por:** 03.

**Dónde:**
- `etapa4-semantico2/requerimientos.md`: una sección "Decisiones de la
  spec 0006"; el "Estado de la implementación" incluye el logro y lo saca
  de los pendientes (y saca la línea "*Super!*: la gramática todavía no lo
  tiene"); la nota de visibilidad sobre constructores private se reescribe
  (D10); la fila de *Super!* sale de "Pendientes de gramática"; el árbol de
  herencia de nodos ya no marca `NodoSuper` ni
  `NodoLlamadaSuperConstructor` como `[pendiente]`; "Mapeo no terminal →
  nodo" incluye las producciones nuevas.
- `etapa2-sintactico/analizador-sintactico.md`: las producciones de D3 y
  los FIRST que cambian.
- `etapa4-semantico2/casos-de-prueba.md`: los códigos y lexemas nuevos en
  la tabla de convenciones, `super` en los primeros tokens de la fila de
  sentencia inalcanzable, y el índice de casos del logro.
- `CLAUDE.md`: el logro figura entre los chequeados de Sem2, con su conteo
  de casos.
- `testers/guia-testers-sem2.md`: lista de `LOGROS_HABILITADOS`.
- `Documentación/README.md`: el índice incluye la spec 0006 y el ADR
  nuevo.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [ ] Todos los archivos de arriba actualizados.
- [ ] Ningún documento dice que `super` está pendiente o que la gramática
      no lo tiene.
- [ ] `./test.sh` en verde.
