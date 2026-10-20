---
titulo: Prefactor de Tipo (dimensiones, origen, igualdad)
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 08: Prefactor de `Tipo`: dimensiones, origen e igualdad

**Qué se construye:** `Tipo` tiene lo que necesita la comparación de
signaturas, sin cambiar todavía ninguna salida.

**Bloqueado por:** 01.

**Historias:** 42 (base), 46 (base), 72.

- [ ] El parser deja de descartar las dimensiones, y `Tipo` las guarda.
- [ ] Un `Tipo` que es parámetro de tipo guarda su origen (la clase,
      interfaz o método que lo declara), resuelto en el control de
      declaraciones.
- [ ] Igualdad de `Tipo`: cabeza, argumento (recursivo), dimensiones y, en
      los parámetros de tipo, el origen.
- [ ] Sin cambios en la salida: todos los casos dan lo mismo que antes del
      ticket.
