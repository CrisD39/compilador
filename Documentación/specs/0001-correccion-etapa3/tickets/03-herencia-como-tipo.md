---
titulo: Herencia como Tipo, con argumentos validados
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 03: Herencia como `Tipo`, con argumentos validados (D2, D7)

**Qué se construye:** el tipo nombrado en un `extends` o `implements` se
valida como cualquier tipo referencia, incluido su argumento genérico.
Heredar de un tipo genérico sin argumento sigue siendo válido. Un
supertipo inexistente se reporta una sola vez, como tipo no declarado.

**Bloqueado por:** 02.

**Historias:** 20, 21, 22, 23, 24.

- [ ] La herencia de una clase, su interfaz implementada y las interfaces
      extendidas se guardan como `Tipo` (cabeza más argumento opcional).
- [ ] Pasan `semError68` (`extends Caja<Nada>`), `semError69`
      (`extends Bb<Bb>`), `semError75` (`extends Caja<T>` con `T` fuera de
      alcance) y `semError84` (`implements Nada`).
- [ ] `semCorrecto33` (herencia sin argumento) sigue en verde.
- [ ] La consolidación saltea sin reportar las relaciones que no
      resuelven. Se retira `ERR_INTERFAZ_NO_DECLARADA`.
- [ ] Ningún caso que pasaba deja de pasar.
