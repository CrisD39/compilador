---
titulo: Parámetro de tipo de la clase en encabezados estáticos
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 13: Parámetro de tipo de la clase en encabezados estáticos (ADR 0006)

**Qué se construye:** un método estático no puede usar el parámetro de
tipo de su clase en el encabezado (retorno y parámetros). Tampoco puede
usarlo dentro de un arreglo ni como argumento genérico. Cada aparición da
`ERR_TIPO_GENERICO_NO_DECLARADO` sobre `T`, con la línea de ese token. Un
estático con su propio `<T>` sigue siendo válido (shadowing, Logro 3). Hoy
`class Caja<T> { static T identidad(T valor) {} }` da `[SinErrores]`.

**Bloqueado por:** 08 (hecho). Puede empezar ya.

**Historias:** 73.

**Dónde:** hoy `Clase.estaBienDeclarado()` le pasa `entornoClase` a todos
sus métodos. Para un estático tiene que pasar un entorno sin los parámetros
de la clase, y `Metodo.estaBienDeclarado()` agrega los propios como ya lo
hace. No hace falta un código nuevo (ver "Opciones consideradas" en el
ADR 0006).

- [x] Pasan `semError95` (`static T identidad(T valor)`, `[Error:T|8]`),
      `semError96` (`Caja<T>` como parámetro, `[Error:T|7]`) y `semError97`
      (`static T[] vacio()`, `[Error:T|6]`). Hoy fallan los tres.
- [x] Siguen pasando `semCorrecto37` (instancia con `T`, estático con su
      propio `<T>`, estático con `Caja<String>`) y `semCorrecto19`
      (`static <T>` en una clase no genérica).
- [x] `static T identidad(T valor)` reporta dos errores, uno por el retorno
      y otro por el parámetro (Logro 4).
- [x] El ADR 0006 pasa a "aceptado, implementado". La viñeta de §3.3 de
      `etapa3-semantico/casos-de-prueba.md` deja de decir "pendiente".
- [x] Ningún caso que pasaba deja de pasar: todos los testers de Etapas
      1–3 en verde. La spec vuelve a `estado: hecho`.
