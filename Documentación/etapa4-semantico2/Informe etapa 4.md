# Informe Etapa 4 

- El main de esta etapa es `ModuloPrincipalET4` (el del jar). Los de las
  etapas anteriores siguen funcionando sin cambios.
- 
# Logros que se intentan en esta etapa

- **Multi-Detección**
- **Postfijos**
- **For**
- **Instanceof**
- **Visibilidad chequeada**
- **Todos los caminos llevan al retorno sin código muerto**
- **Atributos inicializados**
- **Super!**
- **Ternarios**
- **Variables Locales Clásicas**

# Logros de etapas anteriores

## Etapa 1 
- **Floats 1**

## Etapa 2

- **Fors!E2**

## Etapa 3

Logros de la etapa 3 que se intentan cubrir. Se re-intentan en la re-entrega del proyecto 3, pero se aclaran 
por las dudas, ya que las dos etapas se evalúan al mismo tiempo.

- **Sobrecarga!E3**
- **Herencia múltiple E3**
- **Métodos Genéricos E3**
- **Multi-detección errores semánticos E3**
- **Genericidad avanzada E3**



# Cómo correr el programa

Todo se corre desde la carpeta `Código/`. Primero se compila y se arma el jar, para eso dejo el bash:

./build.sh


Esto deja `out/Compilador.jar`. Para usalo, dependiendo la etapa, se hace:

## etapa 4

java -jar out/Compilador.jar <archivo-fuente>


El chequeo de sentencias solo corre si no hubo errores léxicos, sintácticos ni
de declaraciones.

## etapa 3

java -cp out/Compilador.jar View.ModuloPrincipalET3 <archivo-fuente>

## etapa 2

java -cp out/Compilador.jar View.ModuloPrincipalET2 <archivo-fuente>

## etapa 1

java -cp out/Compilador.jar View.ModuloPrincipal <archivo-fuente>


## Salida

Si no hay errores, todas las etapas terminan con `[SinErrores]`.
En caso de haber, se sigue el siguiente formato:
```
Error Semántico en línea <L>: <descripción>
[Error:<lexema>|<línea>]
```

# Errores de etapas anteriores corregidos

- **Etapa 2:** `4.metodo1()` ya no es un error léxico. El léxico lee
  `4.` como un float y `metodo1` como un identificador, como javac, y el
  error es sintáctico: `[Error:4.|L]`.
- **Etapa 2:** en una declaración de variables locales clásicas cada nombre
  tiene su propio inicializador opcional, como en Java: `int x = 1, y = 2;`
  es válido, y en `int x, y, z = 10;`. También se admiten
  locales de arreglo (`int[] a = new int[3];`).
- **Etapa 3:**  se corrigió la el formato de reporte de errores...

# Reporte de errores

Los errores se reportan con el formato del enunciado:

```
Error Semántico en línea <L>: <descripción>
[Error:<lexema>|<L>]
```

o `[SinErrores]` si el programa es correcto. 

## Cuántos errores se reportan (Multi-Detección)

- Cada sentencia se chequea por separado. Ante un error se reporta el
  **primero** de esa sentencia y el análisis sigue con la siguiente, y
  también con los demás métodos y clases.
- Los errores salen en el orden en que aparecen en el programa.
- No hay errores en cascada:
  - Una variable cuya declaración falló queda declarada igual (sin tipo, si
    el tipo era inválido), así que sus usos posteriores no vuelven a dar
    "variable no declarada".
  - Después de una sentencia inalcanzable no se marca como inalcanzable
    todo lo que sigue.

## Consideraciones: dónde se reporta cada error

Cada error se reporta sobre el token que mejor lo identifica: el nombre de
la entidad o el operador o palabra clave que lo dispara.

### Variables, asignaciones y llamadas

| Error | Se reporta en |
|---|---|
| Variable no declarada | el nombre de la variable |
| Atributo usado desde un método estático | el nombre del atributo |
| Atributo inexistente en el tipo del receptor (`e.x`) | el nombre del atributo |
| Valor que no conforma con el destino de una asignación | el `=` |
| Lado izquierdo de `=` que no es asignable | el `=` |
| Método inexistente o con otra cantidad de argumentos | el nombre del método |
| Argumentos que no conforman con los parámetros | el nombre del método, o el de la clase en un `new` |
| Método de instancia llamado sin receptor desde un contexto estático, o como `Clase.m()` | el nombre del método |
| Clase inexistente en un `new` o en `Clase.m()` | el nombre de la clase |
| `new` de una interfaz, o de una clase genérica sin argumento | el nombre del tipo |
| `new` sin un constructor con esa cantidad de argumentos | el nombre de la clase |
| Miembro pedido a un primitivo o a un `T` sin instanciar | el nombre del miembro |
| `this` en un contexto estático | el `this` |
| Índice o tamaño de arreglo que no es `int`, o indexar algo que no es arreglo | el `[` |

### Operadores y sentencias

