# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

MiniJava compiler for the Compiladores e Intérpretes course (UNS), built in stages: Etapa 1 lexer, Etapa 2 LL(1) recursive-descent parser with panic mode, Etapa 3 semantic analysis (declaration checking via a symbol table), Etapa 4 statement checking (Sem2: base rules of §9–§12 plus the Multi-detección and Postfijos logros; the other logros are pending). Plain Java (no Maven/Gradle), JDK 21. Code, comments, identifiers and docs are in Spanish; keep it that way.

## Commands

All commands run from `Código/` — testers resolve case folders with relative paths like `resources/semantico/conErrores/`, so they fail from anywhere else.

```bash
./build.sh                                     # compiles src/Model, src/Controller, src/View into out/classes and builds out/compilador.jar (main: View.ModuloPrincipalET4)
java -jar out/compilador.jar <archivo-fuente>  # run lexer + parser + semantic checks on one file
./test.sh                                      # build + run every tester (Stages 1-3 and Sem2), one line each (failures listed); exit 1 if any fails
./test.sh etapas1-3                            # only the Stage 1-3 testers; `sem2` = only the Sem2 ones
```

The `.githooks/pre-commit` hook runs `./test.sh` on every commit that touches `Código/`. Enable it once per clone with `git config core.hooksPath .githooks`.

Test classes (package `test.java`): `TesterDeCasos{Sin,Con}Errores` (lexer), `TesterSintacticoDeCasos{Sin,Con}Errores` and `TesterSintacticoModoPanico` (parser), `TesterSemanticoDeCasos{Sin,Con}Errores` (semantic, declarations), `Tester{Sin,Con}ErroresSem2` and `TesterLogrosSem2` (semantic, statements: Sem2). To check a single case, run the jar directly on its file and compare against the file's first line.

## Architecture

Layered MVC under `Código/src/`:

