# El ternario se tipa por destino donde hay tipo esperado, y si no, por conformidad entre ramas

**Estado:** aceptado, sin implementar (2026-10-28, entrevista de diseño del
logro *Ternarios*; se planifica en la spec 0007).

El logro pide que los ternarios estén "correctamente tipados". En Java
(JLS §15.25.3), un ternario de referencias en un contexto de asignación o de
invocación es una *poly expression*: no tiene tipo propio, y cada rama tiene
que conformar con el tipo que exige el contexto. Fuera de esos contextos,
su tipo es el supertipo común (lub) de las ramas. Hasta ahora, el chequeo
de expresiones de MiniJava iba sólo de abajo hacia arriba: cada nodo
devuelve su tipo sin saber dónde se usa, y el contexto compara después.

Se decide un esquema **híbrido**:

- **Con tipo esperado** (`=`, inicializador de atributo, `return` en un
  método no void, argumentos de llamadas, de `new` y de `super(...)`), el
  tipo esperado baja al ternario, a través de paréntesis y de ternarios
  anidados. Cada rama tiene que conformar con él. Así,
  `A1 a; a = c ? new B2() : new C2();` es válido, como en Java.
- **Sin tipo esperado** (receptor de una cadena, operandos, `==`,
  `instanceof`, condiciones, índices, for-each, `var`), el ternario tiene
  el tipo de la rama con la que la otra conforma, y si ninguna conforma con
  la otra, es error. No se busca un supertipo común:
  `(c ? new B2() : new C2()).m()` es error, y javac lo acepta.

El tipo esperado baja con un método aparte del chequeo de expresiones, con
una implementación por defecto que tipa de abajo hacia arriba como siempre.
Sólo el ternario y el paréntesis lo redefinen. El ternario, una vez que
validó sus ramas, devuelve el tipo esperado, así el contexto sigue haciendo
su control de conformidad de siempre y nunca reporta un segundo error.

## Opciones consideradas

- **Sólo de abajo hacia arriba, por conformidad entre ramas.** Toca un
  solo nodo, pero rechaza `A1 a = c ? new B2() : new C2();`, que es Java
  común y que un caso de la cátedra podría usar.
- **Supertipo común (lub) en todos lados.** Con interfaces, Java arma un
  tipo intersección (`A1 & I1`), que MiniJava no tiene. Habría que
  inventar un recorte, y aun así no cubre la tipificación por destino.
- **Tipado por destino en todos lados.** No hay tipo esperado en el
  receptor de una cadena, en un `==` o en un `var`, así que igual hace
  falta una regla para esos casos.

## Consecuencias

- Todos los errores del ternario van sobre su `?`: la condición no boolean
  (`ERR_CONDICION_NO_BOOLEAN`), una rama void (`ERR_TERNARIO_RAMA_VOID`),
  una rama que no conforma con el tipo esperado
  (`ERR_TERNARIO_RAMA_INCOMPATIBLE`) y las ramas incompatibles entre sí sin
  tipo esperado (`ERR_TERNARIO_INCOMPATIBLE`). javac marca la rama, no el
  `?`.
- En una llamada, los argumentos se siguen chequeando en dos fases: primero
  se tipan todos, y un ternario recibe ahí su formal, así que sus errores de
  rama salen en orden del fuente. Después va `ERR_ARGUMENTO_INCOMPATIBLE`
  para el resto. `m(1, c ? x : "a")` con formales `(String, int)` reporta el
  `?` y no el `1`. Los casos sin ternario no cambian.
- Las hermanas sin tipo esperado son una diferencia con Java que el informe
  de la Etapa 4 tiene que decir, y conviene confirmarla con la cátedra.
- Las locales clásicas (`A1 a = ...;`) y los inicializadores de arreglo son
  contextos con tipo esperado en Java. Cuando se implementen sus logros,
  tienen que pasar el tipo esperado como los demás contextos.
- Un parámetro de tipo `T` sigue sin conformar con `Object`, así que
  `Object o; o = c ? t : new Object();` es error (regla de
  `semIIError41-43`).
- Sin boxing, `null` con un primitivo es error aun con tipo esperado:
  `int x; x = c ? 1 : null;`.
