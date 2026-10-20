# Semántica de MiniJava

Vocabulario del análisis semántico del compilador de MiniJava: cómo se
llaman las entidades de un programa y las reglas que las relacionan. Ante
una duda, la fuente es el enunciado de la cátedra
(`enunciados/MiniJava-Semantica-Completa-2026.pdf`).

## Lenguaje

### Fases del análisis

**Tabla de símbolos**:
Registro global de los tipos declarados del programa y de las entidades
predefinidas, con sus miembros.

**Chequeo de declaraciones**:
La fase de Etapa 3. Valida todo lo que está fuera de los bloques y deja la
tabla de símbolos lista. Se compone de la construcción de la tabla, el
control de declaraciones y la consolidación.
_Evitar_: chequeo (a secas)

**Construcción de la tabla**:
Registro de los tipos declarados y sus miembros mientras se hace el análisis
sintáctico. Acá se detectan los nombres repetidos.
_Evitar_: Pasada 1, primera pasada

**Control de declaraciones**:
Verificación de que cada declaración sea válida tal como se escribió: tipos
existentes, uso de genéricos, relaciones entre tipos declarados,
circularidad, redefiniciones y contratos de interfaces.
_Evitar_: Pasada 2, segunda pasada, chequeo de corrección

**Consolidación**:
Incorporación de los miembros heredados a cada tipo declarado. Respeta las
redefiniciones válidas y conserva o instancia los parámetros de tipo según
la relación de herencia.

**Chequeo de sentencias**:
La fase de Etapa 4. Valida los cuerpos de métodos y constructores, cada uno
en su clase de origen: variables locales, resolución de nombres, tipos de
expresiones y contexto estático.
_Evitar_: chequeo de cuerpos

### Tipos declarados

**Tipo declarado**:
Una clase o una interfaz, ya sea declarada en el programa o predefinida.
Clases e interfaces comparten un único espacio de nombres.
_Evitar_: clase (para referirse a las dos), entidad declarada

**Clase**:
Tipo declarado que puede tener atributos, métodos con cuerpo y constructores,
y extender una clase o implementar interfaces. Nunca designa a una interfaz.

**Interfaz**:
Tipo declarado que sólo declara métodos sin cuerpo y puede extender otras
interfaces.
_Evitar_: clase

**Tipo predefinido**:
Tipo declarado que existe antes de analizar el programa: `Object`, `String`
y `System`. No puede redeclararse: la declaración del programa se descarta y
el tipo predefinido se conserva. Toda clase sin `extends` extiende `Object`.
`String` y `System` son clases finales, como en Java.
_Evitar_: clase de librería

### Herencia

**Supertipo directo**:
Tipo declarado que aparece en el `extends` o en el `implements` de otro.

**Herencia múltiple de interfaces**:
Una interfaz que extiende varias interfaces. Una clase implementa como
máximo una interfaz y extiende como máximo una clase, y nunca hace las dos
cosas a la vez.
_Evitar_: herencia múltiple (a secas), implementación múltiple

**Tipo sellado**:
Tipo declarado (`sealed`) que restringe sus subtipos directos a los que
nombra como permitidos.
_Evitar_: tipo sealed

**Subtipo permitido**:
Tipo declarado que figura en la lista de permitidos de un tipo sellado y lo
tiene como supertipo directo. Tiene que ser final, sellado o no sellado.
_Evitar_: permitido (a secas), hijo permitido

**Tipo no sellado**:
Tipo declarado (`nonsealed`) que tiene al menos un supertipo directo
sellado y vuelve a dejar abierta su propia herencia.
_Evitar_: nonsealed (como sustantivo)

**Clase final**:
Clase que ninguna otra puede extender.

**Método final**:
Método que ninguna clase descendiente puede redefinir.

### Genericidad

**Parámetro de tipo**:
Variable de tipo que declara un tipo declarado genérico (como la `T` de
`Caja<T>`). Hay como máximo uno por tipo declarado, y no tiene cotas, ni
siquiera `Object`. Se identifica por su nombre y por el tipo declarado que
lo declara: la `T` de `Base<T>` y la `T` de `D<T>` son parámetros distintos.
_Evitar_: genérico (a secas), tipo genérico

