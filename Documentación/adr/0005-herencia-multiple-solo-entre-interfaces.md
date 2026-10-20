# Herencia múltiple sólo entre interfaces

El logro "Herencia múltiple (E3)" dice que el compilador "permite herencia
múltiple de interfaces, como en Java". Se lee como herencia entre
interfaces: una interfaz puede extender varias (`interface X extends A, B`),
pero una clase implementa como máximo una interfaz
(`class X implements A, B` es un error sintáctico). La gramática base
(`<HerenciaOpcional> ::= extends <TipoReferencia> | implements <TipoReferencia> | ϵ`)
y el §4 del enunciado ("no permite … extender o implementar varios tipos")
admiten una sola relación por clase, y el logro sólo nombra interfaces.

## Opciones consideradas

- **Lista también en `implements`**: es lo que hacía el proyecto antes. Es
  más seguro frente a los casos de la cátedra: si el logro incluía las
  clases y se rechaza `implements A, B`, falla cualquier caso positivo que
  lo use. Se descartó porque el logro no lo pide y la gramática base lo
  prohíbe.

## Consecuencias

- `<HerenciaOpcional>` vuelve a aceptar una única interfaz tras
  `implements`. `<ExtensionOpcional>` sigue aceptando una lista.
- Los casos que usaban `implements` con lista se reescriben con una
  interfaz intermedia (`interface AB extends A, B` + `class X implements AB`):
  `sintCorrecto31`, `sintError52`, `semCorrecto10`, `semCorrecto13`,
  `semCorrecto15` (hoy `semError87`, por la clave), `semCorrecto21`,
  `semError30`, `semError33`, `semError36` y `semError44`. Uno de ellos pasa a ser el caso de error
  sintáctico de `implements A, B`.
- Si la cátedra confirma que el logro incluye las clases, alcanza con volver
  a la lista en `<HerenciaOpcional>` y restaurar esos casos.
