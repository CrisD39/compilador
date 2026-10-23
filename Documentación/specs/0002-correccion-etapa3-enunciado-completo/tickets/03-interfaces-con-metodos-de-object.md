---
titulo: Interfaces con los métodos de instancia de Object
estado: hecho
spec: ../../0002-correccion-etapa3-enunciado-completo.md
---

# 03: Interfaces con los métodos de instancia de `Object`

**Qué se construye:** al consolidar, una interfaz incorpora los métodos de
instancia de `Object` (`toString`) que no redefine, para que la Etapa 4 los
encuentre sobre un receptor interfaz. Los estáticos de `Object` no se
incorporan. Ningún error existente cambia ni se duplica.

**Bloqueado por:** 01.

**Historias:** 11–17.

**Dónde:** la comparación de los métodos propios contra `Object` pasa a
un paso que corre después de heredar de las superinterfaces y que además
copia los que faltan. El contrato de una clase saltea los métodos de la
interfaz que son los de `Object`.

- [x] Pasa `semCorrecto39`.
- [x] Siguen pasando `semError63` (`int toString()` en una interfaz),
      `semCorrecto32` (`void debugPrint(int)` en una interfaz) y
      `semError94`.
- [x] Verificado a mano: una interfaz con `int toString()` que extiende
      otra da un solo error, y una clase con `static String toString()`
      que implementa una interfaz no da errores en la línea 0.
- [x] Todos los testers de Etapas 1–3 en verde.
