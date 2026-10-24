---
titulo: Objetos y llamadas
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 04: Objetos y llamadas

**Qué se construye:** referencias a clases e interfaces no genéricas:
conformidad entre ellas, `this`, encadenados `.x` y `.m()`, creación de
objetos, llamadas en sus tres formas y contexto estático.

**Bloqueado por:** 03.

**Decisiones:** D9 (clases e interfaces, sin argumentos genéricos todavía),
D5 (`void` → `Tipo.VOID`) y D11 (visibilidad ignorada; método genérico y
diamante "no soportado") de la spec. `requerimientos.md`: "Nodos
encadenados", "Nodos asignables y llamables" y "Receptor de tipo
interfaz".

**Alcance:**
- `Tipo.conformaCon` para clases e interfaces: ancestros por
  `extends`/`implements`, transitivo, todo llega a `Object`.
- `NodoThis`: tipo de la clase actual. Da `ERR_THIS_EN_ESTATICO` en un método
  estático.
- `NodoVarEncadenada` (`.x`): busca en el tipo **estático** del receptor
  (`ERR_ATRIBUTO_NO_ENCONTRADO`). Un receptor primitivo da
  `ERR_RECEPTOR_SIN_MIEMBROS`.
- `NodoLlamadaMetodo` (`m()`): busca en la clase actual por nombre/aridad
  (`ERR_METODO_NO_ENCONTRADO`). En un contexto estático sólo admite
  estáticos (`ERR_METODO_NO_ESTATICO`).
- `NodoLlamadaEstatica` (`C.m()`): la clase tiene que existir
  (`ERR_TIPO_NO_DECLARADO`) y el método tiene que ser estático.
- `NodoLlamadaEncadenada` (`.m()`): receptor clase o interfaz (la interfaz
  incluye los métodos de `Object`). Un receptor primitivo da
  `ERR_RECEPTOR_SIN_MIEMBROS`.
- En las tres llamadas: aridad y conformidad de cada argumento
  (`ERR_ARGUMENTO_INCOMPATIBLE`, sin buscar otra variante por tipos), y un
  argumento `void` es error.
- `NodoCreacionObjeto`: rechaza una interfaz (`ERR_NEW_INTERFAZ`) y una clase
  inexistente. Selecciona el constructor por aridad, sin el constructor por
  defecto si la clase declara alguno y sin buscar en ancestros
  (`ERR_CONSTRUCTOR_NO_ENCONTRADO`). Chequea los argumentos.
- `NodoLiteralString` con encadenado (`"texto".toString()`).
- `void` como operando, valor asignado, inicializador de `var` o valor de
  `return`.
- `esLlamable` completo en `NodoSentenciaExpresion` (§12.2), incluido un
  primario parentizado (`(p1).m2();`).

**Casos que pasan a verde:**
- [ ] Con error (38): `semIIError02`, `03`, `04`, `09`, `10`, `12`, `13`,
      `14`, `15`, `16`, `17`, `18`, `19`, `21`, `22`, `23`, `25`, `26`,
      `27`, `29`, `30`, `31`, `37`, `38`, `39`, `48`, `49`, `60`, `62`,
      `65`, `66`, `77`, `81`, `82`, `89`, `101`, `102`, `103`.
- [ ] Sin error (15): `semIICorrecto04`, `05`, `06`, `08`, `09`, `10`, `18`,
      `19`, `20`, `21`, `22`, `23`, `24`, `26`, `30`.
- [ ] Siguen en verde los casos del ticket 03 y `TesterLogrosSem2`.
- [ ] `./test.sh` (Etapas 1–3) en verde.