**Argumento de tipo**:
El tipo que se le da a un parámetro de tipo en un uso concreto, como el
`String` de `Caja<String>`.

**Herencia sin argumento**:
Extender o implementar un tipo declarado genérico sin darle argumento de
tipo. Los miembros heredados conservan el parámetro de tipo original, sin
reemplazarlo por un tipo concreto, ni siquiera por `Object`. Como ese
parámetro no se puede nombrar en el tipo que hereda, un método que lo
menciona no se puede redefinir allí, y un contrato que lo menciona no se
puede cumplir.

**Parámetro de tipo de método**:
Parámetro de tipo que declara un método genérico (como la `T` de
`<T> T m(T x)`). Es distinto del parámetro de tipo del tipo declarado y
puede ocultarlo. Al comparar signaturas se identifica por su posición, no
por su nombre.
_Evitar_: parámetro de tipo propio, genérico del método

**Tipo raw**:
Uso de un tipo declarado genérico sin argumento de tipo, fuera de una
herencia (por ejemplo, `Caja dato;`). Se admite en los tipos de atributos,
parámetros y retornos, y sus miembros conservan el parámetro de tipo
original. No se admite al crear objetos (`new Caja()`). Es una desviación
deliberada del enunciado: ver `adr/0001-tipos-raw-en-declaraciones.md`.
_Evitar_: tipo crudo, tipo sin instanciar

### Miembros

**Aridad**:
Cantidad de parámetros formales de un método o de un constructor.

**Clave**:
Lo que identifica a un método dentro de un tipo declarado: el par (nombre,
aridad), escrito `nombre/aridad`. En un constructor, la aridad. Dos métodos
del mismo tipo declarado con la misma clave son siempre una declaración
repetida, aunque difieran los tipos de sus parámetros o su tipo de retorno.
Se detecta en la construcción de la tabla.
_Evitar_: firma

**Signatura**:
La cantidad de parámetros, sus tipos en orden y el tipo de retorno. Los
nombres de los parámetros no forman parte de ella. Dos signaturas son
exactamente iguales cuando coinciden las tres cosas, comparadas después de
aplicar la instanciación genérica que corresponda. Sólo importa en una
redefinición y al cumplir un contrato de interfaz.
_Evitar_: firma

**Sobrecarga**:
Dos métodos del mismo tipo declarado con el mismo nombre y distinta aridad.

**Redefinición**:
Método de instancia con la misma clave que un método de instancia heredado.
Su signatura tiene que ser exactamente igual a la del heredado. Los métodos
estáticos no se redefinen: la clave de un estático heredado no puede volver
a declararse en ningún descendiente.
_Evitar_: override (en el texto en español)

**Constructor por defecto**:
Constructor sin parámetros y con cuerpo vacío que se agrega a una clase que
no declara ninguno.

**Contrato de interfaz**:
Obligación de una clase que implementa una interfaz: tener, propio o
heredado, un método de instancia compatible por cada método de la interfaz.
Compatible quiere decir con la misma clave y exactamente la misma
signatura. Un método estático nunca cumple un contrato.

**Unidad**:
Un método o un constructor: lo que tiene un cuerpo con sentencias. Un
inicializador de atributo no es una unidad.
_Evitar_: unidad (para referirse a un atributo inicializado)

**Contexto de chequeo**:
Lo que se está chequeando en un momento del chequeo de sentencias: una
unidad o el inicializador de un atributo, junto con la clase que lo
declara.
_Evitar_: unidad actual (para incluir atributos)

### Nombres

**Nombre invalidado**:
Nombre de tipo declarado que apareció en más de una declaración del
programa. Ninguna de sus declaraciones entra en la tabla de símbolos, sin
importar cuántas veces se repita. El nombre de un tipo predefinido nunca se
invalida.
_Evitar_: nombre descartado, nombre duplicado
