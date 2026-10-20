# Los tipos predefinidos siguen a Java donde el enunciado no dice nada

El §7 del enunciado fija qué métodos declaran `Object`, `String` y `System`,
pero no dice si se pueden extender ni si tienen constructor. Se decidió
completar esos huecos como lo hace Java:

- `String` y `System` son clases finales. `class A extends String {}` da
  `ERR_HERENCIA_CLASE_FINAL` sobre `A`. `Object` no es final.
- `Object` y `String` tienen constructor por defecto, así que
  `new Object()` y `new String()` son válidos. `System` no tiene ningún
  constructor (en Java es privado), y `new System()` es un error en Etapa 4.

## Opciones consideradas

- **Lectura literal del enunciado**: si no prohíbe extenderlas, se pueden
  extender, y por la regla general del §6.3 las tres tienen constructor por
  defecto. Se descartó porque el compilador tiene que comportarse como
  `javac` ante un programa válido en Java.

## Consecuencias

- Si la cátedra exige la lectura literal, alcanza con no marcar `String` y
  `System` como finales y darle a `System` el constructor por defecto, los
  dos en `TablaSimbolos.registrarPredefinidas()`.
- Redeclarar una predefinida es un caso aparte de la regla general de
  nombres repetidos: se reporta `ERR_CLASE_DUPLICADA` sobre la declaración
  del programa, que se descarta, y la predefinida se conserva. Si se
  descartaran las dos, `String` desaparecería y cada `printS(String)`
  produciría errores en cascada.
