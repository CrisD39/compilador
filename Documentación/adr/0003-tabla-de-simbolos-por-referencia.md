# Tabla de símbolos: una por compilación, pasada por referencia

El profesor pidió que la tabla de símbolos sea global pero no un Singleton.
Se interpretó "global" como **una única instancia viva por compilación**,
que todos usan: el handler de cada etapa la crea y la pasa por parámetro al
parser, a las entidades (`estaBienDeclarado(tabla, …)`) y, en Etapa 4, a los
nodos del AST (`chequear(tabla)`).

## Fundamentación

Todo lo que necesita la tabla recibe una referencia a una instancia común de
`TablaSimbolos`, y la clase no tiene atributos ni métodos `static`. Usar
estado estático dentro de `TablaSimbolos` equivale en la práctica a un
Singleton, y es todavía más rígido. El problema que señala el profesor viene
de que el estado estático vive durante toda la ejecución del programa:

- **Varias compilaciones en la misma ejecución.** Si se usa `static` o
  Singleton y hace falta volver a analizar desde cero, o si una suite de
  tests compila 20 archivos de prueba seguidos (como hacen los testers, que
  llaman a `main` una vez por caso), los símbolos del archivo 1 siguen
  guardados cuando se compila el archivo 2. Basta con olvidarse de limpiar
  una bandera o una lista estática para que quede basura colgada, y los
  errores que eso provoca son difíciles de rastrear.
- **La solución es la inyección de dependencias.** `TablaSimbolos` es una
  clase común, sin `static`. El punto de entrada de la compilación (el
  `main`, o en este proyecto el handler de cada etapa) hace un
  `new TablaSimbolos()` y le pasa esa misma referencia a cada fase que la
  necesita: al sintáctico (que construye la tabla), al semántico y, más
  adelante, al generador de código. Hoy el léxico no la usa, así que no la
  recibe.

Así, la tabla es "global" conceptualmente para todas las fases de esa
compilación. Cuando la compilación termina, el recolector de basura destruye
la instancia y no queda nada colgado.

## Opciones consideradas

- **Singleton** (`TablaSimbolos.getInstance()`): descartado por pedido del
  profesor. Además esconde la dependencia en las firmas y obliga a
  resetearla a mano entre análisis.
- **Campo estático accesible desde todos lados**: descartado. Los testers
  llaman a `main` una vez por caso en la misma JVM, así que cada corrida
  tendría que acordarse de reasignarlo para no arrastrar el estado del caso
  anterior.

## Consecuencias

- Toda clase que necesita la tabla la recibe explícitamente, y eso se ve en
  su firma.
- Cada `main` crea su propia tabla, así que los casos de prueba no se
  contaminan entre sí.
