# `super(...)` es una sentencia y va primera, como en JDK 21

**Estado:** aceptado (grill del 2026-10-27; se implementa en la spec 0006,
`specs/0006-super.md`).

El logro *Super!* pide "utilizar super como en Java [...] para invocar a su
constructor". Qué es "como en Java" depende de la versión: hasta JDK 24 la
llamada explícita al constructor de la superclase sólo puede ser la primera
sentencia del cuerpo de un constructor (JLS §8.8.7), y JDK 25 (JEP 513,
*flexible constructor bodies*) admite sentencias antes de ella, siempre que
no usen el objeto actual. Se decide seguir a **JDK 21**: `super(...)` es una
**sentencia**, `super <ArgsActuales> ;`, y sólo vale como la primera
sentencia del cuerpo de un constructor. En cualquier otro lugar es
`ERR_SUPER_FUERA_DE_LUGAR`, sobre `super`. Como no es una expresión, no
produce un valor: `x = super(1);` y `super(1).m();` son errores sintácticos.

El compilador se escribe y se corre con JDK 21, y la cátedra describe el
lenguaje con esa versión.

## Opciones consideradas

- **Cuerpos flexibles de JDK 25 (JEP 513).** Habría que admitir sentencias
  antes de `super(...)` y chequearlas todas en el contexto previo a la
  construcción, no sólo los argumentos. Eso complica la gramática y el
  análisis (un estado más que atraviesa sentencias y bloques), y el logro no
  lo pide.
- **`super(...)` como expresión** (un operando más, y la ubicación como
  chequeo semántico). Acepta en la gramática formas que no tienen sentido
  (`x = super(1)`), que después habría que rechazar una por una.

## Consecuencias

- La ubicación la decide el AST: el constructor marca la sentencia que está
  literalmente primera en su cuerpo si es la llamada; `{ ; super(); }`,
  `{ { super(); } }` y `{ if (c) super(); }` no tienen ninguna en lugar.
- Un programa válido en JDK 25 con sentencias antes de `super(...)` es un
  error en MiniJava. El informe de la Etapa 4 tiene que decirlo.
- Se mantiene §6.3 de la Parte I: sin `super(...)` escrito no hay llamada
  implícita, ni se exige a la superclase un constructor sin parámetros.
