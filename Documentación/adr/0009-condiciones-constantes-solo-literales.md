# Sólo los literales true y false son condiciones constantes

**Estado:** aceptado (2026-10-26, al revisar el ticket 03 de la spec 0004).

El logro *Todos los caminos llevan al retorno sin código muerto* sigue a
Java: `while (true)` no termina y el cuerpo de un `while (false)` es
inalcanzable. Java va más allá y trata como constante cualquier expresión
constante (JLS §15.29): `while (1 < 2)` o `while (!false)` también son
bucles infinitos, porque javac calcula su valor al compilar. Se decide que
en MiniJava **sólo el literal `true` o `false`**, solo o entre paréntesis,
sea una condición constante. Calcular el valor de una expresión, aunque sea
de literales, es resolución de valores, y eso no es tarea de esta etapa:
el chequeo semántico mira nombres, tipos y estructura, no valores.

Reconocer el literal no es evaluar: es mirar qué token es. Por eso
`NodoLiteralBooleano` y `NodoExpParentizada` (sin encadenado) son los únicos
nodos que dan un valor constante.

## Opciones consideradas

- **Plegar las expresiones constantes como javac (JLS §15.29).** Se
  implementó en el ticket 03 y se descartó: evalúa operaciones (aritmética
  `int` con desborde, comparación de `char` y `String` por valor,
  decodificación de escapes). El código quedó en la rama
  `respaldo/ticket03-plegado`, por si la cátedra lo pide.
- **Que nada sea constante, ni `true`.** No calcula nada, pero contradice
  más al "al igual que en Java" del logro: `int m() { while (true) {} }`
  daría falta de retorno, y javac lo acepta.

## Consecuencias

- Hay programas que javac y MiniJava tratan distinto.
  `int m() { while (1 < 2) {} }` da falta de retorno en MiniJava, y javac lo
  acepta. Con `while (!false) {} return 1;`, MiniJava acepta el `return`,
  y javac lo marca como inalcanzable. El informe de la Etapa 4 tiene que
  decirlo.
- Los casos `semIIRetornoCorrecto04` y `semIIRetornoError11` fijan esta
  diferencia.
