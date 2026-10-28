---
titulo: Un inicializador por declarador y locales de arreglo
estado: ready-for-agent
spec: ../../0009-variables-locales-clasicas.md
---

# 01: Un inicializador por declarador y locales de arreglo

**Qué se construye:** en `ModuloPrincipalET2`, `ET3` y `ET4` se parsea una
local clásica como en Java. Cada nombre tiene su propio inicializador
opcional (`int x = 1, y = 2;`, `int x, y = 2, z;`), y el tipo puede ser un
arreglo de una o más dimensiones (`int[] a;`, `A1[][] b;`, `T[] t;`,
`Caja<A1>[] c;`). El `for` no cambia: un solo nombre, sin dimensiones, y
`for (int i = 0, j = 0; …)` sigue siendo error sintáctico sobre la `,`.
`<InitLocalOpc>` devuelve también el token `=`, en las locales y en el
`for`. `NodoVarLocalClasica` sigue siendo un nodo por sentencia. Guarda el
tipo, ya con sus dimensiones, y una lista ordenada de declaradores, cada
uno con su nombre, su `=` (null sin inicializador) y su expresión (null sin
inicializador). El `for` arma el mismo nodo con un solo declarador. El
chequeo semántico todavía no cambia: sigue dando `ERR_NO_SOPORTADO` sobre
el primer nombre.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (alcance), D2 (gramática) y D3 (AST).
Glosario: *variable local clásica*, *declarador*.

**Dónde:**
- Parser: `<RestoDeclLocal>`, `<Declarador>`, `<MasDeclaradores>` e
  `<InitLocalOpc>` en reemplazo de `<RestoDeclLocal>` y `<MasIdsLocal>`,
  con la producción en el comentario de cada método. La rama tipo de
  `<SentIdClase>` suma el `[` a su FIRST. `<TrasIdForTipo>` usa el nuevo
  `<InitLocalOpc>`.
- `NodoVarLocalClasica` y su declarador. `primerToken()` sigue siendo el
  token del tipo, y `ERR_DECLARACION_COMO_CUERPO` sigue sobre el primer
  nombre.
- `etapa2-sintactico/analizador-sintactico.md`: la gramática expandida, la
  factorización de `<Sentencia>`, el párrafo "Init compartido", los
  FIRST/FOLLOW nuevos y REQ-AS-006 (sin "un valor común al final para
  toda la lista").
- Casos sintácticos:
  - `conErrores/sintError44` pasa a `sinErrores/sintCorrecto39`, con el
    comentario reescrito.
  - `conErrores/sintError50`: la etiqueta pasa a la línea 8 (el `[` sin
    cerrar). Se fija corriendo el caso y se actualiza el comentario.
  - Nuevos: `sintCorrecto40` (declaradores mezclados, arreglos de
    primitivo, clase, genérico instanciado y parámetro de tipo con una y
    dos dimensiones, y un `for` con local clásica), `sintError64`
    (`int x = 1, ;`, error sobre el `;`) y `sintError65`
    (`for (int i = 0, j = 0; …)`, error sobre la `,`).

**Criterios:**
- [ ] Los casos sintácticos de arriba, en verde.
- [ ] `int x = ;` e `int[ a;` dan un error sintáctico claro.
- [ ] Los casos de modo pánico (`TesterSintacticoModoPanico`) no cambian.
- [ ] Ningún caso de las Etapas 1–3 ni de Sem2 (base y logros habilitados)
      cambia de etiqueta, salvo `sintError44` y `sintError50`.
- [ ] `analizador-sintactico.md` describe la gramática nueva.
- [ ] `./test.sh` en verde.
