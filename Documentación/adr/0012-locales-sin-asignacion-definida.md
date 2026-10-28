# Las locales clásicas no exigen asignación definida

**Estado:** aceptado e implementado (2026-10-28, en la entrevista de diseño
del logro *Variables Locales Clásicas Controladas*; se implementó en la spec
0009, `specs/0009-variables-locales-clasicas.md`).

Java rechaza leer una variable local que no tiene un valor asignado en todos
los caminos que llegan a la lectura (`int x; return x;` da "variable x
might not have been initialized", JLS cap. 16). Se decide que en MiniJava
**una variable local clásica declarada sin inicializador queda declarada
con su tipo y se puede usar libremente**: no se controla la asignación
definida. Por lo mismo, `int x = x;` no es "no inicializada" como en Java:
el inicializador no ve el nombre que se declara (como `var x = x`), así que
`x` es la de un atributo, si lo hay, o una variable no declarada.

El logro pide que las declaraciones clásicas estén "correctamente tipadas".
La asignación definida no es un control de tipos sino un análisis de flujo
sobre las asignaciones, con reglas propias para cada sentencia y operador
(`&&`, `||`, `!`, el ternario, los bucles, `return`).

## Opciones consideradas

- **Asignación definida como javac (JLS cap. 16).** Es lo de Java, pero es
  un análisis aparte, del tamaño de un logro, que el enunciado no pide. Se
  podría montar sobre el flujo estructural de la spec 0004
  (`NodoSentencia.salida`), pero esa infraestructura no sigue qué variables
  se asignaron.
- **Exigir inicializador en toda local clásica.** Es simple, pero rechaza
  `int x;`, que es el ejemplo del propio logro.

## Consecuencias

- Hay programas que javac rechaza y MiniJava acepta: `int x; return x;`,
  `int x; if (c) { x = 1; } return x;`. El informe de la Etapa 4 tiene que
  decirlo.
- Si la cátedra lo pide, el control se agrega después sin cambiar la
  sintaxis ni el AST: sólo convierte en error programas que hoy son
  válidos.
- El logro no dice "al igual que en Java", pero Java es la referencia del
  proyecto. Conviene confirmarlo con la cátedra.
