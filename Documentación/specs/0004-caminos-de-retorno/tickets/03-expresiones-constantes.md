---
titulo: Expresiones constantes completas
estado: ready-for-agent
spec: ../../0004-caminos-de-retorno.md
---

# 03: Expresiones constantes completas

**Qué se construye:** la condición de un `while` o de un `for` cuenta como
constante cuando es cualquier expresión constante, no sólo un literal:
`while (!false) {}` y `while (1 < 2) {}` no terminan normalmente, y lo que
venga después es inalcanzable.

**Bloqueado por:** 02 (el Error 11 necesita el reporte de inalcanzables).

**Decisiones:** de la spec, D6 (reglas de las expresiones constantes) y la
parte de D8 que dice que una lambda o un ternario nunca son constantes.
Glosario: *expresión constante*.

**Dónde:**
- Los nodos de expresión dan su valor constante (por defecto, ninguno). Lo
  tienen los literales `int`, `boolean`, `char` y `String`, los unarios
  (`+ - !`), los binarios (`|| && == != < > <= >= + - * / %`) y la
  parentizada. Se pliega sobre el AST, con la asociatividad a izquierda y
  sin precedencia de MiniJava.
- El análisis de flujo usa ese valor en lugar del reconocimiento de
  literales del ticket 01.

**Criterios:**
- [ ] Casos de `logros/caminosRetornoSinCodigoMuerto/` en verde: Correcto
      04 y 05; Error 11.
- [ ] La aritmética es `int` de 32 bits con desborde; `==` y `!=` comparan
      el valor entre `char` y el contenido entre `String`.
- [ ] `null`, una división o un módulo por cero y una expresión mal tipada
      no son constantes, y el plegado no reporta ningún error.
- [ ] `./test.sh` en verde.
