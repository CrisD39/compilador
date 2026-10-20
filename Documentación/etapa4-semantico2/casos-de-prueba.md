# Casos de prueba — Sem2: chequeo básico de sentencias

Casos de `Código/resources/semantico2/{conErrores,sinErrores}/` para el funcionamiento **básico** de la Etapa 4, derivados de `enunciados/MiniJava-Semantica-Completa-2026.pdf` (§9–§12). Los casos por logro van aparte, en `resources/semantico2/logros/`.

- `semIIError01..05` y `semIICorrecto01..05` son los de referencia de la cátedra (sin modificar).
- Los demás se escribieron sin usar sintaxis de logros (sin for, ternario, lambda, float, instanceof, super, `++`/`--`, locales clásicas `Tipo x = e;`, atributos inicializados).
- Cada caso con error tiene **un** error; el resto del programa es válido. Todos pasan el sintáctico (`ModuloPrincipalET2`) y el chequeo de declaraciones (`ModuloPrincipalET3`), salvo `ERR_TIPO_NO_DECLARADO` para `Object`/`String`/`System`, que todavía no están registradas como predefinidas.

## Convención de lexema en `[Error:<lexema>|<línea>]`

| Error | Lexema |
|---|---|
| Variable/atributo no encontrado, `e.x` inexistente | el nombre |
| Asignación incompatible o destino no asignable | `=` |
| Método inexistente, aridad sin match, argumento no conforma, llamada estática inválida | nombre del método |
| `new` de interfaz / clase inexistente / genérica sin argumento / sin constructor de esa aridad / argumento no conforma | nombre de la clase |
| Operador con tipos incorrectos u operando void | el operador |
| Condición no boolean | `if` / `while` |
| Return incorrecto (incluye retorno no conformante) | `return` |
| `this` en contexto estático | `this` |
| Índice/tamaño no int, indexar algo que no es arreglo | `[` |
| Miembro inexistente sobre primitivo, `T`, Object o arreglo (`length`, `clone`, …) | el nombre del miembro |
| Uso de `T` en contexto estático | `T` |
| Expresión que no es sentencia válida | `;` |
| Local repetida / choca con parámetro / `var` con null, void o autorreferencia | nombre de la local |
| `var` como cuerpo directo de if/else/while | `var` |

Extiende la de los casos de referencia (no declarada → nombre; asignación → `=`; parámetros → nombre del método; local repetida → nombre). Es una propuesta: si la implementación reporta en otro token, ajustar la tabla y las primeras líneas.

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
