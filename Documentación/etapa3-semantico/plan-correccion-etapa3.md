# Plan: corrección del chequeo de declaraciones (Etapa 3)

## Objetivo y alcance

Dejar el chequeo de declaraciones conforme a
`enunciados/MiniJava-Semantica-2026-Parte 1.pdf` ("Semántica de MiniJava –
Parte I. Declaraciones"), que es el documento de referencia de esta etapa.
Lo que ese documento no especifica sigue las reglas de Java (su "regla de
oro"). Los logros de Etapa 3 ya hechos (`sealed`/`final`, métodos genéricos,
multi-detección, genericidad avanzada) se mantienen y no tienen que
romperse.

**Fuera de este plan (Etapa 4):** AST, `chequearSentencias()`,
`ModuloPrincipalET4`, testers Sem2, logros de Etapa 4, boceto de `Entorno`
y la jerarquía completa de `Tipo`. Los testers Sem2 siguen fallando, como
hoy; eso es lo esperado hasta empezar la Etapa 4. `ModuloPrincipalET3` sigue
siendo el main del jar.

El diseño de casi todo está en `analizador-semantico.md`, sección
"Decisiones: tipos predefinidos, clave y redefinición", y en los ADR 0004 y
0005. Este plan ordena ese trabajo y suma cuatro huecos que aparecieron al
contrastar el código con la Parte I.

## Huecos nuevos (probados con el jar actual)

| | Programa | Hoy | Parte I |
|---|---|---|---|
| H1 | `interface Ii {}` + `class Bb extends Ii {}` | **el compilador falla**: `ClassCastException` en `Clase.consolidarHerencia` (`Clase.java:351`) | §4: `extends` de una clase nombra una clase |
| H2 | `class Bb implements Aa {}` o `interface Jj extends Aa {}`, con `Aa` clase | `[SinErrores]`: se ignora en silencio | §4: `implements` y el `extends` de una interfaz nombran una interfaz |
| H3 | `class Bb extends Caja<Nada> {}` o `class Cc extends Bb<Bb> {}` | `[SinErrores]`: el argumento de `extends`/`implements` se descarta sin validarse | §3: todo tipo referencia con argumento se valida |
| H4 | `class Aa { Bb() {} }` | `[SinErrores]` | §6.3: un constructor tiene el nombre de su clase |

### Decisiones

Todas confirmadas.

- **D1 (H1, H2):** un código nuevo, `ERR_HERENCIA_TIPO_INCORRECTO`, para
  las tres variantes (clase que extiende una interfaz, clase que implementa
  una clase, interfaz que extiende una clase). El lexema es el del tipo
  nombrado en el `extends`/`implements` y la línea, la de ese token, igual
  que `ERR_TIPO_NO_DECLARADO`. La descripción dice qué se esperaba ("Ii no
  es una clase", "Aa no es una interfaz"). La relación se ignora (la clase
  queda con `Object` como padre efectivo) y sigue el chequeo de lo demás.
- **D2 (H3):** `Clase.herencia`, `Clase.interfaces` e `Interfaz.herencias`
  pasan de `Token` a `Tipo`, y se validan con `Tipo.estaBienDeclarado`
  (`ERR_TIPO_NO_DECLARADO`, `ERR_TIPO_NO_GENERICO`,
  `ERR_TIPO_GENERICO_NO_DECLARADO`). Heredar sin argumento sigue siendo
  válido (§5). Además, es lo que necesita el paso 6 para saber con qué
  argumento se instancia cada ancestro.
- **D3 (H4):** un código nuevo, `ERR_CONSTRUCTOR_NOMBRE_INVALIDO`, con el
  lexema y la línea del nombre del constructor. Se reporta al encontrar el
  constructor y no se registra. Si era el único, la clase recibe el
  constructor por defecto: no se ve en la salida, porque la Etapa 4 no corre
  con errores de declaración.
- **D4 (paso 4):** las descripciones de `ERR_METODO_DUPLICADO` y
  `ERR_CONSTRUCTOR_DUPLICADO` hablan de "la misma cantidad de parámetros",
  no de "firma" ni de "los mismos parámetros".
- **D5 (paso 7), herencia sin argumento:** se lee el §5 al pie de la letra.
  El parámetro de tipo conservado no se borra a `Object` al comparar. En
  `interface Ii<T> { T obtener(); }` + `class Aa implements Ii { Object obtener() {} }`
  el método no cumple el contrato: misma clave, otra signatura, así que es
  `ERR_REDEFINICION_INCOMPATIBLE` sobre `obtener`. Lo mismo vale al
  redefinir un método heredado vía `extends Caja`.
- **D6 (pasos 3 y 7), parámetros de tipo de método:** al comparar dos
  signaturas, los parámetros de tipo de método se emparejan por posición
  (`<U> U id(U x)` y `<V> V id(V x)` son la misma signatura). Si uno es
  genérico y el otro no, difieren. Los parámetros de tipo de clase o
  interfaz se siguen comparando por nombre y origen. Vale para redefinición,
  contratos y conflictos entre interfaces.
- **D7 (paso 2), supertipo inexistente:** se reporta una sola vez, como
  `ERR_TIPO_NO_DECLARADO`, en el control de declaraciones (D2). La
  consolidación saltea sin reportar las relaciones que no resuelven, y se
  retira `ERR_INTERFAZ_NO_DECLARADA`. Ningún caso la documenta.

## Pasos

Cada paso es un cambio que se puede revisar solo (`git diff`) y termina con
**todos** los testers de Etapas 1–3 en verde (comandos en `CLAUDE.md`) más
sus casos nuevos. Si un caso existente cambia de resultado por una regla de
la Parte I, se corrige su etiqueta, no el código, y se reporta. No se hace
commit sin pedido.

0. **Línea de base.** `./build.sh`, compilar y correr los testers de
   Etapas 1–3, y anotar el resultado. Es la referencia para detectar
   regresiones.

1. **Herencia múltiple sólo entre interfaces** (ADR 0005). Volver
   `<HerenciaOpcional>` a una única interfaz tras `implements`, reescribir
   los 10 casos que usan la lista y agregar el caso sintáctico de
   `class X implements A, B`. Va primero porque toca la gramática y muchos
   casos, y deja menos piezas en movimiento para el resto.

2. **Relaciones entre tipos y herencia como `Tipo`** (H1, H2, H3; D1, D2).
   Arreglar el `ClassCastException`, reportar las relaciones con el tipo
   equivocado y validar los argumentos de `extends`/`implements`.

3. **`Tipo`: dimensiones y origen de los parámetros de tipo.** El contador
   de dimensiones en `Tipo` (el parser deja de descartarlas), el origen de
   cada `ID_GEN` resuelto en el control de declaraciones y la igualdad de
   `Tipo` (cabeza, argumento, dimensiones y origen). Todavía no cambia
   ningún resultado; lo usan los pasos 6 y 7.

4. **Clave (nombre, aridad).** `getFirma()` pasa a ser `getClave()` en
   `Metodo`, `Constructor` y `Atributo`, y los duplicados se detectan por
   clave. Es el paso con más chances de cambiar casos existentes (los que
   dependían de la sobrecarga por tipos).

5. **Tipos predefinidos y constructores.**
   - `registrarPredefinidas()` con `Object`, `String` y `System` y sus
     métodos del §7. `String` y `System` son finales (ADR 0004).
   - La redeclaración de una predefinida descarta sólo la del programa.
   - `Object` como padre efectivo de toda clase sin `extends`.
   - Las predefinidas se saltean `estaBienDeclarado` y se consolidan.
   - Constructor por defecto (`Object` y `String` sí, `System` no).
   - Nombre del constructor (H4, D3).

   Desde este paso, `debugPrint`/1 y `toString`/0 llegan a todas las
   clases. Los chequeos del paso 7 dependen de eso.

6. **Sustitución genérica.** `Tipo.sustituir` y el mapa
   `ancestro → argumento` compuesto en la consolidación (usa los `Tipo` de
   herencia del paso 2). Los miembros copiados no se reescriben. Así se
   cumple el §5 de la Parte I: los parámetros heredados se ven instanciados
   o conservados según la relación, pero sin crear copias de los miembros.

7. **Redefinición, estáticos y contratos.**
   - Redefinición con signatura exacta (`ERR_REDEFINICION_INCOMPATIBLE`).
   - Las dos reglas de estáticos (`ERR_REDEFINE_ESTATICO`).
   - Contrato de interfaz con signatura exacta; un estático no cumple el
     contrato.
   - `ERR_METODO_RETORNO_INCOMPATIBLE` compara la signatura completa.
   - Métodos de interfaz contra los métodos de instancia de `Object`.

8. **Cierre de documentación.**
   - Marcar como implementada la sección de decisiones de
     `analizador-semantico.md`.
   - Pasar los casos de `casos-de-prueba.md` §5.6 de pendientes a hechos,
     con sus líneas reales.
   - Documentar `ERR_HERENCIA_TIPO_INCORRECTO` y
     `ERR_CONSTRUCTOR_NOMBRE_INVALIDO`.
   - Corregir la etiqueta vieja `[ErrorSem:<código>|<línea>]` del
     `README.md`.

## Casos de prueba

Los de `casos-de-prueba.md` §5.6 (`semCorrecto23`–`27`,
`semError54`–`64`), repartidos por paso, más uno por cada hueco nuevo:

- `semError65` (H1): clase que extiende una interfaz;
- `semError66` (H2): clase que implementa una clase;
- `semError67` (H2): interfaz que extiende una clase;
- `semError68` (H3): `extends Caja<Nada>`;
- `semError69` (H3): `extends Bb<Bb>` con `Bb` no genérica;
- `semError70` (H4): constructor con otro nombre;
- `semError71` (D5): `class Aa implements Ii` (sin argumento) con
  `Object obtener()` contra `T obtener()`;
- `semCorrecto28` (D6): `<V> V id(V x)` redefine `<U> U id(U x)`.

## Sin cambios, a propósito

- **Tipos raw en declaraciones** (ADR 0001). La Parte I §3 admite `Caja`
  sin argumento, así que la decisión ya coincide con el documento de
  referencia de esta etapa.
- **Parámetros repetidos.** La Parte I §8 los ubica en la primera pasada y
  hoy se detectan en el control de declaraciones. No cambia ningún
  resultado: todos los errores se imprimen y los testers buscan la etiqueta
  en la salida.
- **Pendientes de nombres** de `analizador-semantico.md` ("Desvíos", 1–4):
  quedan para después, como se decidió.
