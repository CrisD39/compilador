# Casos de prueba — Sem2: chequeo básico de sentencias

Casos de `Código/resources/semantico2/{conErrores,sinErrores}/` para el funcionamiento **básico** de la Etapa 4, derivados de `enunciados/MiniJava-Semantica-Completa-2026.pdf` (§9–§12). Los casos por logro van aparte, en `resources/semantico2/logros/`.

- `semIIError01..05` y `semIICorrecto01..05` son los de referencia de la cátedra (sin modificar).
- Los demás se escribieron sin usar sintaxis de logros (sin for, ternario, lambda, float, instanceof, super, `++`/`--`, locales clásicas `Tipo x = e;`, atributos inicializados).
- `semIIError105..171` y `semIICorrecto35..94` vienen de un segundo juego de casos (comentarios `CN-SII-*`/`CP-SII-*`) que se agregó después. A 17 de ellos se les reescribió la etiqueta para que sigan la convención de abajo, y a `semIICorrecto56` se le corrigió la llamada `crear()` por `Persona.crear()`. Algunos (p. ej. herencia circular o un método de interfaz sin implementar) prueban errores de la Etapa 3, no del chequeo de sentencias.
- Cada caso con error tiene **un** error; el resto del programa es válido. Todos pasan el sintáctico (`ModuloPrincipalET2`) y el chequeo de declaraciones (`ModuloPrincipalET3`); el error aparece recién en el chequeo de sentencias (`ModuloPrincipalET4`).

## Convención de lexema en `[Error:<lexema>|<línea>]`

Es el contrato de la spec 0003 (D7): todos los casos con error lo cumplen. El código `ERR_*` sólo elige la descripción (`ErrorSemantico.getDescripcion()`) y nunca se imprime en la etiqueta.

| Error | Lexema | Código `ERR_*` |
|---|---|---|
| Variable no declarada (incluye `var x = x;`) | el nombre | `ERR_VAR_NO_DECLARADA` |
| Atributo implícito desde un método estático | el nombre | `ERR_ATRIBUTO_EN_ESTATICO` |
| `e.x` inexistente en el tipo estático de `e` (incluye `length` sobre `Object`) | el nombre del atributo | `ERR_ATRIBUTO_NO_ENCONTRADO` |
| Asignación incompatible | `=` | `ERR_ASIGNACION_INCOMPATIBLE` |
| Destino no asignable | `=` (o `++`/`--`) | `ERR_DESTINO_NO_ASIGNABLE` |
| Método inexistente o aridad sin match (incluye `clone` sobre un arreglo) | nombre del método | `ERR_METODO_NO_ENCONTRADO` |
| Argumento que no conforma (método o constructor) | nombre del método o de la clase | `ERR_ARGUMENTO_INCOMPATIBLE` |
| Llamada sin receptor o `Clase.m()` a un método de instancia | nombre del método | `ERR_METODO_NO_ESTATICO` |
| `Clase.m()` o `new` de una clase inexistente | nombre de la clase | `ERR_TIPO_NO_DECLARADO` (de la Etapa 3) |
| `new` de interfaz | nombre de la interfaz | `ERR_NEW_INTERFAZ` |
| `new` de una genérica sin argumento | nombre de la clase | `ERR_TIPO_SIN_ARGUMENTO` (de la Etapa 3) |
| `new` sin constructor de esa aridad | nombre de la clase | `ERR_CONSTRUCTOR_NO_ENCONTRADO` |
| Miembro sobre un primitivo o un `T` sin instanciar | el nombre del miembro | `ERR_RECEPTOR_SIN_MIEMBROS` |
| Operador con tipos incorrectos u operando void (incluye `++`/`--` sobre algo que no es `int`) | el operador | `ERR_OPERADOR_INCOMPATIBLE` |
| Condición no boolean | `if` / `while` / `for` | `ERR_CONDICION_NO_BOOLEAN` |
| Return incorrecto (incluye retorno no conformante) | `return` | `ERR_RETORNO_INCORRECTO` |
| `this` en contexto estático | `this` | `ERR_THIS_EN_ESTATICO` |
| Uso de `T` en contexto estático | `T` | `ERR_PARAMETRO_TIPO_EN_ESTATICO` |
| Índice o tamaño no int | `[` | `ERR_INDICE_NO_ENTERO` |
| Indexar algo que no es arreglo | `[` | `ERR_NO_ES_ARREGLO` |
| Expresión que no es sentencia válida | `;` | `ERR_SENTENCIA_INVALIDA` |
| Local repetida, choca con un parámetro u oculta una local visible | nombre de la local | `ERR_LOCAL_DUPLICADA` |
| `var` con `null` o `void` | nombre de la local | `ERR_VAR_SIN_TIPO` |
| `var` como cuerpo directo de if/else/while/for | `var` | `ERR_DECLARACION_COMO_CUERPO` |
| Inicialización del for que no es una asignación ni un `var` (logro For) | `for` | `ERR_FOR_INICIALIZACION` |
| Actualización del for que no es una asignación (logro For) | `for` | `ERR_FOR_ACTUALIZACION` |
| Expresión del for-each que no conforma con `MiniIterable` (arreglos incluidos; logro For) | `for` | `ERR_FOREACH_NO_ITERABLE` |
| Elemento del for-each que no conforma con el tipo de la variable (logro For) | `for` | `ERR_FOREACH_TIPO_INCOMPATIBLE` |
| Construcción de un logro no implementado | ver la spec 0003, D11 | `ERR_NO_SOPORTADO` |

