---
titulo: Referencia adelantada
estado: hecho
spec: ../../0005-atributos-inicializados.md
---

# 02: Referencia adelantada

**Qué se construye:** usar, por su nombre suelto y dentro del inicializador
de un atributo, un atributo de la misma clase que no está declarado antes
(el propio atributo incluido) da `ERR_REFERENCIA_ADELANTADA` sobre el nombre
usado, como en Java. Cuenta aunque el nombre se use como argumento
(`m(b)`), indexado (`b[0]`), con encadenado (`b.x`) o con un postfijo
(`b++`), y aunque los dos atributos estén en la misma línea. No cuentan el
destino directo de un `=` (`(b = 3)`), los accesos con receptor (`this.b`,
`obj.b`), los atributos heredados ni lo que lea un método llamado. En
métodos y constructores la regla no aplica.

**Bloqueado por:** 01.

**Decisiones:** de la spec, D5 (índice de declaración), D6 (dónde y cuándo
se reporta) y D7 (excepción del destino). Glosario: *referencia
adelantada*. JLS §8.3.3.

**Dónde:**
- `Atributo` lleva un índice de declaración que asigna
  `Clase.agregarAtributo()` con un contador de la clase, en el orden del
  fuente. Un atributo duplicado no se registra y no recibe índice. No se usa
  la línea ni el orden del `HashMap`.
- `NodoAccesoVar.chequear`: con `atributoActual` seteado, si el nombre
  resuelve a un atributo que declara la clase actual y cuyo índice no es
  menor que el de `atributoActual`, lanza `ERR_REFERENCIA_ADELANTADA` sobre
  el nombre. Va después de resolver el nombre y antes de seguir con el
  encadenado.
- `NodoExpAsignacion` chequea su lado izquierdo "como destino". Sólo un
  `NodoAccesoVar` sin encadenado lo usa para saltear la regla: `b[0] = 1` y
  `b.x = 1` siguen siendo referencias adelantadas. `NodoExpPostfija` no
  cambia.
- `ERR_REFERENCIA_ADELANTADA` en la tabla de códigos con su descripción
  ("el atributo X se usa en un inicializador antes de su declaración").
- Casos nuevos en `logros/atributosInicializados/` (tabla de la spec).

**Criterios:**
- [x] Casos de `logros/atributosInicializados/` en verde: Correcto 04 y 05;
      Error 08–13 y 16.
- [x] En `int a = (c = b); int b; int c;` se marca `b` y no `c`.
- [x] Siguen en verde los casos del ticket 01 y los demás logros
      habilitados (en particular `visibilidadChequeada` y
      `multiDeteccionErrores`).
- [x] `./test.sh` en verde. Las Etapas 1–3 no cambian.

**Notas de la implementación:** `NodoExpresion.chequearComoDestino` delega
en `chequear`; sólo `NodoAccesoVar` lo redefine, y saltea la regla cuando no
tiene encadenado. `Clase.chequearSentencias` encola los atributos por índice
de declaración antes de ordenar todo por línea (el orden es estable), así
dos atributos en una línea se reportan en el orden del fuente. En este
ticket se agregó `atributosInicializados` a `LOGROS_HABILITADOS`.
