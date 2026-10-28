---
titulo: Contexto previo a la construcción en un solo lugar
estado: hecho
spec: ../../0006-super.md
---

# 07: Contexto previo a la construcción en un solo lugar

**Qué se construye:** prefactor sin cambio de salida, sobre la marca del
*contexto previo a la construcción* (D9 de la spec 0006).

- **Se anida:** `super(...)` guarda el valor anterior de la marca y lo
  restaura al terminar (aunque salga por una excepción), en vez de
  apagarla. Así un chequeo anidado que también la use no la apaga mientras
  el externo sigue chequeando argumentos.
- **Cubre solo los argumentos**, como dice la D9: la búsqueda del
  constructor por aridad y la visibilidad corren fuera de ella. Por
  ejemplo, la resolución del constructor recibe si los argumentos se
  chequean en ese contexto, y prende la marca solo alrededor de su
  chequeo.
- **La regla "el objeto actual no está disponible" está en un solo
  lugar:** hoy `this`, el acceso con super, el atributo suelto y la llamada
  sin receptor a un método de instancia repiten cada uno el control de
  contexto estático y el del contexto previo a la construcción. Un helper
  en `TablaSimbolos` (que recibe el token y el código del error estático)
  hace los dos, y los cuatro nodos lo usan. Un nodo nuevo que use el
  receptor implícito no puede olvidarse de uno.

**Bloqueado por:** 06 (los dos tocan `super(...)` y la resolución del
constructor).

**Origen:** `revision.md`, §2.3, §2.4 y §2.5.

**Criterios:**
- [x] La marca se restaura al valor anterior, no a `false`.
- [x] La marca está prendida solo mientras se chequean los argumentos de
      `super(...)`; el comentario de `NodoLlamadaSuperConstructor` que
      admite lo contrario se reescribe.
- [x] Ningún nodo consulta las dos marcas por su cuenta: los cuatro usan
      el helper, y el error que gana en cada caso es el mismo que hoy
      (estático antes que previo a la construcción).
- [x] Ninguna salida cambia: `./test.sh` en verde, sin tocar casos (en
      particular `superKeyword`, `atributosInicializados` y
      `multiDeteccionErrores`).
