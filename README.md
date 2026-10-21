# MiniJava — Analizador Léxico (Etapa 1)

Compiladores e Intérpretes — Departamento de Cs. e Ingeniería de la Computación, Universidad Nacional del Sur. Proyecto: implementación del analizador léxico de MiniJava.

Con esta entrega se busca cubrir la totalidad de los logros correspondientes a esta etapa del proyecto (análisis léxico): reconocimiento completo de los lexemas de MiniJava (palabras clave, identificadores, literales, símbolos de puntuación, operadores y fin de archivo), manejo de errores léxicos con recuperación (sin cortar la ejecución en el primer error), y una interfaz de línea de comandos que cumple el formato de salida pedido. El detalle de qué se cubre y cómo está documentado en el informe (ver más abajo).

## Cómo usar el programa

### Compilar

Desde `Código/`:

```bash
./build.sh
```

Esto compila el proyecto y arma `Código/out/compilador.jar`.

### Ejecutar

```bash
java -jar Código/out/compilador.jar <archivo-fuente>
```

Corre la etapa 3 (léxico + sintáctico + semántico). `<archivo-fuente>` puede tener cualquier extensión. Si no hay errores se imprime `[SinErrores]`; si los hay, cada uno se imprime con una línea legible seguida de la etiqueta `[Error:<lexema>|<línea>]`. La etiqueta es la misma para errores léxicos, sintácticos y semánticos; en un error semántico, el lexema es el nombre de la entidad que lo provoca y la línea es la de ese mismo token. Los códigos internos `ERR_*` sólo eligen la descripción y nunca se imprimen (ver `Documentación/etapa3-semantico/casos-de-prueba.md`).

Ejemplo:

```bash
$ java -jar Código/out/compilador.jar Código/resources/semantico/conErrores/semError15.java
Error Semántico en línea 5: el tipo "Foo" no está declarado.
[Error:Foo|5]
```

### Correr los tests

```bash
cd Código
javac -cp "out/classes:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" -d out/test-classes $(find src/test -name "*.java")
java -cp "out/classes:out/test-classes:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore test.java.TesterDeCasosSinErrores test.java.TesterDeCasosConErrores
```

## Dónde está el resto de la documentación

Este README es solo una guía rápida de uso. **Todo el detalle técnico está en el informe**, en:

```
Documentación/etapa1-lexico/informe-analizador-lexico.md
```

Ahí se encuentra, entre otras cosas:

- La tabla completa de requisitos (REQ-MP-\*, REQ-AL-\*, REQ-AH-\*).
- La sección **Tokens**, con la expresión regular y las notas de cada token reconocido (palabras clave, identificadores, literales numéricos, char/string, puntuación, operadores, EOF).
- La arquitectura del programa (patrón en capas, interfaces, clases) y el diagrama del autómata finito (método por estado).
- La sección **Casos de prueba**, con los casos límite relevados durante el desarrollo (probados contra la implementación real, no derivados a mano), incluyendo aclaraciones sobre qué es alcance de esta etapa (léxica) y qué queda para la etapa sintáctica.

Documentos adicionales, también en `Documentación/`:

- `testers/guia-testers-lexico.md`: cómo están armados y cómo agregar casos nuevos a los testers de JUnit (`resources/sinErrores/`, `resources/conErrores/`).