Una local cuyo `var` falló (inicializador con error, `null` o `void`) queda declarada sin tipo: usarla corta la sentencia sin reportar nada más, así un solo error no se repite en cada uso (`semIIMultiErrorError03`).

Extiende la de los casos de referencia (no declarada → nombre; asignación → `=`; parámetros → nombre del método; local repetida → nombre). Un caso nuevo tiene que seguir esta tabla; si una regla nueva necesita otro token, se agrega una fila.

## Pendiente de confirmar

§12.5 del documento base no exige retorno en todos los caminos ni rechaza código inalcanzable (`semIICorrecto` de §12.5). Si se implementa el logro *Todos los caminos llevan al retorno*, ese caso deja de ser válido.

## Índice

### Con errores

| Caso | § | Lexema | Qué prueba |
|---|---|---|---|
| `semIIError06` | 10.1 | `x` | Una local solo es visible despues de su declaracion: x se usa una linea antes de declararse. |
| `semIIError07` | 10.1 | `i` | La local declarada en un bloque deja de ser visible al cerrarlo: el bloque hermano no la ve. |
| `semIIError08` | 10.1 | `a1` | En un metodo estatico no se permite el acceso implicito a un atributo. |
| `semIIError09` | 10.1 | `b2` | e.x busca x en el tipo ESTATICO de e (A1), no en la clase dinamica: el atributo existe solo en B2. |
| `semIIError10` | 10.1 | `campo` | Un primitivo no permite seleccionar atributos por punto. |
| `semIIError11` | 10.1 | `dato` | Un parametro de tipo no instanciado (T) no permite seleccionar atributos por punto. |
| `semIIError12` | 10.1 | `inexistente` | Acceso a atributo inexistente en una clase existente. |
| `semIIError13` | 10.2 | `m9` | Llamada a un metodo que no existe en la clase actual ni en sus ancestros. |
| `semIIError14` | 10.2 | `m2` | La resolucion es por (nombre, aridad): existe m2/1 pero no m2/2. |
| `semIIError15` | 10.2 | `m2` | El argumento no conforma con el formal: se pasa un A1 donde se espera B2 (subtipo invertido). |
| `semIIError16` | 10.2 | `m2` | Con la aridad elegida no se busca otra variante por tipos: m2/1 recibe int y se pasa boolean, aunque no haya otro m2/1. |
| `semIIError17` | 10.2 | `m2` | En un metodo estatico, una llamada sin receptor solo puede seleccionar un metodo estatico. |
| `semIIError18` | 10.2 | `m2` | Clase.m() exige que m sea estatico: un metodo de instancia no se invoca con el nombre de la clase. |
| `semIIError19` | 10.2 | `Inexistente` | Clase.m() con un nombre de clase que no existe. |
| `semIIError20` | 10.2 | `toString` | Un receptor de tipo T no instanciado no garantiza miembros de Object: contenido.toString() es incorrecto. |
| `semIIError21` | 10.2 | `m2` | Un primitivo no tiene metodos. |
| `semIIError22` | 10.2 | `m3` | Llamada sobre un receptor de interfaz a un metodo que la interfaz no declara. |
| `semIIError23` | 11.7 | `I1` | No se instancian interfaces. |
| `semIIError24` | 5.2 | `Caja` | Sin tipos raw: crear una entidad generica sin argumento es error. |
| `semIIError25` | 10.3 | `A1` | Se selecciona el constructor por aridad: A1 solo tiene constructor de 1 parametro. |
| `semIIError26` | 10.3 | `B2` | Si la clase declara un constructor no se agrega el por defecto; tampoco se buscan constructores en ancestros. |
| `semIIError27` | 11.7 | `A1` | El argumento del constructor no conforma con su parametro formal. |
| `semIIError28` | 11.7 | `Caja` | El argumento del constructor se chequea con la sustitucion generica: en Caja<String>, T es String. |
| `semIIError29` | 11.7 | `Inexistente` | new de una clase que no existe. |
| `semIIError30` | 10.4 | `this` | this no es valido en un metodo estatico. |
| `semIIError31` | 10.4 | `this` | La restriccion de contexto estatico tambien aplica dentro de parentesis y subexpresiones. |
| `semIIError32` | 10.4 | `T` | El parametro de tipo de la clase no puede usarse en el cuerpo de un metodo estatico. |
| `semIIError33` | 9.1 | `=` | Sin conversion implicita char -> int. |
| `semIIError34` | 9.1 | `=` | Sin conversion implicita int -> char. |
| `semIIError35` | 9.1 | `=` | boolean no conforma con int. |
| `semIIError36` | 9.1 | `=` | Sin conversion entre referencias y primitivos: String no conforma con int. |
| `semIIError37` | 9.2 | `=` | Un supertipo no conforma con el subtipo (la direccion inversa no se permite). |
| `semIIError38` | 9.2 | `=` | Una interfaz no conforma con una clase que la implementa. |
| `semIIError39` | 9.2 | `=` | Clases no relacionadas por herencia no conforman entre si. |
| `semIIError40` | 9.3 | `=` | Argumentos genericos invariantes: Caja<String> no conforma con Caja<Object>. |
| `semIIError41` | 9.3 | `return` | Un T no instanciado no conforma con Object (sin cota implicita). |
| `semIIError42` | 9.3 | `=` | Object no conforma con un T no instanciado. |
| `semIIError43` | 9.3 | `=` | Dentro de un cuerpo chequeado parametricamente, String no conforma con T. |
| `semIIError44` | 9.4 | `=` | Arreglos invariantes: String[] no conforma con Object[]. |
| `semIIError45` | 9.4 | `=` | La cantidad de dimensiones forma parte del tipo: int[][] no conforma con int[]. |
| `semIIError46` | 9.4 | `=` | Una fila de String[][] tiene tipo String[], que no conforma con Object[]. |
| `semIIError47` | 9.5 | `=` | null no conforma con primitivos. |
| `semIIError48` | 9.5 | `=` | Una llamada void no produce valor: no puede ser lado derecho de una asignacion. |
| `semIIError49` | 9.5 | `m3` | Una llamada void no puede ser argumento actual. |
| `semIIError50` | 11.2 | `+` | + binario exige int, int. |
| `semIIError51` | 11.2 | `+` | No hay concatenacion de cadenas con +. |
| `semIIError52` | 11.2 | `+` | No hay promocion numerica: char + int es error. |
| `semIIError53` | 11.2 | `-` | - unario exige int. |
| `semIIError54` | 11.2 | `!` | ! exige boolean. |
| `semIIError55` | 11.2 | `%` | % exige int, int. |
| `semIIError56` | 11.2 | `<` | Los relacionales exigen int, int: dos char no se comparan con <. |
| `semIIError57` | 11.2 | `&&` | && exige boolean, boolean. |
| `semIIError58` | 11.2 | `\|\|` | || exige boolean, boolean. |
| `semIIError59` | 11.2 | `==` | == exige que un operando conforme con el otro: char e int no se comparan. |
| `semIIError60` | 11.2 | `==` | == entre clases no relacionadas: ninguna conforma con la otra. |
| `semIIError61` | 11.2 | `!=` | != entre boolean e int. |
| `semIIError62` | 11.2 | `+` | Un resultado void no se usa como operando. |
| `semIIError63` | 11.4 | `=` | Una expresion parentizada no es destino asignable: (x) = 3. |
| `semIIError64` | 11.9 | `=` | Un literal no es asignable. |
| `semIIError65` | 11.9 | `=` | this por si solo no es asignable. |
| `semIIError66` | 11.9 | `=` | Una llamada por si sola no es asignable. |
| `semIIError67` | 11.8 | `=` | length es de solo lectura. |
| `semIIError68` | 11.9 | `=` | Crear un arreglo no designa un destino asignable. |
| `semIIError69` | 11.8 | `[` | El indice debe ser int. |
| `semIIError70` | 11.8 | `[` | El tamano en la creacion debe ser int (no hay conversion char -> int). |
| `semIIError71` | 11.8 | `[` | Solo se indexa una expresion de tipo arreglo. |
| `semIIError72` | 11.8 | `[` | Un arreglo con tipo estatico Object no permite indexacion. |
| `semIIError73` | 11.8 | `length` | Con tipo estatico Object tampoco hay length. |
| `semIIError74` | 11.8 | `clone` | Los arreglos no tienen clone() ni otros miembros salvo length y toString(). |
| `semIIError75` | 11.8 | `[` | Cada indexacion quita una dimension: v[0] es int y no admite otra indexacion. |
| `semIIError76` | 12.2 | `;` | Una expresion binaria no es una sentencia. |
| `semIIError77` | 12.2 | `;` | Si hay encadenado, el ultimo debe ser una llamada: crear().a1 termina en un atributo. |
| `semIIError78` | 12.2 | `;` | Un acceso a componente no es sentencia. |
| `semIIError79` | 12.2 | `;` | Crear un arreglo no es llamar a un constructor: no es sentencia. |
| `semIIError80` | 12.2 | `;` | Una asignacion parentizada no es sentencia de asignacion. |
| `semIIError81` | 12.2 | `;` | Una llamada parentizada no es sentencia de llamada. |
| `semIIError82` | 12.2 | `;` | Una sentencia de llamada no puede basarse en un primario parentizado. |
| `semIIError83` | 12.2 | `;` | Un acceso a variable suelto no es sentencia. |
| `semIIError84` | 12.3 | `p1` | Una local no puede llamarse igual que un parametro de la unidad. |
| `semIIError85` | 12.3 | `v` | Una local no puede repetirse en el mismo bloque. |
| `semIIError86` | 12.3 | `v` | Una local no puede ocultar a una local todavia visible de un contenedor. |
| `semIIError87` | 12.3 | `p1` | Un parametro no puede ser ocultado por una local de un bloque anidado. |
| `semIIError88` | 12.3 | `v` | var con null no permite inferir el tipo. |
| `semIIError89` | 12.3 | `v` | Una llamada void no permite inferir un tipo para var. |
| `semIIError90` | 12.3 | `x` | El inicializador se chequea antes de incorporar la local: var x = x no se ve a si misma. |
| `semIIError91` | 12.3 | `var` | Una declaracion local no puede ser cuerpo directo de if. |
| `semIIError92` | 12.3 | `var` | Una declaracion local no puede ser cuerpo directo de else. |
| `semIIError93` | 12.3 | `var` | Una declaracion local no puede ser cuerpo directo de while. |
| `semIIError94` | 12.4 | `if` | La condicion del if debe ser boolean: un int no se convierte. |
| `semIIError95` | 12.4 | `while` | La condicion del while debe ser boolean: una referencia no se convierte. |
| `semIIError96` | 12.4 | `if` | Una asignacion como condicion vale solo si su resultado es boolean; aca es int. |
| `semIIError97` | 12.4 | `=` | Se chequean todas las ramas aunque no se ejecuten: error dentro del else. |
| `semIIError98` | 12.5 | `return` | En un metodo void solo se permite return; sin expresion. |
| `semIIError99` | 12.5 | `return` | En un metodo con retorno no void, el return debe llevar expresion. |
| `semIIError100` | 12.5 | `return` | La expresion del return debe conformar con el tipo de retorno declarado. |
| `semIIError101` | 12.5 | `return` | El retorno de subtipo invertido: se declara B2 y se devuelve A1. |
| `semIIError102` | 12.5 | `return` | En un constructor no se permite return e;. |
| `semIIError103` | 12.5 | `return` | Un return con expresion void es incorrecto incluso en un metodo void. |
| `semIIError104` | 12.5 | `z` | El codigo inalcanzable igual se chequea: variable no declarada despues de un return. |
| `semIIError105` | 12.3 | `x` | var no se infiere el tipo con null |
| `semIIError106` | 12.3 | `x` | var no se infiere el tipo con una llamada void |
| `semIIError107` | 12.3 | `dato` | var no puede coincidir con un parametro de la unidad |
| `semIIError108` | 12.3 | `x` | var no puede coincidir con una local visible de un contenedor |
| `semIIError109` | 12.3 | `var` | declaracion local prohibida como cuerpo directo de if |
| `semIIError110` | 12.3 | `var` | declaracion local prohibida como cuerpo directo de while |
| `semIIError111` | 10.4 | `this` | this prohibido en contexto estatico |
| `semIIError112` | 10.4 | `this` | this prohibido dentro de una expresion parentizada en contexto estatico |
| `semIIError113` | — | `edad` | acceso implicito a un atributo prohibido en contexto estatico (S10.1/S10.4) |
| `semIIError114` | 10.4 | `m1` | invocacion de metodo no estatico prohibido en contexto estatico |
| `semIIError115` | 12.1 | `=` | la parte izquierda de una asignacion no debe ser un primitivo ni una expresion de operacion |
| `semIIError116` | 12.1 | `=` | asginacion a la izquierda de una variable no declarada en el alcance |
| `semIIError117` | 12.1 | `=` | this no se puede modificar (es una referencia, no es ligable) |
| `semIIError118` | 12.1 | `=` | no se puede asignar a la longitud de un arreglo |
| `semIIError119` | 12.1 | `=` | no se puede asignar un int a un boolean |
| `semIIError120` | 12.1 | `=` | no se puede asignar un String a un int |
| `semIIError121` | 12.1 | `=` | invariancia de arreglos: no se asigna un arreglo de un tipo a un arreglo de otro |
| `semIIError122` | 12.1 | `=` | invariancia de genericos: no se asigna una instancia de un tipo generico a otra instanciacion |
| `semIIError123` | 13.1 | `return` | un tipo base no conforma con un tipo derivado (la herencia es hacia arriba) |
| `semIIError124` | 12.1 | `=` | no se asigna un Object a una variable de un tipo derivado sin cast |
| `semIIError125` | 12.1 | `==` | igualdad prohibida entre tipos primitivos de distinta naturaleza |
| `semIIError126` | 12.1 | `+` | caso no previsto de + (concatenacion de literales) |
| `semIIError127` | 12.1 | `!` | ! solo se aplica a boolean |
| `semIIError128` | 12.1 | `<` | operadores relacionales solo sobre int |
| `semIIError129` | 12.1 | `&&` | operadores logicos solo sobre boolean |
| `semIIError130` | 12.1 | `if` | la condicion de if debe ser boolean |
| `semIIError131` | 12.1 | `while` | la condicion de while debe ser boolean |
| `semIIError132` | 12.1 | `[` | el indice de un arreglo debe ser int |
| `semIIError133` | 12.1 | `[` | el tamano de un arreglo nuevo debe ser int |
| `semIIError134` | 12.1 | `[` | no se puede indexar una variable que no es arreglo |
| `semIIError135` | 12.1 | `[` | no se puede indexar una variable de tipo Object |
| `semIIError136` | 12.1 | `length` | length solo se aplica a arreglos |
| `semIIError137` | 12.1 | `return` | solo las expresiones con valor pueden acompanar a return |
| `semIIError138` | 12.2 | `return` | prohibido return acompanado de expresion en un constructor |
| `semIIError139` | 12.1 | `return` | el valor de return debe conformar con el tipo declarado del metodo |
| `semIIError140` | 12.1 | `return` | prohibido que return devuelva el resultado de una llamada a un metodo void |
| `semIIError141` | 11.1 | `hacer` | invocacion a un metodo no declarado en la clase (sin receptor implicito) |
| `semIIError142` | 11.1 | `hacer` | invocacion a un metodo no declarado en la clase del receptor |
| `semIIError143` | 11.1 | `nombre` | acceso a un atributo no declarado en la clase del receptor |
| `semIIError144` | 11.1 | `obtenerEdad` | los metodos se invocan solo sobre instancias, no sobre la clase |
| `semIIError145` | 11.1 | `sumar` | argumento que no conforma con el tipo del parametro del metodo |
| `semIIError146` | 11.1 | `imprimir` | argumento que es el resultado de una llamada void |
| `semIIError147` | 14.1 | `Figura` | prohibido instanciar una interfaz |
| `semIIError148` | 9 | `;` | prohibida una sentencia formada por una expresion de operacion |
| `semIIError149` | 9 | `;` | prohibida una sentencia formada por el acceso a un atributo |
| `semIIError150` | 9 | `;` | prohibida una sentencia formada por el acceso a un indice |
| `semIIError151` | 9 | `;` | prohibida una sentencia formada por la creacion de un arreglo nuevo |
| `semIIError152` | 9 | `;` | prohibida una sentencia formada por una expresion parentizada con asignacion |
| `semIIError153` | 9 | `;` | prohibida una sentencia formada por una expresion parentizada con llamada a metodo |
| `semIIError154` | 9 | `;` | prohibida una sentencia formada por una expresion parentizada con receptor this |
| `semIIError155` | 12.1 | `=` | la escritura en un elemento de un arreglo usa un valor que no conforma con el tipo del arreglo |
| `semIIError156` | 12.1 | `==` | igualdad prohibida entre tipos primitivos de distinta naturaleza |
| `semIIError157` | 10.1 | `Base` | herencia circular A extends B, B extends A |
| `semIIError158` | 10.5 | `toString` | interfaz redefiniendo metodo de Object con tipo de retorno distinto |
| `semIIError159` | 10.2 | `Figura` | una clase no puede heredar de una interfaz |
| `semIIError160` | 10.2 | `Base` | una interfaz no puede heredar de una clase |
| `semIIError161` | 10.5 | `hablar` | clase que no implementa un metodo declarado por la interfaz |
| `semIIError162` | 10.5 | `hablar` | implementacion de metodo de interfaz con tipo de retorno incompatible |
| `semIIError163` | 10.4 | `m` | descendiente no puede redefinir un metodo estatico del ancestro |
| `semIIError164` | 11.4 | `=` | no se usa un primario parentizado como base de una asignacion |
| `semIIError165` | 12.3 | `var` | var prohibido como cuerpo directo de else |
| `semIIError166` | 12.4 | `return` | return sin expresion en metodo no void |
| `semIIError167` | 10.3 | `Caja` | instanciacion raw de clase generica prohibida |
| `semIIError168` | 9.5 | `+` | no se puede operar con expresiones de tipo void |
| `semIIError169` | 10.2 | `toString` | no se permiten invocar metodos sobre un receptor de tipo parametrico T |
| `semIIError170` | 10.1 | `dato` | no se permiten acceder atributos sobre un receptor de tipo parametrico T |
| `semIIError171` | 10.2 | `procesar` | argumento incompatible en llamada a metodo estatico calificado |
| `semIIError172` | D11 | `A1` | El diamante también en la creación de un arreglo (`new A1<>[2]`): logro no soportado. |

