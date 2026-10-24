---
titulo: Núcleo — expresiones, variables y sentencias
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 03: Núcleo: expresiones, variables y sentencias

**Qué se construye:** el chequeo de todo lo que no necesita objetos,
llamadas, arreglos ni genéricos. Incluye la multi-detección por sentencia y
los postfijos (D12).

**Bloqueado por:** 02.

**Decisiones:** D5, D6, D7, D9 (primitivos, `null`, `void`) y D12 de la
spec. `requerimientos.md`: ocultamiento (§10.1/§12.3), ⚠3 y la tabla de
`esAsignable`/`esLlamable`.

**Alcance:**
- `Tipo`: `conformaCon` para primitivos, `null` y `void`, más `esPrimitivo()`
  y las demás consultas, y las constantes `Tipo.NULL`/`Tipo.VOID`.
- Literales: entero, carácter, booleano, `null` y String (tipo `String`
  predefinido, sin encadenado todavía).
- `NodoExpBinaria`: aritméticos y `%` (`int`,`int` → `int`), relacionales
  (`int`,`int` → `boolean`), `&&`/`||` (`boolean`), `==`/`!=` (D9).
  `NodoExpUnaria`: `+`/`-` sobre `int` y `!` sobre `boolean`.
  `NodoExpPostfija` (D12).
- `NodoExpParentizada`: devuelve el tipo de adentro y corta `esAsignable` y
  `esLlamable`.
- `NodoAccesoVar` sin encadenado: local → parámetro → atributo
  (`buscarVariable`). Si no aparece, `ERR_VAR_NO_DECLARADA`. Un atributo
  implícito en un método estático da `ERR_ATRIBUTO_EN_ESTATICO`.
- `NodoVarLocal` (`var`): chequea el inicializador antes de declarar, no
  acepta `null` ni `void` (`ERR_VAR_SIN_TIPO`) y aplica el ocultamiento
  (`ERR_LOCAL_DUPLICADA`).
- `NodoExpAsignacion`: el lado izquierdo tiene que ser asignable
  (`ERR_DESTINO_NO_ASIGNABLE`) y el derecho conformar
  (`ERR_ASIGNACION_INCOMPATIBLE`). El resultado tiene el tipo del lado
  izquierdo.
- `NodoBloque`, `NodoSentenciaVacia`, `NodoIf`, `NodoWhile` (condición
  boolean, cuerpo no declaración: `ERR_DECLARACION_COMO_CUERPO`),
  `NodoReturn` (método `void`, no `void` y constructor) y
  `NodoSentenciaExpresion` (asignación, llamada, `new` de objeto o postfijo).
- `logros/postfijos/conErrores/semIIPostfijoError01.java`: `boolean b` pasa
  a parámetro de `m1`, y `b++` queda en la línea 7.

**Casos que pasan a verde:**
- [ ] Con error (43): `semIIError01`, `05`, `06`, `07`, `08`, `33`, `34`,
      `35`, `36`, `47`, `50`, `51`, `52`, `53`, `54`, `55`, `56`, `57`,
      `58`, `59`, `61`, `63`, `64`, `76`, `80`, `83`, `84`, `85`, `86`,
      `87`, `88`, `90`, `91`, `92`, `93`, `94`, `95`, `96`, `97`, `98`,
      `99`, `100`, `104`.
- [ ] Sin error (6): `semIICorrecto01`, `02`, `03`, `27`, `29`, `31`.
- [ ] `TesterLogrosSem2` en verde (multi-detección y postfijos).
- [ ] `./test.sh` (Etapas 1–3) en verde.
