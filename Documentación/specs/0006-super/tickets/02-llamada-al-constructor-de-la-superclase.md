---
titulo: Llamada al constructor de la superclase
estado: ready-for-agent
spec: ../../0006-super.md
---

# 02: Llamada al constructor de la superclase

**Qué se construye:** se puede escribir `super(...);` como primera
sentencia del cuerpo de un constructor. Elige por aridad un constructor de
la superclase, igual que `new`: vale `super()` si la superclase no declaró
constructores o si es `Object`, y si no hay uno de esa aridad da
`ERR_CONSTRUCTOR_NO_ENCONTRADO`. Un constructor private da
`ERR_ACCESO_PRIVADO`, y un argumento que no conforma con el parámetro da
`ERR_ARGUMENTO_INCOMPATIBLE`. Los parámetros se ven desde el tipo de la
superclase, así que en `B extends A<String>` el constructor `A(T t)` se ve
como `A(String t)`. Los tres errores van sobre `super`. En cualquier otro
lugar (un método, estático o no, después de otra sentencia, dentro de un
bloque anidado o de un `if`, o como segunda llamada), `super(...)` da
`ERR_SUPER_FUERA_DE_LUGAR` sobre `super`, antes de mirar los argumentos.
`super(1).m();` es un error sintáctico sobre `.`. Un constructor que no
escribe `super(...)` sigue sin llamar a ningún constructor de la superclase
(§6.3). En este ticket los argumentos se chequean como cualquier expresión,
y la restricción del contexto previo a la construcción queda para el 03.

**Bloqueado por:** 01.

**Decisiones:** de la spec, D3 (rama `(` de la sentencia), D4
(`NodoLlamadaSuperConstructor`), D7 (ubicación por el AST), D8
(resolución, con el texto ajustado de los tres códigos que ya existen) y
D10 (inalcanzable y visibilidad). Glosario: *llamada al constructor de la
superclase*. JLS §8.8.7.

**Dónde:**
- Prefactor, primero y en verde: separar de `NodoCreacionObjeto` la
  resolución del constructor (aridad, visibilidad, conformidad de los
  argumentos con la instanciación), con el token que recibe los errores
  como parámetro, para que la reusen los dos nodos.
- Parser: en la intercepción de `super` de `<Sentencia>`, si sigue `(`,
  se arma `super <ArgsActuales> ;`.
- `NodoLlamadaSuperConstructor` (un `NodoSentencia`, con el token `super`
  y los argumentos): sale `VIVO` en el flujo, como una sentencia expresión,
  y su primer token es `super`. Si no está en lugar, lanza
  `ERR_SUPER_FUERA_DE_LUGAR`; si está, resuelve el constructor en la clase
  de la superclase.
- `Constructor`: marca como en lugar la primera sentencia de su cuerpo si
  es una `NodoLlamadaSuperConstructor`. Que sea primera es literal: `;`,
  `{ super(); }` o un `if` al principio no cuentan.
- `ERR_SUPER_FUERA_DE_LUGAR` en la tabla de códigos. Las descripciones de
  `ERR_CONSTRUCTOR_NO_ENCONTRADO`, `ERR_ARGUMENTO_INCOMPATIBLE` y
  `ERR_ACCESO_PRIVADO` se leen bien cuando el lexema es `super` (por
  ejemplo, nombran la superclase).
- Casos nuevos en `logros/superKeyword/` (tabla de la spec).

**Criterios:**
- [ ] Casos de `logros/superKeyword/` en verde: Correcto 03 y 04;
      Error 09–16.
- [ ] Siguen en verde los casos del ticket 01, `semIICorrecto22` y
      `semIIVisibilidadCorrecto04`.
- [ ] `new` da las mismas etiquetas que antes del prefactor.
- [ ] Siguen en verde los demás logros habilitados.
- [ ] `./test.sh` en verde. Las Etapas 1–3 no cambian.
