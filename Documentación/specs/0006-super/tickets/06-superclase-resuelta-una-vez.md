---
titulo: Resolver la superclase una sola vez
estado: ready-for-agent
spec: ../../0006-super.md
---

# 06: Resolver la superclase una sola vez

**Qué se construye:** prefactor sin cambio de salida. `super(...)` usa la
superclase que la consolidación ya dejó resuelta (el padre efectivo, que
es `Object` sin `extends`) en vez de buscarla otra vez por nombre y
castearla a `Clase`. Si el invariante de la D5 de la spec 0006 (sin errores
de declaración no corre el chequeo) se rompiera, hoy saldría una
`ClassCastException` en vez de un error.

Con eso, la resolución del constructor deja de recibir la clase y su tipo
por separado: la clase es siempre la entidad del tipo, y se deriva adentro
(`TablaSimbolos` ya sabe dar la entidad de un tipo). Los dos llamadores,
`new` y `super(...)`, pasan solo el tipo.

De paso, el tipo de la superclase sin `extends` deja de crear un `Object`
nuevo en cada acceso con `super` o `super(...)`: sale del padre efectivo o
se guarda.

**Bloqueado por:** ninguno (puede empezar ya).

**Origen:** `revision.md`, §2.1, §2.2 y §2.7.

**Criterios:**
- [ ] `super(...)` no busca la superclase por nombre ni hace un cast sin
      chequear.
- [ ] La resolución del constructor recibe el tipo y no la clase; `new` y
      `super(...)` la llaman igual.
- [ ] El tipo de la superclase implícita (`Object`) no se construye en
      cada llamada.
- [ ] Los comentarios que describen esos pasos (D8/D10 en
      `NodoLlamadaSuperConstructor`, `NodoCreacionObjeto` y la resolución
      del constructor) siguen diciendo lo que hace el código.
- [ ] Ninguna salida cambia: `./test.sh` en verde, sin tocar casos.
