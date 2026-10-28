---
titulo: Número seguido de letra (Etapa 1)
estado: ready-for-agent
origen: corrección de la cátedra (2026-10-28), plan 1 de 2 (el 2 es la gramática de locales clásicas)
fecha: 2026-10-28
---

# Número seguido de letra (Etapa 1)

## Problem Statement

La corrección reporta que `4.metodo1()` da un error **léxico**:

```
Error Léxico en línea 5: 4.metodo1 un literal numérico no puede ir seguido inmediatamente de una letra o guion bajo
[Error:4.metodo1|5]
```

El léxico arma un único lexema inválido (`4.metodo1`) con todo lo que
viene pegado al número. Esa regla se agregó a propósito en la Etapa 1
(`consumirSufijoInvalidoDeNumero`, ver "Dígito seguido inmediatamente de
letra o `_`" en `etapa1-lexico/informe-analizador-lexico.md`), pero es
incorrecta: ningún requerimiento léxico prohíbe que un número vaya seguido
de una letra, y decidir que esa secuencia no forma un programa válido es
trabajo del sintáctico. Así lo hace javac: parte la entrada en tokens
válidos y el error lo da el parser.

## Solution

Un literal numérico termina donde dejan de valer sus caracteres, y lo que
sigue arranca un token nuevo, sin error léxico. `4.metodo1` son tres tokens
(`4`, `.`, `metodo1`), y el sintáctico reporta el error sobre el `.`
porque un literal no admite encadenamiento (`<Primitivo>` no es
`<Referencia>`). Los floats que Java forma con una letra después del punto
(`3.f`, `3.e5`) siguen siendo floats.

## User Stories

1. Como estudiante, quiero que `x = 4.metodo1();` dé un error sintáctico sobre el `.`, para que el mensaje diga qué construcción está mal y no invente un "literal inválido".
2. Como estudiante, quiero que `345a`, `5Persona`, `100L` y `0x1F` se lean como un número seguido de un identificador, como en javac, y que el error (si lo hay) lo dé el parser.
3. Como estudiante, quiero que `3.f`, `3.14f` y `3.e5` sigan siendo floats válidos, como en Java (REQ-AL-21).
4. Como estudiante, quiero que `1_000` siga siendo un error léxico, porque `_` no es un carácter válido para empezar un token.

## Implementation Decisions

### D1. La regla general

Un literal numérico se forma con la misma estructura de siempre (dígitos,
parte decimal opcional, exponente opcional, sufijo `f`/`F`/`d`/`D`
opcional en un float). Cuando aparece un carácter que no continúa esa
estructura, el literal se cierra y el carácter queda como lookahead para el
token siguiente, **sea o no una letra o `_`**. Desaparece el error "un
literal numérico no puede ir seguido inmediatamente de una letra o guion
bajo" y, con él, `consumirSufijoInvalidoDeNumero`.

| Entrada | Tokens |
| :---- | :---- |
| `345a` | `intLiteral(345)` `idMV(a)` |
| `5Persona` | `intLiteral(5)` `idClase(Persona)` |
| `100L` | `intLiteral(100)` `idGen(L)` |
| `0x1F` | `intLiteral(0)` `idMV(x1F)` |
| `3.14x` | `floatLiteral(3.14)` `idMV(x)` |
| `3.14fx` | `floatLiteral(3.14f)` `idMV(x)` |
| `3e10x` | `floatLiteral(3e10)` `idMV(x)` |
| `1_000` | `intLiteral(1)`, error léxico en `_`, `intLiteral(000)` |

### D2. Dígitos seguidos de `.`

El caso del reporte. Después de los dígitos y un `.`, el carácter
siguiente decide:

- un dígito: parte decimal, como hoy (`3.14`);
- `e`/`E`: exponente, como hoy (`3.e5`);
- `f`/`F`/`d`/`D`: sufijo del float `3.` (`3.f`), como en Java;
- cualquier otra letra o `_`: **el `.` no es parte del número**. Se emite
  `intLiteral` con los dígitos y después `punto`, y la letra arranca el
  token siguiente (`4.metodo1` → `4` `.` `metodo1`; `3.a` → `3` `.` `a`);
- cualquier otro carácter (blanco, `;`, `)`, `.`, EOF): el float `3.`,
  como hoy.

Consecuencias, iguales a las de javac: `4.foo()` es `floatLiteral(4.f)`
`idMV(oo)`, y `4.equals()` es el error léxico de exponente sin dígitos sobre
`4.e`. No se agrega lookahead de más de un carácter para "adivinar" si la
letra empieza un nombre.

### D3. Mecanismo: un token en reserva

Cuando D2 decide partir, el `.` ya está consumido y la letra es
`caracterActual`. `SourceManager` no tiene `unread`/`peek` y no se le
agrega: el léxico arma el `intLiteral` y deja el `punto` (lexema `.`, misma
línea) como **token en reserva**, que la llamada siguiente devuelve antes de
leer otro carácter. Los dos puntos de entrada (`startAnalizar`, del tester
léxico, y `nextToken`, del parser) vacían la reserva antes de llamar a
`estadoInicial()`. La letra sigue en `caracterActual`, así que el
identificador se arma por el camino normal.

### D4. Lo que no cambia

- El exponente sin dígitos (`3e`, `3e+`, `4.e`) sigue siendo error léxico
  (`lexConErrores09`).
- El límite de 9 dígitos de `intLiteral` se aplica a los dígitos del
  número: `1234567890a` es error léxico sobre `1234567890` y después
  `idMV(a)`.
- `.5` sigue siendo `punto` + `intLiteral(5)`.
- La gramática y el sintáctico no cambian: con los tokens partidos, el
  parser ya reporta el error donde corresponde.

### D5. Contrato de salida

- `x = 4.metodo1();` en ET2/ET4: `[Error:.|L]` (se esperaba `;` y se
  encontró `.`), sin error léxico.
- `x = 345a;`: `[Error:a|L]`.
- El tester léxico (`ModuloPrincipal`) imprime los tokens partidos y
  ningún error para `345a`, `5Persona`, `100L`, `0x1F`, `3.a`.

## Testing Decisions

- **Casos léxicos que cambian** (`resources/conErrores/`):
  - `lexConErrores13` (`345a`), `15` (`100L`), `16` (`0x1F`), `17`
    (`5Persona`) y `18` (`3.a`) dejan de tener error léxico. Se mudan a
    `resources/sinErrores/` como casos de tokens (`//#lexema,línea`) que
    muestran la partición de D1/D2; los números de `lexConErrores` que
    quedan libres no se reutilizan.
  - `lexConErrores14` (`1_000`) sigue en `conErrores` con la etiqueta
    `[Error:_|2]`.
- **Casos léxicos nuevos** (`resources/sinErrores/`): `3.f`, `3.e5`,
  `3.14fx`, `3e10x`, `4.metodo1` y `4.foo`, con sus tokens.
- **Casos sintácticos nuevos** (`resources/sintactico/conErrores/`,
  `sintError64` en adelante): el caso del reporte (`x = 4.metodo1();`,
  `[Error:.|L]`), `x = 345a;` (`[Error:a|L]`) y un `3.a` en una expresión.
- Probar a mano el archivo del reporte con el jar (ET4): una sola línea de
  error, sintáctica, sobre el `.`.
- `./test.sh` en verde.

## Out of Scope

- La gramática de las locales clásicas (`int x = 1, y = 2;`): es el plan 2,
  con su propia spec.
- Literales que MiniJava no tiene (hexadecimales, `long`, separador `_`):
  no se agregan; sólo cambia cómo se parten.

## Further Notes

- Esto revierte una decisión de la Etapa 1. En el informe hay que dejar
  escrito el porqué del cambio (la corrección y el criterio de javac), no
  borrar la historia.
- Para el informe: en estos casos no queda ninguna diferencia con javac;
  `4.foo()` y `4.equals()` se parten igual que en Java.

## Tickets

1. `01-corte-en-el-lexico`: D1–D4 en `AnalizadorLexicoImpl` y los casos.
2. `02-cierre`: documentación.