- **View**: one entry point per stage, all kept working because the testers for each stage call them: `ModuloPrincipal` (lexer only, prints tokens), `ModuloPrincipalET2` (lexer + parser), `ModuloPrincipalET3` (lexer + parser + declaration checks, frozen), `ModuloPrincipalET4` (adds statement checking; the jar's main). View only handles presentation and output format.
- **Controller**: `Analizador*HandlerImpl` wires the pipeline for each stage. The ET3 wiring is in `AnalizadorSemanticoHandlerImpl`: `SourceManagerMejorado` → `AnalizadorLexicoImpl` → `AnalizadorSintacticoImpl(lexico, tablaSimbolos)` → `sintactico.start()` → `tablaSimbolos.estaBienDeclarado()` → `tablaSimbolos.consolidar()`. Both run even when there were syntax errors. `AnalizadorSemantico2HandlerImpl` (ET4) runs the same and then `tablaSimbolos.chequearSentencias()`, but only if there were no lexical, syntax or declaration errors.
- **Model/lexico**: hand-written automaton (one method per state) in `AnalizadorLexicoImpl`. The parser pulls tokens from it one at a time. Lexical errors are pushed to a `ResultadoLexicoListener` (the View), and analysis continues after them.
- **Model/sintactico**: `AnalizadorSintacticoImpl` is a recursive-descent LL(1) parser. Each method has the grammar production it implements in a comment above it. It recovers in panic mode (`sincronizar()`) and collects `ErrorSintactico`s instead of throwing. It also runs the semantic actions that build the symbol table while it parses (Pasada 1: register names; Pasada 2: inheritance, attributes, methods), calling the `TablaSimbolos` setters (`setClaseActual`, `setMetodoActual`, `agregarMetodoActualAClaseActual`, …).
- **Model/semantico**: `TablaSimbolos` is a single instance passed by reference (deliberately not a Singleton; see the docs). It stores `Clase`/`Interfaz` in one shared namespace (`LinkedHashMap`, so error order is deterministic) and collects all `ErrorSemantico`s. The checks run as two separate calls, and the order matters: (1) `TablaSimbolos.estaBienDeclarado()` runs `Chequeable.estaBienDeclarado(tabla, entornoGenerico)` on each entity as the user wrote it, delegating down to members and passing along the in-scope generic type params; (2) `TablaSimbolos.consolidar()` calls `Clase/Interfaz.consolidar()`, which resolves inheritance and cycles and copies parent members. Running (1) after (2) would report errors twice in subclasses. ET4 adds (3) `TablaSimbolos.chequearSentencias()`, the third pass over the bodies (see Sem2 below). `Tipo` is a single class (no primitive/reference hierarchy) with `conformaCon`, `Tipo.NULL` and `Tipo.VOID`; `Metodo` still uses `tipoRetorno == null` for `void`.

## Output format and test cases

Testers capture stdout and match tags, so the output format is a contract:
- Success: `[SinErrores]`
- Lexical/syntax error: a readable line, then `[Error:<lexema>|<línea>]`
- Semantic error: `Error Semántico en línea <L>: <descripción>`, then `[Error:<lexema>|<línea>]`. This is the same tag as lexical/syntax errors, as the course guidelines require. The lexeme is the name of the entity that triggers the error, and lexeme and line come from the same token. Internally each error has a fixed category code (`ERR_CLASE_DUPLICADA`, `ERR_HERENCIA_CICLICA`, …) that only picks the description (`ErrorSemantico.getDescripcion()`) and documents the cases. It is never printed in the tag (see `Documentación/etapa3-semantico/casos-de-prueba.md`).

Case files live in `Código/resources/{sinErrores,conErrores}` (lexer), `resources/sintactico/{sinErrores,conErrores,panico}` and `resources/semantico/{sinErrores,conErrores,multiError}` and `resources/semantico2/{sinErrores,conErrores}` (Sem2). Parameterized testers pick up every file in their folder. In error cases, the **first line** must be `///<expected tag>` (e.g. `///[Error:A1|5]`), and the test checks that the tag appears in the output. To add a case, drop a file in the folder; no code changes are needed. Panic-mode and `multiError` cases have hand-written assertions or are not covered by a tester.

## Sem2 (statement checking)

Implemented by spec 0003 (`Documentación/specs/0003-chequeo-sentencias-base.md`). The parser builds an AST (`Model.semantico.ast`, `Nodo*`) for every method body, constructor body and attribute initializer and hangs it on `Metodo`, `Constructor` and `Atributo`. The third pass, `TablaSimbolos.chequearSentencias()`, walks the classes in `LinkedHashMap` order (skipping interfaces and predefined types) and, in each one, only its **own** members: initialized attributes, constructors, methods. No cascade: it does not run at all if an earlier stage reported errors. Each statement is checked separately: a node throws `ExcepcionSemantica`, `NodoBloque` catches it per statement and records it, and checking goes on with the next statement (logro *Multi-detección*). Locals live in a block stack in `TablaSimbolos` (`abrirBloque`/`cerrarBloque`, `buscarVariable`: locals → parameters → attributes). Logro syntax the parser accepts (for, ternary, lambda, classic locals, initialized attributes, array initializers, generic methods, diamond) has nodes, but checking them reports `ERR_NO_SOPORTADO`; postfix `++`/`--` is the only logro checked.

`Tester{Sin,Con}ErroresSem2` and `resources/semantico2/` (`semIICorrectoNN`, `semIIErrorNN`, 34 + 104 cases) cover statement/expression checking. Sem2 error tags use the same `[Error:<lexema>|<línea>]` format, and they run on `ModuloPrincipalET4`. `TesterLogrosSem2` runs the logros listed in its `LOGROS_HABILITADOS` (currently `multiDeteccionErrores` and `postfijos`) under `logros/`, requiring every leading `///[...]` tag; enabling a new logro means adding its name there. The usage guide is `Documentación/testers/guia-testers-sem2.md`. `Documentación/etapa4-semantico2/casos-de-prueba.md` indexes every base case (rule § of the spec PDF it covers) and fixes the lexeme convention for Sem2 error tags.

Per-achievement cases (one folder per logro of `Documentación/etapa4-semantico2/requerimientos.md`) live in `Código/resources/semantico2/logros/<logro>/{conErrores,sinErrores}/`. Each holds a skeleton (1 correct + 1 error case); only the enabled logros are run by `TesterLogrosSem2`, and the expected lexemes of the others are provisional until their spec. `logros/` is a sibling of `sinErrores/conErrores` so the existing Sem2 testers don't pick it up.

## Design docs

`Documentación/` holds the reasoning behind the design. Read the relevant file before changing behaviour:
- `etapa2-sintactico/analizador-sintactico.md`: the grammar, how it was turned into LL(1), and FIRST/FOLLOW sets.
- `etapa3-semantico/analizador-semantico.md`: symbol table design, the two passes, the "Logros (Etapa 3)" requirements, and plans for statement checking (AST, `Entorno`).
- `etapa1-lexico/informe-analizador-lexico.md`: tokens and the REQ-* requirements.
- `testers/guia-testers-lexico.md`: how the testers work.
- `enunciados/`: the course spec PDFs. `Documentación/README.md` is the index.

## Project skills

`.claude/skills/diseno-testing` (designing and adding test cases, running the testers) and `.claude/skills/implementacion` (rules for changing the lexer, parser or semantic analysis). Load the matching one before working on either task.
