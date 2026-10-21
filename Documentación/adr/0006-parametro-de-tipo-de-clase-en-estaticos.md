# Parámetro de tipo de la clase prohibido en encabezados estáticos

**Estado:** aceptado, implementado. Lo implementó el ticket 13 de la spec 0001
(`specs/0001-correccion-etapa3/tickets/13-parametro-de-tipo-en-estaticos.md`).

El enunciado completo (§10.4, con referencias en §6.2 y §8) rechaza el
parámetro de tipo de la clase en el encabezado y en el cuerpo de un método
estático. La prohibición incluye que aparezca directo, dentro de un arreglo
o como argumento genérico. El §8 ubica el control del encabezado en la
segunda pasada de la Etapa 3. Se decide implementarlo así: la firma de un
método estático se valida en un entorno de genéricos que **no** incluye los
parámetros de la clase, solo los propios del método. Un `T` de la clase
queda fuera de alcance y se reporta con el error que ya existe,
`ERR_TIPO_GENERICO_NO_DECLARADO`.

```java
class Caja<T> {
    static T identidad(T valor) { }   // error sobre T (retorno y parámetro)
    static void poner(Caja<T> c) { }  // error sobre T (argumento genérico)
    static T[] arr() { }              // error sobre T (arreglo)
    static <T> T propio(T x) { }      // válido: este T es del método
}
```

`Clase.estaBienDeclarado()` le pasa a cada método estático un entorno vacío
en lugar de `entornoClase`, y `Metodo.estaBienDeclarado()` le agrega los
parámetros de tipo propios. Antes los tres primeros daban `[SinErrores]`.

## Opciones consideradas

- **Código nuevo** (p. ej. `ERR_PARAMETRO_TIPO_EN_ESTATICO`, "el parámetro
  de tipo T de la clase no puede usarse en un método estático"): el mensaje
  sería más preciso. Se descartó porque obliga a distinguir, en
  `Tipo.estaBienDeclarado()`, un `T` que no existe de uno que existe pero
  está oculto. Además el cuerpo (Etapa 4, `semIIError32`) tendría que
  repetir esa distinción. Si se valida en un entorno que no incluye a `T`,
  el encabezado y el cuerpo usan la misma regla. La etiqueta es igual en
  las dos opciones (`[Error:T|<línea>]`), así que los casos de prueba no
  dependen de esta elección.

## Consecuencias

- **Shadowing en estáticos.** El enunciado dice que "no hay métodos con
  parámetros de tipo propios en la gramática", pero el proyecto los admite
  por el Logro 3. Entonces `static <T> T propio(T x)` dentro de `Caja<T>`
  es válido, porque el `T` de la firma tiene origen en el método. Lo mismo
  vale para `semCorrecto19` (`static <T>` en una clase no genérica).
- Cada aparición de `T` en la firma es un error aparte (Logro 4).
  `static T identidad(T valor)` da dos errores en la misma línea, uno por
  el retorno y otro por el parámetro.
- Las interfaces no admiten métodos estáticos (error sintáctico), así que
  la regla solo afecta a `Clase`. MiniJava tampoco tiene atributos estáticos.
- El cuerpo del método sigue pendiente para la Etapa 4. El `Entorno` de un
  método estático tampoco debe incluir los parámetros de la clase.
- Casos: `semError95` (directo), `semError96` (argumento genérico),
  `semError97` (arreglo) y el gemelo sano `semCorrecto37`.
