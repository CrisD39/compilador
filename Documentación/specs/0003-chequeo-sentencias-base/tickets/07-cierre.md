---
titulo: Cierre
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 07: Cierre

**Qué se construye:** Sem2 pasa a ser parte de la corrida normal, y la
documentación refleja lo implementado.

**Bloqueado por:** 06.

**Dónde:**
- `test.sh`: `TesterSinErroresSem2`, `TesterConErroresSem2` y
  `TesterLogrosSem2` entran en la corrida por defecto, y con eso en el hook
  pre-commit. Se actualiza el comentario de uso.
- `CLAUDE.md`: el main del jar es `ModuloPrincipalET4`. La sección "Sem2"
  deja de decir que no está cableado, describe la tercera pasada
  (`chequearSentencias()`, sin cascada) y nombra `TesterLogrosSem2` con su
  lista de logros habilitados. Los comandos de `test.sh` quedan al día.
- `Documentación/README.md`: el índice incluye la spec 0003.
- `etapa4-semantico2/requerimientos.md`: qué quedó implementado y qué
  logros siguen pendientes.
- `etapa4-semantico2/casos-de-prueba.md`: la tabla de lexemas deja de ser
  "propuesta", y se agregan los `ERR_*` que finalmente se usaron.
- La spec 0003 y sus tickets pasan a `estado: hecho`.

**Criterios:**
- [ ] `./test.sh` completo en verde: Etapas 1–3, los 138 casos de Sem2 y
      `TesterLogrosSem2`. Se anota la cantidad de tests en la spec, como en la 0002.
- [ ] Un programa con dos errores en sentencias distintas de métodos
      distintos reporta los dos (verificado a mano).
- [ ] `java -jar out/compilador.jar` sobre un caso de `semantico/` (Etapa 3)
      con errores de declaraciones da los mismos errores que ET3, sin errores
      de sentencias agregados.
