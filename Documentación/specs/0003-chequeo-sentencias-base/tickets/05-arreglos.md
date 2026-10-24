---
titulo: Arreglos
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 05: Arreglos

**Qué se construye:** arreglos de tipos no genéricos: creación con tamaño,
indexación, `length`, `toString()` y conformidad (§9.4, §11.8).

**Bloqueado por:** 04.

**Decisiones:** D9 (arreglos) de la spec. `requerimientos.md`: ⚠10
(`length` en `NodoVarEncadenada`), ⚠11 y "Receptor arreglo".

**Alcance:**
- `Tipo.conformaCon` para arreglos: invariante en tipo base y dimensiones.
  Todo arreglo conforma con `Object`, y `null` conforma con un arreglo.
- `NodoCreacionArregloConTamanio`: cada tamaño tiene que ser `int`
  (`ERR_INDICE_NO_ENTERO`). El tipo base es primitivo, clase o interfaz
  (`new I1[5]` es válido). El tipo tiene tantas dimensiones como tamaños.
  No es asignable ni llamable.
- `NodoAccesoArregloEncadenado` (`[i]`): el receptor tiene que ser un
  arreglo (`ERR_NO_ES_ARREGLO`, también si el tipo estático es `Object`). El
  índice tiene que ser `int`. Quita una dimensión y es asignable.
- `NodoVarEncadenada` con receptor arreglo: sólo `length` (`int`, no
  asignable). `length` sobre `Object` da `ERR_ATRIBUTO_NO_ENCONTRADO`.
- `NodoLlamadaEncadenada` con receptor arreglo: busca en los métodos de
  instancia de `Object`, así que sólo `toString()`. `clone()` da
  `ERR_METODO_NO_ENCONTRADO`.
- `NodoCreacionArregloConInicializador` sigue dando `ERR_NO_SOPORTADO`
  (logro).

**Casos que pasan a verde:**
- [ ] Con error (14): `semIIError44`, `45`, `46`, `67`, `68`, `69`, `70`,
      `71`, `72`, `73`, `74`, `75`, `78`, `79`.
- [ ] Sin error (5): `semIICorrecto07`, `15`, `17`, `25`, `28`.
- [ ] Siguen en verde los casos de los tickets 03–04 y `TesterLogrosSem2`.
- [ ] `./test.sh` (Etapas 1–3) en verde.
