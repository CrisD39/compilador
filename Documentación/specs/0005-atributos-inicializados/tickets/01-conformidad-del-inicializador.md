---
titulo: Conformidad del inicializador
estado: hecho
spec: ../../0005-atributos-inicializados.md
---

# 01: Conformidad del inicializador

**Qué se construye:** un atributo inicializado deja de dar "no soportado".
Su inicializador se chequea en su clase de origen, como un contexto de
instancia: valen `this`, `this.x`, los atributos propios y heredados, las
llamadas sin receptor a métodos de instancia y los métodos estáticos, y la
visibilidad se controla como en cualquier cuerpo de la clase. El tipo del
inicializador tiene que conformar con el del atributo, con las mismas reglas
que el lado derecho de una asignación simple; si no, da
`ERR_ASIGNACION_INCOMPATIBLE` sobre el `=`. Un error adentro del
inicializador es el único error del atributo. La sintaxis de otros logros
sigue dando `ERR_NO_SOPORTADO` sobre su propio token. En este ticket todavía
no se controla la referencia adelantada.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (alcance: atributos propios, la tercera
pasada no cambia), D2 (conformidad igual que `NodoExpAsignacion`), D3
(`Atributo` guarda el token `=`), D4 (contexto de instancia), D8 (logros no
implementados) y D9 (contrato de salida, sin cascada). Glosario: *contexto
de chequeo*; un inicializador no es una *unidad*.

**Dónde:**
- Parser, rama del atributo inicializado de `<RestoMiembro>`: deja de
  descartar el token que devuelve `operadorAsignacion()` y se lo pasa al
  `Atributo`.
- `Atributo.chequearInicializador`: chequea el inicializador y compara su
  tipo con el del atributo con `conformaCon` (la versión que recibe la
  tabla). Sin conformidad, `ERR_ASIGNACION_INCOMPATIBLE` sobre el `=`.
- `TesterLogrosSem2`: agregar `atributosInicializados` a
  `LOGROS_HABILITADOS`.
- Casos nuevos en `logros/atributosInicializados/` (tabla de la spec).

**Criterios:**
- [x] Casos de `logros/atributosInicializados/` en verde: Correcto 01
      (existe), 02 y 03; Error 01 (existe), 02–07, 14 y 15.
- [x] `class A { int a = 5; }` da `[SinErrores]` con el jar.
- [x] Ningún atributo inicializado da `ERR_NO_SOPORTADO` sobre su nombre;
      un ternario en el inicializador lo da sólo sobre el `?`.
- [x] Ningún caso base de `semantico2/{sinErrores,conErrores}` cambia de
      etiqueta.
- [x] `./test.sh` en verde. Las Etapas 1–3 no cambian.

**Notas de la implementación:** `Atributo.setInicializador` recibe también
el token `=`, que el parser ya no descarta. `chequearInicializador` chequea
la expresión y compara con `conformaCon(tipo, tabla)`, igual que
`NodoExpAsignacion`. El ejemplo del criterio se probó como
`class A1 { int a = 5; }` (`A` suelto es un `idGen` para el léxico): da
`[SinErrores]` con el jar. Los casos base no cambian (92 sin error, 171 con
error).
