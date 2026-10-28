---
titulo: Miembros heredados de una superclase genérica sin argumento
estado: hecho
spec: ../../0006-super.md
---

# 05: Miembros heredados de una superclase genérica sin argumento

**Qué se construye:** la Etapa 4 deja fijado con casos lo que ya hace hoy
cuando una clase hereda sin argumento de una superclase genérica
(`class Bb extends Aa` con `Aa<T>`, excepción de herencia del ADR 0007). El
parámetro `T` de los miembros heredados se conserva (spec 0001, D5) y no se
reemplaza por `Object`, así que ningún argumento concreto conforma con él:

```java
class Aa<T> { Aa(T t){} void m(T t){} }
class Bb extends Aa { Bb(){ super(5); } }      // [Error:super|2]
class Bb extends Aa { Bb(){ super(null); } }   // [SinErrores]
class Bb extends Aa { void p(){ super.m(5); } } // [Error:m|2]
class Bb extends Aa { void p(){ m(5); } }       // [Error:m|2]
```

En Java las cuatro compilan (`T` se borra a `Object`, con un warning
*unchecked*). Es una diferencia buscada: se decidió **mantenerla** y no
tratar el parámetro conservado como `Object` en la Etapa 4. No hay código
que cambiar.

**Bloqueado por:** ninguno (puede empezar ya).

**Origen:** `revision.md`, §1.1.

**Dónde:**
- Casos base en `resources/semantico2/` para la llamada sin receptor
  (`m(5)` con error, `m(null)` sin error).
- Casos en `logros/superKeyword/` para `super(5)`, `super.m(5)` (con
  error) y `super(null)` (sin error).
- Una línea en `etapa4-semantico2/requerimientos.md` (decisiones de la
  spec 0006 o la sección de herencia, la que corresponda) que diga que un
  miembro heredado sin argumento conserva su `T` en la Etapa 4, con la
  referencia a la D5 de la spec 0001 y al ADR 0007.
- Los casos nuevos en `etapa4-semantico2/casos-de-prueba.md` (índice base
  e índice "Casos del logro Super!").

**Criterios:**
- [x] Los casos nuevos dan la etiqueta de la tabla de arriba: `super` en
      `super(...)`, el nombre del método en `m(...)` y en `super.m(...)`.
- [x] Los casos con error del logro tienen exactamente esa etiqueta
      (`TesterLogrosSem2`), y el base la tiene como primera
      (`TesterConErroresSem2`).
- [x] `requerimientos.md` y `casos-de-prueba.md` actualizados; el conteo
      de casos de `superKeyword` en `CLAUDE.md` también.
- [x] `./test.sh` en verde.
