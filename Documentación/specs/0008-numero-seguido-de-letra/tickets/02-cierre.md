---
titulo: Cierre
estado: ready-for-agent
spec: ../../0008-numero-seguido-de-letra.md
---

# 02: Cierre

**Qué se construye:** la documentación de la Etapa 1 describe la regla
nueva y por qué cambió.

**Bloqueado por:** 01.

**Dónde:**
- `etapa1-lexico/informe-analizador-lexico.md`:
  - "Dígito seguido inmediatamente de letra o `_`": la tabla pasa a mostrar
    los tokens partidos, y un párrafo explica que la regla anterior (un
    único lexema inválido) se revirtió por la corrección de la cátedra y el
    criterio de javac.
  - La nota de `floatLiteral`: el caso `dígitos . letra` (D2 de la spec).
  - El diagrama del autómata, si muestra la transición a
    `consumirSufijoInvalidoDeNumero`.
- `testers/guia-testers-lexico.md`, si nombra los casos movidos.
- `CLAUDE.md`, si algo de lo que dice deja de ser cierto.
- La spec y sus tickets pasan a `hecho`, con la cuenta de tests de
  `./test.sh`.

**Criterios:**
- [ ] Ningún documento dice que un número seguido de una letra es un error
      léxico.
- [ ] `./test.sh` en verde.
