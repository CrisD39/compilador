---
titulo: Genéricos
estado: hecho
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
- [x] Con error (9): `semIIError11`, `20`, `24`, `28`, `32`, `40`, `41`,
      `42`, `43`.
- [x] Sin error (8): `semIICorrecto11`, `12`, `13`, `14`, `16`, `32`, `33`,
      `34`.
- [x] Siguen en verde los casos de los tickets 03–05 y `TesterLogrosSem2`.
- [x] `./test.sh` (Etapas 1–3) en verde.

**Nota de implementación:**
- Los tipos escritos en un cuerpo (`new Caja<T>()`, `new T[10]`) no pasan
  por `estaBienDeclarado()`. `Tipo.resolverEnCuerpo()` los valida con las
  reglas y los códigos de la Etapa 3, lanzando, y devuelve una copia con el
  origen de cada `T` resuelto (el método pisa a la clase). Sin el origen,
  `matriz[0][1] = this;` no conformaría (`semIICorrecto16`). El `T` de la
  clase dentro de un estático da `ERR_PARAMETRO_TIPO_EN_ESTATICO`, y un
  idGen que no declara nadie da `ERR_TIPO_GENERICO_NO_DECLARADO`.
- La conformidad con un ancestro usa `TablaSimbolos.vistaComoAncestro()`,
  que reemplaza a `heredaDe()`: sube por los supertipos tal como están
  escritos, sustituyendo en cada paso, y devuelve el ancestro con el
  argumento con que lo ve el tipo. Sube también por `implements`, que
  `Clase.getInstanciacion()` no incluye. Así `Hija extends Imp`,
  `Imp implements Prov<String>` conforma con `Prov<String>` y no con
  `Prov<Object>`. Heredado sin argumento, el `T` del ancestro se conserva y
  no iguala a ningún argumento concreto (D5).
- La vista desde el receptor (`instanciacionDe()`, D10) se aplica en `.x`,
  `.m()` y `new C<X>(...)`. Para los miembros resueltos sin receptor (`m()`
  y un atributo implícito) se usa `getInstanciacion()` de la clase actual.
  Las locales y los parámetros no se sustituyen. Una llamada estática pasa
  un mapa vacío, porque su firma no puede nombrar el `T` de la clase
  (ADR 0006).
- `this` es `Clase.comoTipo()`: `Caja<T>`, con el `T` de origen `Caja`.
- `EntidadDeclarada` expone `getInstanciacion()` (ya la tenían `Clase` e
  `Interfaz`).
