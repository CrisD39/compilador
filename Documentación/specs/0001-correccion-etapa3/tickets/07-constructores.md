---
titulo: Constructores
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 07: Constructores (D3)

**Qué se construye:** un constructor cuyo nombre no es el de su clase da
error y no se registra. Una clase del programa sin constructores recibe uno
por defecto, sin parámetros. `Object` y `String` tienen constructor por
defecto; `System` no tiene ninguno.

**Bloqueado por:** 04.

**Historias:** 34, 35, 36, 37, 38.

- [ ] Pasa `semError70` (`ERR_CONSTRUCTOR_NOMBRE_INVALIDO`, con el lexema
      y la línea del constructor).
- [ ] Una clase con al menos un constructor no recibe el de por defecto.
- [ ] Los parámetros repetidos de un constructor siguen dando error
      (`semError13`).
- [ ] Ningún caso que pasaba deja de pasar.
