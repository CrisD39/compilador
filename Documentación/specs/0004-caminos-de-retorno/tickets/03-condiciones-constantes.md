---
titulo: Condiciones constantes: sólo literales
estado: hecho
spec: ../../0004-caminos-de-retorno.md
---

# 03: Condiciones constantes: sólo literales

**Qué se construye:** los casos fijan que sólo el literal `true` o `false`,
solo o entre paréntesis, es una condición constante. Una condición con
operadores (`!false`, `1 < 2`, `'a' == 'a'`) es un bucle común, aunque en
Java sea constante: calcular valores no es tarea de esta etapa.

**Bloqueado por:** 02.

**Decisiones:** de la spec, D6 (condiciones constantes: sólo literales) y la
parte de D8 que dice que una lambda o un ternario nunca son constantes. ADR
0009. Glosario: *condición constante*.

**Dónde:**
- El código ya hace esto desde el ticket 01: sólo `NodoLiteralBooleano` y
  `NodoExpParentizada` sin encadenado tienen `valorConstante()`. Cambian
  los comentarios de `NodoExpresion.valorConstante()` y
  `NodoSentencia.esConstanteTrue`, que citaban JLS §15.29.
- Correcto 04 y Error 11 se reescriben para el nuevo alcance y vuelven de
  `pendientes/`, que desaparece.

**Criterios:**
- [x] Casos de `logros/caminosRetornoSinCodigoMuerto/` en verde: Correcto
      04 y 05; Error 11.
- [x] `!false`, `1 < 2`, `true && true`, `'a' == 'a'` y `"a" == "a"` no son
      constantes; `(true)` sí.
- [x] `./test.sh` en verde.

**Historia:** una primera versión de este ticket plegaba las expresiones
constantes como javac (JLS §15.29): aritmética `int` con desborde,
comparación de `char` y `String` por valor y decodificación de escapes. Se
descartó al revisarla porque evalúa valores (ADR 0009). Sus dos commits
quedaron en la rama `respaldo/ticket03-plegado`.

**Notas de la implementación:** no hubo cambios de comportamiento: los casos
nuevos pasan con el código del ticket 02. Correcto 04 prueba cinco
condiciones con operadores, cada una seguida de un `return` (que en Java
sería inalcanzable), y `(true)` sin `return`. Error 11 es
`int m1() { while (!false) {} }`, con falta de retorno sobre `m1`.
`./test.sh`: Sem2 con 92 casos sin error, 171 con error y 107 de logros,
todo en verde. Después del code review se agregaron casos: en Correcto 04,
`for (; ((true)); )` sin `return` y `while (!true)` y `while (1 > 2)` con el
cuerpo alcanzable; en Error 11, `while ((false)) { x = 1; }` con el cuerpo
inalcanzable. `valorConstante()` pasó a devolver `Boolean`.
