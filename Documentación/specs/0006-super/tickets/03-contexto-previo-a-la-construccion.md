---
titulo: Contexto previo a la construcción
estado: hecho
spec: ../../0006-super.md
---

# 03: Contexto previo a la construcción

**Qué se construye:** en los argumentos de `super(...)` todavía no se
puede usar el objeto actual. `this`, un acceso con super, un atributo
propio o heredado por su nombre suelto y una llamada sin receptor a un
método de instancia dan `ERR_REFERENCIA_ANTES_DE_SUPER` sobre el token del
uso (`this`, `super`, el nombre del atributo o el del método). También
valen entre paréntesis o anidados (`super(new C(this))`, `super((x))`).
Siguen valiendo los parámetros del constructor (también uno con el nombre
de un atributo, porque resuelve antes), los literales, las llamadas
estáticas con o sin receptor, `new` de cualquier clase (la actual incluida)
y los miembros de otros objetos (`p.x`, `p.m()`). Fuera de esos argumentos
no cambia nada: en el resto del constructor, con o sin `super()`, se
puede usar el objeto actual.

**Bloqueado por:** 02.

**Decisiones:** de la spec, D6 (paso 2) y D9. Glosario: *contexto previo a
la construcción* (no es el contexto estático). JLS §8.8.7.1.

**Dónde:**
- `TablaSimbolos`: una marca que `NodoLlamadaSuperConstructor` prende
  mientras chequea sus argumentos y apaga al terminar, aunque salga por una
  excepción.
- Con la marca prendida, `ERR_REFERENCIA_ANTES_DE_SUPER` en `NodoThis` y
  `NodoSuper` (junto al control de contexto estático), en `NodoAccesoVar`
  (después de resolver el nombre a un atributo y su visibilidad) y en
  `NodoLlamadaMetodo` (método de instancia sin receptor), en el lugar del
  control de contexto estático.
- `ERR_REFERENCIA_ANTES_DE_SUPER` en la tabla de códigos, con su
  descripción.
- Casos nuevos en `logros/superKeyword/` (tabla de la spec).

**Criterios:**
- [x] Casos de `logros/superKeyword/` en verde: Correcto 05 y 06;
      Error 17 y 18.
- [x] Un error en los argumentos no deja la marca prendida: lo que sigue
      en el constructor se chequea normalmente.
- [x] Siguen en verde los casos de los tickets 01 y 02 y los demás logros
      habilitados (en particular `atributosInicializados` y
      `multiDeteccionErrores`).
- [x] `./test.sh` en verde. Las Etapas 1–3 no cambian.
