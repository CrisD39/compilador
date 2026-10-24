---
titulo: Genéricos
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 06: Genéricos

**Qué se construye:** tipos genéricos en expresiones: la vista desde el
receptor, la conformidad con argumentos, el chequeo paramétrico de los
cuerpos de una clase genérica, el contexto estático y la ausencia de tipos
raw en `new`.

**Bloqueado por:** 05.

**Decisiones:** D9 (argumentos invariantes, `T`), D10 (vista desde el
receptor) y D11 (diamante y método genérico "no soportado") de la spec.
`requerimientos.md`: "Receptor parámetro de tipo" y "Tipos raw". ADR 0006
(parámetro de tipo de la clase en estáticos) y ADR 0007 (sin tipos raw).

**Alcance:**
- `Tipo.conformaCon`: con `S` ancestro, el argumento con que `S` ve a `D`
  (`instanciacion`) tiene que ser **igual** al de `D`. `T` sólo conforma con
  el mismo `T`, y `null` conforma con `T`.
- `TablaSimbolos.instanciacionDe(Tipo receptor)`, aplicada con
  `Tipo.sustituir` en `NodoVarEncadenada`, `NodoLlamadaEncadenada`,
  `NodoCreacionObjeto` (parámetros del constructor) y en los miembros
  heredados resueltos sin receptor (`obtener()` en `Final extends
  Intermedia<String>`).
- `this` dentro de `Caja<T>` tiene tipo `Caja<T>`.
- Herencia sin argumento (`Derivada extends Base`): el método heredado sigue
  devolviendo `T`, sin borrarse a `Object`.
- Un receptor `T` sin instanciar no tiene miembros, tampoco `toString()`
  (`ERR_RECEPTOR_SIN_MIEMBROS`).
- `T` en el cuerpo de un método estático (por ejemplo, en `new T[10]`) da
  `ERR_PARAMETRO_TIPO_EN_ESTATICO` sobre `T`.
- `new Caja()` con `Caja` genérica da `ERR_TIPO_SIN_ARGUMENTO`. `new
  Caja<>()` y la llamada a un método con parámetros de tipo propios dan
  `ERR_NO_SOPORTADO` (D11).
- Arreglos con tipo base paramétrico o genérico instanciado (`new T[10]`,
  `new Caja<String>[10]`) en un contexto de instancia.

**Casos que pasan a verde:**
- [ ] Con error (9): `semIIError11`, `20`, `24`, `28`, `32`, `40`, `41`,
      `42`, `43`.
- [ ] Sin error (8): `semIICorrecto11`, `12`, `13`, `14`, `16`, `32`, `33`,
      `34`.
- [ ] Siguen en verde los casos de los tickets 03–05 y `TesterLogrosSem2`.
- [ ] `./test.sh` (Etapas 1–3) en verde.
