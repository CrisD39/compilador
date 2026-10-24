---
name: implementacion
description: Implementar o modificar funcionalidad del compilador MiniJava (léxico, sintáctico LL(1), tabla de símbolos y chequeos semánticos). Usar cuando se pida agregar un logro, un chequeo semántico, una regla de gramática o corregir un bug del compilador.
---

# Implementación — compilador MiniJava

## Antes de tocar código

1. Leer la sección relevante del diseño en `Documentación/`:
   - `Documentación/etapa2-sintactico/analizador-sintactico.md`: gramática, transformación a LL(1), FIRST/FOLLOW.
   - `Documentación/etapa3-semantico/analizador-semantico.md`: tabla de símbolos, Pasada 1/2, "Logros (Etapa 3)", plan para chequeo de sentencias.
   - `enunciados/`: enunciados de la cátedra (fuente de verdad ante dudas).
2. Ubicar el caso en la arquitectura (abajo) y decidir en qué capa va el cambio.
3. Si el cambio corresponde a un logro, revisar que haya casos en `Código/resources/` que lo cubran; si no, usar la skill `diseno-testing`.

## Arquitectura (resumen)

Capas en `Código/src/`, sin dependencias externas:

- **View** (`ModuloPrincipal`, `ModuloPrincipalET2`, `ModuloPrincipalET3`, `ModuloPrincipalET4`): sólo presentación y formato de salida. Las cuatro se mantienen porque cada tester de etapa invoca la suya; ET3 queda congelado y ET4 es el main del jar. No mover lógica acá.
- **Controller** (`Analizador*HandlerImpl`): arma el pipeline. ET3: `SourceManagerMejorado` → `AnalizadorLexicoImpl` → `AnalizadorSintacticoImpl(lexico, tablaSimbolos)` → `start()` → `tablaSimbolos.estaBienDeclarado()` → `tablaSimbolos.consolidar()`. ET4 (`AnalizadorSemantico2HandlerImpl`): lo mismo y después `tablaSimbolos.chequearSentencias()`, sólo si no hubo errores previos.
- **Model/lexico**: autómata con un método por estado. Errores léxicos van al `ResultadoLexicoListener` y no cortan.
- **Model/sintactico**: descenso recursivo LL(1). Un método por no terminal, con la producción en un comentario encima. Errores → `ErrorSintactico` + `sincronizar()` (modo pánico), nunca excepción. Las acciones semánticas que construyen la tabla se ejecutan acá, mientras se parsea.
- **Model/semantico**: `TablaSimbolos` (instancia única pasada por referencia, **no** Singleton), `Clase`, `Interfaz`, `Metodo`, `Constructor`, `Atributo`, `Parametro`, `VariableLocal`, `Tipo` (una sola clase, sin jerarquía), `ErrorSemantico`, `ExcepcionSemantica`; los nodos del AST en `Model.semantico.ast`.

## Reglas que hay que respetar

- **Orden en `TablaSimbolos.consolidar()`**: primero `Chequeable.estaBienDeclarado(tabla, entornoGenerico)` sobre cada entidad (tal como la escribió el usuario), después `Clase/Interfaz.consolidar()` (herencia, ciclos, copia de miembros del padre). Invertirlo duplica errores en subclases y evalúa genéricos del padre en el entorno de la hija.
- **Chequeable delega hacia abajo**: cada entidad chequea lo suyo y delega en sus miembros, pasando la misma tabla y extendiendo `entornoGenerico` con sus propios parámetros de tipo.
- **Errores semánticos no cortan**: se agregan con `tabla.agregarError(new ErrorSemantico(CODIGO, linea, lexema))` y el análisis sigue. `estaBienDeclarado()` y `consolidar()` corren aun con errores sintácticos.
- **Códigos de error**: categorías fijas `ERR_*` (ver `Documentación/etapa3-semantico/casos-de-prueba.md`). Reusar uno existente si aplica; uno nuevo se documenta y se cubre con un caso.
- **Determinismo**: clases/interfaces en `LinkedHashMap` (orden de declaración), así el error reportado ante ciclos o duplicados es siempre el mismo. No cambiar a `HashMap`.
- **Clase e Interfaz comparten namespace**. Un nombre duplicado descarta ambas entidades para siempre (`nombresInvalidados`), y se reporta en la línea de la segunda declaración.
- **Métodos**: `crearMetodo()` deja un placeholder; tipo de retorno y parámetros se parchean después vía `TablaSimbolos.set*AMetodoActual`, y recién entonces `agregarMetodoActualAClaseActual()` (la firma depende de los parámetros reales).
- **Gramática**: cualquier cambio tiene que seguir siendo LL(1). Actualizar el comentario de la producción y `analizador-sintactico.md`.
- **Formato de salida** (contrato con los testers): `[SinErrores]` o, para cualquier error (léxico, sintáctico o semántico), una línea legible + `[Error:<lexema>|<línea>]`. En el semántico, lexema y línea salen del mismo token. Un código `ERR_*` nuevo lleva su descripción en `ErrorSemantico.getDescripcion()`.

## Estilo

- Código, identificadores y comentarios en español, como el resto del proyecto.
- Comentarios que explican el **por qué** (qué caso límite cubre, qué se rompería si se cambia), con referencia al logro o a la sección del doc.
- Java 21 sin dependencias nuevas, compilado con `javac` puro (no Maven/Gradle).

## Verificar

Desde `Código/`: `./build.sh`, después compilar y correr **todos** los testers (léxico, sintáctico, pánico, semántico). Los comandos están en la skill `diseno-testing` y en `CLAUDE.md`. Reportar los resultados reales. Si algo falla, decir cuál caso y por qué.
