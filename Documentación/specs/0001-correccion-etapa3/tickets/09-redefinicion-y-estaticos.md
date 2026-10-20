---
titulo: Redefinición exacta y estáticos
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 09: Redefinición exacta y estáticos (sin genéricos)

**Qué se construye:** un método de instancia con la clave de uno heredado
lo redefine, y su signatura tiene que ser exactamente igual. Nadie puede
declarar un método con la clave de un estático heredado, y un estático no
puede tener la clave de un método de instancia heredado.

**Bloqueado por:** 04, 06, 08.

**Historias:** 39, 40, 41, 42, 43, 50, 51, 52, 53, 54.

- [ ] Pasa `semCorrecto24` (`String toString()` redefinido).
- [ ] `ERR_REDEFINICION_INCOMPATIBLE`: pasan `semError57`, `60`, `78`.
- [ ] `ERR_REDEFINE_ESTATICO`: pasan `semError58`, `59`, `81`, `82`, y
      `semError74` (tras descartar un `extends` con D1, `Object` es el
      padre efectivo).
- [ ] Los estáticos se siguen copiando a los descendientes, y se mantiene
      `ERR_METODO_REDEFINE_FINAL` (`semError22`, `42`).
- [ ] Ningún caso que pasaba deja de pasar.
