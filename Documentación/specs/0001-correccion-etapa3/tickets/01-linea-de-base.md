---
titulo: Línea de base
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 01: Línea de base

**Qué se construye:** una referencia de los testers de Etapas 1–3 antes del
primer cambio, para detectar regresiones en cada ticket.

**Bloqueado por:** nada (se puede empezar ya).

**Historias:** 67.

- [x] Compilar y correr los testers léxico, sintáctico, modo pánico y
      semántico de declaraciones.
- [x] Resultado (2026-10-20, antes de agregar los casos de la spec): 217
      tests en verde (léxico 14 + 24, sintáctico 37 + 63, pánico 4,
      semántico 22 + 53).
- [x] Los casos de la spec ya están escritos (`semCorrecto23`–`33`,
      `semError54`–`85`, reescritura del ADR 0005). Con ellos, los que
      fallan son exactamente los que cubren los tickets 02–11.