### Sin errores

| Caso | § | Qué prueba |
|---|---|---|
| `semIICorrecto06` | 10.1 | Una local puede ocultar un atributo; this.nombre selecciona el atributo. |
| `semIICorrecto07` | 10.1 / 12.6 | Bloques hermanos pueden declarar el mismo nombre; los anidados ven parametros y locales del contenedor. |
| `semIICorrecto08` | 10.1 | Atributos heredados se resuelven sin receptor; e.x busca en el tipo estatico de e. |
| `semIICorrecto09` | 9.2 | Subtipo -> supertipo, clase -> interfaz que implementa, y todo tipo clase -> Object. |
| `semIICorrecto10` | 9.2 | Interfaz que extiende a otra: conforma con la interfaz ancestra (transitivo). |
| `semIICorrecto11` | 9.5 | null conforma con clases, interfaces, parametros de tipo y arreglos. |
| `semIICorrecto12` | 5.1 | Vista desde Caja<String>: atributo, parametro y retorno T se ven como String, y el resultado conforma con Object. |
| `semIICorrecto13` | 5.1 | Las sustituciones se componen en la cadena Base<T> -> Intermedia<U> -> Final (U = String). |
| `semIICorrecto14` | 5 / 9.3 | Dentro de la clase generica, T conforma consigo mismo y el cuerpo se chequea parametricamente. |
| `semIICorrecto15` | 9.4 / 11.8 | Un arreglo conforma con Object; length y toString() estan disponibles; matriz[0] es int[]. |
| `semIICorrecto16` | 11.8 | Arreglos con tipo base parametrico y genericos instanciados en un contexto de instancia. |
| `semIICorrecto17` | 11.8 | Arreglo de una interfaz: crea referencias, no instancias de la interfaz; el encadenado continua tras indexar. |
| `semIICorrecto18` | 10.4 | Un metodo estatico accede a miembros de instancia mediante un receptor explicito. |
| `semIICorrecto19` | 10.2 | Un estatico se invoca con Clase.m() o con una expresion receptora; un estatico llama a otro sin receptor. |
| `semIICorrecto20` | 7 | Entidades predefinidas: System y Object.debugPrint, toString() heredado en String. |
| `semIICorrecto21` | 6.2 | Sobrecarga simple por aridad: m2/0, m2/1 y m2/2. |
| `semIICorrecto22` | 6.3 | Constructores por aridad y sin llamada implicita a super: B2() es valido aunque A1 solo tenga A1(int). |
| `semIICorrecto23` | 10.3 | Clases, metodos y constructores se resuelven aunque se declaren despues. |
| `semIICorrecto24` | 12.2 | Sentencias de llamada validas: void, con valor descartado, creacion de objeto, encadenada terminando en llamada. |
| `semIICorrecto25` | 11.5 / 11.9 | Destinos asignables: locales, parametros, atributos encadenados tras llamada y componentes. |
| `semIICorrecto26` | 11.4 / 11.9 | La asignacion produce un valor: x = (y = 3), var z = (x = 3), return x = 3, y parentesis en expresiones. |
| `semIICorrecto27` | 11.2 | Operadores bien tipados: aritmeticos, relacionales, logicos, unarios y igualdad. |
| `semIICorrecto28` | 11.2 | == entre char, entre referencias conformantes en alguna direccion, con null y entre String. |
| `semIICorrecto29` | 12.4 | Condiciones boolean en if/else/while, asignacion boolean como condicion, cuerpos con bloque y sentencia vacia. |
| `semIICorrecto30` | 12.5 | return; en metodo void y en constructor; return e con subtipo del retorno declarado. |
| `semIICorrecto31` | 12.5 | Doc. base: no se exige retorno en todos los caminos ni se rechaza codigo inalcanzable (se chequea igual). Choca con el logro de caminos de retorno. |
| `semIICorrecto32` | 12.3 | var toma exactamente el tipo del inicializador, conservando argumento generico y dimensiones. |
| `semIICorrecto33` | 5 | Herencia sin argumento conserva T: el metodo heredado sigue retornando T (no Object). |
| `semIICorrecto34` | 11.3 | this tiene tipo de la clase actual con su parametro: dentro de Caja<T> es Caja<T>. |
| `semIICorrecto35` | — | literales bien tipados (int, char, boolean, String) y null en contexto de referencia |
| `semIICorrecto36` | — | operadores unarios +, - y ! |
| `semIICorrecto37` | — | operadores aritmeticos + - * / % sobre int |
| `semIICorrecto38` | — | operadores relacionales < <= > >= |
| `semIICorrecto39` | — | operadores logicos && y || con cortocircuito; ambos operandos se chequean |
| `semIICorrecto40` | — | igualdad == y != para primitivos, referencias y null |
| `semIICorrecto41` | — | igualdad entre tipos conformantes en alguna direccion |
| `semIICorrecto42` | — | this en un metodo de instancia |
| `semIICorrecto43` | — | this dentro de un constructor |
| `semIICorrecto44` | — | this en una clase generica tiene tipo Clase<T> |
| `semIICorrecto45` | — | this como expresion y parentizado |
| `semIICorrecto46` | — | los parentesis agrupan valores en contextos de expresion |
| `semIICorrecto47` | — | una asignacion parentizada produce un valor |
| `semIICorrecto48` | — | return de una asignacion |
| `semIICorrecto49` | — | una local puede ocultar un atributo |
| `semIICorrecto50` | — | this.nombre selecciona el atributo con una local homonima visible |
| `semIICorrecto51` | — | acceso implicito a atributos y metodos heredados |
| `semIICorrecto52` | — | una local es visible en los bloques anidados |
| `semIICorrecto53` | — | bloques hermanos pueden declarar el mismo nombre |
| `semIICorrecto54` | — | llamada implicita a metodos de instancia y sobrecarga por aridad |
| `semIICorrecto55` | — | llamada implicita a un metodo estatico desde instancia y desde contexto estatico |
| `semIICorrecto56` | — | llamada a metodo estatico mediante el nombre de la clase |
| `semIICorrecto57` | — | receptor expresion y encadenados llamada -> atributo |
| `semIICorrecto58` | — | una llamada void es valida como sentencia |
| `semIICorrecto59` | — | un metodo estatico puede seleccionarse mediante un receptor expresion |
| `semIICorrecto60` | — | entidades predefinidas System, Object y String |
| `semIICorrecto61` | — | creacion de objetos con argumentos, generica y como sentencia o argumento |
| `semIICorrecto62` | — | arreglos basicos y multidimensionales; indexacion y length |
| `semIICorrecto63` | — | arreglos de referencias, genericos y parametros de tipo |
| `semIICorrecto64` | — | arreglos: conformidad con Object, length y toString |
| `semIICorrecto65` | — | encadenado sobre componentes de arreglos y tamanio con expresion |
| `semIICorrecto66` | — | asignaciones a componentes, atributos y conformidad polimorfica |
| `semIICorrecto67` | — | conformidad con ancestro, interfaz, superinterfaz, Object y null |
| `semIICorrecto68` | — | sustitucion generica desde el receptor (incluye arreglos) y herencia sin argumento |
| `semIICorrecto69` | — | en contexto estatico, acceso a miembros de instancia mediante receptor explicito |
| `semIICorrecto70` | — | un metodo estatico puede usar tipos genericos instanciados |
| `semIICorrecto71` | — | sentencias vacias, bloques, if/else y while; var requiere bloque |
| `semIICorrecto72` | — | variantes de return; no se exige retorno total ni se rechaza codigo inalcanzable |
| `semIICorrecto73` | — | == y != entre referencias del mismo tipo estatico y entre arreglos |
| `semIICorrecto74` | — | conformidad transitiva entre clases (Especifica -> SubRaza -> Raza) |
| `semIICorrecto75` | — | clase que implementa una interfaz generica con argumento conforma con ella |
| `semIICorrecto76` | — | un tipo generico instanciado conforma consigo mismo y new devuelve el tipo completo |
| `semIICorrecto77` | — | filas de arreglos asignables; un subarreglo solo a un destino de tipo igual |
| `semIICorrecto78` | — | null conforma con referencias, arreglos, interfaces y T; no con primitivas |
| `semIICorrecto79` | — | un parametro puede ocultar un atributo |
| `semIICorrecto80` | — | la sobrecarga por aridad atraviesa la herencia |
| `semIICorrecto81` | — | un metodo estatico heredado puede invocarse sin receptor y mediante el nombre de la clase descendiente |
| `semIICorrecto82` | — | sobrecarga de constructores por aridad |
| `semIICorrecto83` | — | no hay llamada implicita a un constructor de la superclase |
| `semIICorrecto84` | — | asignacion anidada con parentesis en el lado derecho |
| `semIICorrecto85` | — | una llamada que produce valor es correcta como sentencia, incluso encadenada |
| `semIICorrecto86` | — | una asignacion booleana puede ser condicion de if y de while |
| `semIICorrecto87` | — | el tipo base de un arreglo puede ser una interfaz |
| `semIICorrecto88` | — | crear arreglos cuyo argumento generico usa el parametro de la clase |
| `semIICorrecto89` | — | un receptor de tipo interfaz selecciona sus metodos declarados y heredados |
| `semIICorrecto90` | 10.4 | metodo estatico seleccionado por nombre de clase y por expresion receptora |
| `semIICorrecto91` | 10.1 | herencia, atributo heredado y redefinicion con mismo tipo de retorno |
| `semIICorrecto92` | 10.5 | variable tipada con interfaz e invocacion de su metodo |
| `semIICorrecto93` | 9.4 | toString heredado de Object y accesible sobre un arreglo |
| `semIICorrecto94` | 9.4 | null como argumento y en comparaciones con referencias y arreglos |
