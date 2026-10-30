# MiniJava — Compilador (Etapas 1 a 4)

Compiladores e Intérpretes — Departamento de Cs. e Ingeniería de la Computación, Universidad Nacional del Sur. Proyecto: compilador de MiniJava, construido por etapas.

- **Etapa 1:** analizador léxico.
- **Etapa 2:** analizador sintáctico LL(1) descendente recursivo, con recuperación en modo pánico.
- **Etapa 3:** análisis semántico de declaraciones (tabla de símbolos).
- **Etapa 4:** análisis semántico de sentencias (chequeo de tipos sobre el AST de cada cuerpo), con los logros *Multi-detección*, *Postfijos*, *For*, *Instanceof*, *Visibilidad chequeada*, *Todos los caminos llevan al retorno sin código muerto*, *Atributos inicializados*, *Super!*, *Ternarios* y *Variables Locales Clásicas*.

El detalle de qué se cubre en cada etapa está en el informe correspondiente (ver más abajo).

## Cómo usar el programa

### Compilar

Desde `Código/` (JDK 21):

```bash
./build.sh
```

Esto compila el proyecto y arma `Código/out/Compilador.jar`.

### Ejecutar

```bash
java -jar Código/out/Compilador.jar <archivo-fuente>
```

Corre la Etapa 4 (léxico + sintáctico + declaraciones + sentencias). El chequeo de sentencias sólo corre si no hubo errores léxicos, sintácticos ni de declaraciones. Los main de las etapas anteriores siguen en el jar:

```bash
java -cp Código/out/Compilador.jar View.ModuloPrincipalET3 <archivo-fuente>   # Etapa 3
java -cp Código/out/Compilador.jar View.ModuloPrincipalET2 <archivo-fuente>   # Etapa 2
java -cp Código/out/Compilador.jar View.ModuloPrincipal <archivo-fuente>      # Etapa 1 (imprime los tokens)
```

`<archivo-fuente>` puede tener cualquier extensión. Si no hay errores se imprime `[SinErrores]`; si los hay, cada uno se imprime con una línea legible seguida de la etiqueta `[Error:<lexema>|<línea>]`. La etiqueta es la misma para errores léxicos, sintácticos y semánticos; en un error semántico, el lexema es el del token que provoca el error y la línea es la de ese mismo token. Los códigos internos `ERR_*` sólo eligen la descripción y nunca se imprimen (ver `Documentación/etapa3-semantico/casos-de-prueba.md` y `Documentación/etapa4-semantico2/casos-de-prueba.md`).

Ejemplo:

```bash
$ java -jar Código/out/Compilador.jar Código/resources/semantico/conErrores/semError15.java
Error Semántico en línea 5: el tipo "Foo" no está declarado.
[Error:Foo|5]
```

### Correr los tests

Desde `Código/`, después de `./build.sh`:

```bash
CP="out/classes:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"
javac -cp "$CP" -d out/test-classes $(find src/test -name "*.java")
java -cp "$CP:out/test-classes" org.junit.runner.JUnitCore \
    test.java.TesterDeCasosSinErrores test.java.TesterDeCasosConErrores \
    test.java.TesterSintacticoDeCasosSinErrores test.java.TesterSintacticoDeCasosConErrores test.java.TesterSintacticoModoPanico \
    test.java.TesterSemanticoDeCasosSinErrores test.java.TesterSemanticoDeCasosConErrores \
    test.java.TesterSinErroresSem2 test.java.TesterConErroresSem2 test.java.TesterLogrosSem2
```

Los testers buscan los casos con rutas relativas (`resources/...`), así que tienen que correrse desde `Código/`.

## Dónde está el resto de la documentación

Este README es sólo una guía rápida de uso. El índice de toda la documentación está en `Documentación/README.md`. Los informes de cada etapa:

- `Documentación/etapa1-lexico/informe-analizador-lexico.md`: requisitos, tokens, arquitectura y autómata del léxico.
- `Documentación/etapa2-sintactico/analizador-sintactico.md`: gramática, pasaje a LL(1), FIRST/FOLLOW y modo pánico.
- `Documentación/etapa3-semantico/analizador-semantico.md`: tabla de símbolos y chequeo de declaraciones.
- `Documentación/etapa4-semantico2/Informe etapa 4.md` y `requerimientos.md`: chequeo de sentencias, logros y decisiones de diseño.

Las guías de los testers están en `Documentación/testers/`, y las decisiones que se apartan del enunciado, en `Documentación/adr/`.