| Error | Se reporta en |
|---|---|
| Operador con tipos incorrectos (`1 + true`, `!3`, `x == "a"`) | el operador |
| Condición que no es `boolean` | el `if`, el `while` o el `for` |
| `return` que no corresponde con el tipo de retorno (incluye `return e;` en un constructor o en un método `void`, y `return;` en uno no `void`) | el `return` |
| Expresión que no es una sentencia válida (`1 + 2;`, `x;`) | el `;` |
| Variable local repetida, o con el nombre de un parámetro | el nombre de la local |
| `var` inicializado con `null` o con una llamada `void` | el nombre de la local |
| Declaración local como cuerpo directo de `if`, `else`, `while` o `for` | el `var`, o el primer nombre en una local clásica |

La forma de una sentencia se controla **antes** que sus tipos Por eso `(4 + true);` da el error sobre el `;`, 
porque una expresión parentizada no es una sentencia, y no sobre el `+` (Diapositiva informe).


### Ternario

Cuando el ternario se usa como valor (`int x = c ? a : b;`, un argumento,
un `return`, un operando, …), todos sus errores se reportan sobre el
**`?`**. Usado como sentencia suelta, el error es el del `;` (ver arriba):

| Error | Se reporta en |
|---|---|
| Condición que no es `boolean` | el `?` |
| Una rama `void` (aunque lo sean las dos) | el `?` |
| Una rama que no conforma con el tipo esperado | el `?` |
| Dos ramas sin tipo esperado de las que ninguna conforma con la otra | el `?` |


### For

| Error | Se reporta en |
|---|---|
| Inicialización que no es una asignación, un postfijo, un `var` ni una local clásica | el `for` |
| Condición que no es `boolean` | el `for` |
| Actualización que no es una asignación ni un postfijo | el `for` |
| For-each sobre algo que no conforma con `MiniIterable` (arreglos incluidos) | el `for` |
| Elementos del for-each que no conforman con el tipo de la variable | el `for` |

El logro especifica que el for clásico tiene una asignación o un `var`, una
expresión booleana y una asignación. Aprovechando el logro *Postfijos*, se
consideró conveniente aceptar también un postfijo (`i++`, `i--`) donde va
una asignación (en la inicialización y en la actualización). Así
`for (int i = 0; i < n; i++)` es válido. Sólo cuenta la raíz de la
expresión: `i++ + 1` y `(i++)` siguen siendo error, igual que una llamada o
un `new`. Los errores del propio postfijo (`b++` con `b` boolean) van sobre
su operador.

### Postfijos

| Error | Se reporta en |
|---|---|
| Operando que no es `int` | el `++` o `--` |
| Operando que no es un destino asignable (`5++`, `a++--`, `(a)++`) | el `++` o `--` |

### Instanceof

| Error | Se reporta en |
|---|---|
| Operando primitivo, o tipos sin relación entre sí | el `instanceof` |
| Tipo de la derecha genérico (con argumento o sin él) | el `instanceof` |
| Tipo de la derecha inexistente | el nombre del tipo |

### Visibilidad

| Error | Se reporta en |
|---|---|
| Acceso a un atributo o método `private` fuera de su clase (también con `super.`) | el nombre del miembro |
| `new` con un constructor `private` | el nombre de la clase |
| `super(...)` con un constructor `private` | el `super` |

### Super

| Error | Se reporta en |
|---|---|
| `super.x` o `super.m()` en un contexto estático | el `super` |
| `super(...)` que no es la primera sentencia de un constructor | el `super` |
| Argumentos o constructor de `super(...)` incorrectos | el `super` |
| Uso del objeto actual en los argumentos de `super(...)` (`this`, `super.x`, un atributo o un método de instancia sin receptor) | el token de ese uso |

### Todos los caminos llevan al retorno sin código muerto

| Error | Se reporta en |
|---|---|
| Método no void con un camino que no termina en `return` | el nombre del método |
| Sentencia inalcanzable | el primer token de la sentencia (`return`, `if`, `{`, el tipo de una local, o el primer nombre de una expresión: `x = 2;` da `x`) |

La falta de retorno se controla aparte de los tipos: un método con errores
de tipo puede reportar también que le falta el `return`.

### Atributos inicializados

| Error | Se reporta en |
|---|---|
| Inicializador que no conforma con el tipo del atributo | el `=` del atributo |
| Uso de un atributo declarado más abajo, o del mismo atributo (`int a = b; int b;`) | el nombre usado |


### Variables locales clásicas

| Error | Se reporta en                                    |
|---|--------------------------------------------------|
| Tipo inexistente, o genérico sin argumento | el token del tipo                                |
| Inicializador que no conforma con el tipo declarado | el `=` de ese nombre                             |
| Nombre repetido (con otra local visible, un parámetro o un nombre anterior de la misma declaración) | el nombre repetido (el segundo/el que se repite) |

Se reporta el primer error de la declaración, pero los demás nombres válidos
quedan declarados, para no generar errores en cascada en las sentencias que
siguen.

#
