---
titulo: Cierre
estado: ready-for-agent
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
- [ ] Todos los archivos de arriba actualizados.
- [ ] Ningún documento dice que el ternario no se soporta o que da
      `ERR_NO_SOPORTADO`.
- [ ] `./test.sh` en verde.
