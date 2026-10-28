---
titulo: Chequeo de la local clásica en un bloque
estado: hecho
spec: ../../0009-variables-locales-clasicas.md
---

# 02: Chequeo de la local clásica en un bloque

**Qué se construye:** una local clásica en un bloque deja de dar "no
soportado" y se chequea como en Java, salvo por la asignación definida.

- **El tipo** se resuelve una vez, con la misma resolución que el for-each
  (`Tipo.resolverEnCuerpo`). Si falla, el error va sobre el token del tipo,
  no se chequea ningún inicializador y cada nombre que no choca se declara
  sin tipo.
- **Los declaradores** se procesan de izquierda a derecha. Primero, el
  inicializador se chequea con `chequearConTipoEsperado` contra el tipo
  declarado: si no conforma, `ERR_ASIGNACION_INCOMPATIBLE` sobre su `=`, y
  un error del ternario va sobre su `?` sin un segundo error. Después, si
  el nombre es una local visible o un parámetro, `ERR_LOCAL_DUPLICADA`
  sobre el nombre (un atributo no choca). Por último, se declara la local.
  Un inicializador ve los nombres anteriores pero no el suyo.
- **Un error por sentencia:** el primer error corta el chequeo. Antes de
  propagarlo se declaran, con el tipo declarado, el nombre del declarador
  que falló y los siguientes, salvo los que chocan, sin chequear sus
  inicializadores.
- **No hay asignación definida** (ADR 0012).

El `for` todavía da `ERR_FOR_INICIALIZACION` con una local clásica (ticket
03).

**Bloqueado por:** 01.

**Decisiones:** de la spec, D4 (tipo), D5 (declaradores), D6 (un error por
sentencia), D7 (asignación definida) y D9 (lo que no cambia). ADR 0011
(tipo esperado) y ADR 0012. Antecedente: `NodoVarLocal` (mismo orden y
misma recuperación sin tipo).

**Dónde:**
- `NodoVarLocalClasica.chequear`.
- `TesterLogrosSem2`: agregar `variablesLocalesClasicas` a
  `LOGROS_HABILITADOS`.
- `logros/variablesLocalesClasicas/`: Error 01 de la tabla de la spec
  reemplaza al `semIIVarLocalError01` actual (que probaba `var x = null`).
  Se suman los casos nuevos de las tablas. Cuando un caso tiene que
  distinguir declaradores (Error 02 y 03), el que falla va en su propia
  línea.

**Criterios:**
- [x] Casos del logro en verde: Correcto 01 (existe) y 02–09, Error 01–16,
      con exactamente las etiquetas de la tabla de la spec, en orden.
- [x] `semIIVarLocalCorrecto01` sigue en verde sin cambios, o se ajusta
      si dependía del comportamiento viejo (anotarlo).
- [x] Ningún caso base de `semantico2/{sinErrores,conErrores}` ni de las
      Etapas 1–3 cambia de etiqueta.
- [x] Siguen en verde los demás logros habilitados.
- [x] `./test.sh` en verde.
