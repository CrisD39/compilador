# Sin tipos raw fuera de la herencia

**Estado:** aceptado, implementado. Reemplaza al ADR 0001. Lo implementó el
ticket 02 de la spec 0002
(`specs/0002-correccion-etapa3-enunciado-completo/tickets/02-sin-tipos-raw.md`).

El ADR 0001 aceptaba `Caja dato;` en atributos, parámetros y retornos,
apoyándose en la Parte I §3 ("Puede tener un argumento genérico opcional:
`Caja`, `Caja<T>` o `Caja<String>`"). El enunciado completo (§1) dice que
define los controles de las **Etapas 3 y 4**, y que retoma la Parte I "con
las aclaraciones de genericidad y contexto estático definidas en este
documento". Una de esas aclaraciones es §5.2, "Ausencia de tipos raw": fuera
de `extends` e `implements`, usar una entidad genérica sin argumento es un
error semántico, y eso "incluye atributos, parámetros, retornos, tipos base
de arreglos y creaciones de objetos". §3 agrega que tampoco puede usarse una
entidad genérica sin argumento **como argumento** de otra. §8 ubica el
control en la segunda pasada de declaraciones ("uso completo de entidades
genéricas fuera de la excepción de herencia"), o sea, en la Etapa 3. §13 lo
repite, y
el logro *Genericidad Avanzada* también lo excluye. Se decide seguir el
enunciado completo, como ya se hizo con el contexto estático (ADR 0006).

```java
class Caja<T> { T contenido; }
class Lista<T> { }
class Aa extends Caja {           // válido: excepción de herencia sin argumento
    Caja dato;                     // error: atributo
    Caja obtener() { }             // error: retorno
    void poner(Caja c) { }         // error: parámetro
    Caja[] arr;                    // error: tipo base de arreglo
    Lista<Caja> anidado;           // error: argumento genérico
}
class Bb extends Caja<Lista> { }   // error: el argumento de la herencia no se exime
```

En la Etapa 4 la regla alcanza también a las variables locales, a los tipos
de `for`/`foreach` y a `new Caja()` (este último ya lo cubre
`semIIError24`).

## Opciones consideradas

- **Mantener el ADR 0001** (raw solo en declaraciones, `new Caja()` error).
  Se descartó: contradice §5.2, y en la Etapa 4 un tipo raw se acepta pero
  no se puede usar. Como los miembros conservan la `T` (§13 prohíbe el
  borrado a `Object`), `dato.contenido` tiene tipo `T` fuera de la clase que
  la declara, y ese tipo no conforma con nada.

## Consecuencias

- Cambia el comportamiento de la Etapa 3: antes, los seis errores del
  ejemplo daban `[SinErrores]`. `semCorrecto22` dejó de declarar
  `Lista cruda;`.
- La excepción de herencia sin argumento sigue igual: `class B extends Caja`
  conserva el parámetro de tipo original al consolidar (Parte I §5).
- El error es `ERR_TIPO_SIN_ARGUMENTO`, sobre el nombre del tipo
  (`[Error:Caja|<línea>]`). Casos: `semError98`–`104` y el gemelo sano
  `semCorrecto38`.
