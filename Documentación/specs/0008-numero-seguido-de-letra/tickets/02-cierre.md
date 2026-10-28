---
titulo: Cierre
estado: hecho
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
- [x] Ningún documento dice que un número seguido de una letra es un error
      léxico.
- [x] `./test.sh` en verde.

**Notas de la implementación:** sólo documentación, en
`etapa1-lexico/informe-analizador-lexico.md`: la aclaración de "Tokens" ya
no pone la letra pegada a un número como ejemplo de error (los literales
numéricos son la excepción), la nota de `floatLiteral` describe el caso
`dígitos . letra` (D2), el diagrama tiene la transición de
`estadoParteDecimal` que emite `LIT_INT` y deja `PUNTO` en reserva, con un
párrafo sobre `tokenEnReserva`, y "Dígito seguido inmediatamente de letra o
`_`" muestra los tokens partidos (verificados con el jar) y un párrafo "Por
qué cambió" con la regla anterior, la corrección y el criterio de javac. El
diagrama no tenía transición a `consumirSufijoInvalidoDeNumero`.
`testers/guia-testers-lexico.md` no nombra los casos movidos y `CLAUDE.md`
no dice nada que deje de ser cierto: no cambian. `./test.sh`: léxico
25 + 19, sintáctico 38 + 66 + 4, semántico 37 + 104, Sem2 93 + 172 + 207,
todo en verde.
