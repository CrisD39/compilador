---
titulo: Tipo esperado en los argumentos
estado: hecho
spec: ../../0007-ternarios.md
---

# 03: Tipo esperado en los argumentos

**Qué se construye:** un ternario que se pasa como argumento se tipa
contra el parámetro formal que lo recibe, visto desde el receptor. Vale en
las llamadas a métodos de instancia, estáticos y encadenados, en `new` y
en `super(...)`. Así valen `usar(c ? new B2() : new C2())` con un
parámetro `A1`, y `cs.poner(c ? "a" : null)` con `cs` de tipo
`Caja<String>`, donde cada rama se compara con `String`. Los argumentos
siguen en dos fases: primero se tipan todos, en orden del fuente (y ahí
salen los errores de rama de un ternario), y después cada uno se compara
con su formal y da `ERR_ARGUMENTO_INCOMPATIBLE`. En `m(1, c ? x : "a")`
con formales `(String, int)` se informa el `?` y no el `1`. Un método con
parámetros de tipo propios tipa sus argumentos sin tipo esperado, como
hoy, antes de dar `ERR_NO_SOPORTADO`. En `super(...)` los argumentos
siguen en el contexto previo a la construcción, así que
`super(c ? this : null)` da `ERR_REFERENCIA_ANTES_DE_SUPER` sobre `this`.

**Bloqueado por:** 02.

**Decisiones:** de la spec, D3 (el punto de `chequearArgumentos`) y D6
(*Super!* y métodos genéricos). ADR 0011 (las dos fases en las llamadas).
Glosario: *tipo esperado*, *contexto previo a la construcción*.

**Dónde:**
- `ChequeoLlamada.chequearArgumentos`: recibe los formales y la vista del
  método o del constructor ya resuelto por aridad, y chequea cada argumento
  con `chequearConTipoEsperado` y su formal sustituido por la vista. Los
  dos ya están resueltos antes de tipar los argumentos, así que no hace
  falta reordenar nada.
- Sus llamadores: las tres llamadas a método y `chequearConstructor` (que
  sirve a `new` y a `super(...)`, con el contexto previo a la construcción
  envolviendo los argumentos como hoy). Un método con parámetros de tipo
  propios los tipa sin tipo esperado.
- Casos nuevos en `logros/ternarios/` (tabla de la spec).

**Criterios:**
- [x] Casos de `logros/ternarios/` en verde: Correcto 03; Error 14, 15, 16
      y 22. Siguen en verde los de los tickets 01 y 02.
- [x] Ningún caso de `ERR_ARGUMENTO_INCOMPATIBLE` sin ternario cambia de
      etiqueta, en la base ni en los logros.
- [x] Ningún caso base de `semantico2/{sinErrores,conErrores}` ni de las
      Etapas 1–3 cambia de etiqueta.
- [x] Siguen en verde los demás logros habilitados.
- [x] `./test.sh` en verde.
