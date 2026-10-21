---
name: diseno-testing
description: Diseñar y agregar casos de prueba para el compilador MiniJava (léxico, sintáctico, semántico) y correr los testers JUnit. Usar cuando se pidan casos de prueba nuevos, cubrir un logro/requisito con tests, o verificar que una implementación pasa los testers.
---

# Diseño de casos de prueba — MiniJava

## Cómo funcionan los testers

Los testers (`Código/src/test/java/`, paquete `test.java`) son JUnit 4 parametrizados: toman **todos** los archivos de su carpeta, llaman al `main` del Módulo Principal de la etapa y buscan una etiqueta en stdout. Agregar un caso = agregar un archivo; no se toca código.

| Etapa | Carpeta (relativa a `Código/`) | Tester | Main que invoca | Nombre de archivo |
|---|---|---|---|---|
| Léxico OK | `resources/sinErrores/` | `TesterDeCasosSinErrores` | `ModuloPrincipal` | `lexSinErroresNN.java` |
| Léxico error | `resources/conErrores/` | `TesterDeCasosConErrores` | `ModuloPrincipal` | `lexConErroresNN.java` |
| Sintáctico OK | `resources/sintactico/sinErrores/` | `TesterSintacticoDeCasosSinErrores` | `ModuloPrincipalET2` | `sintCorrectoNN.java` |
| Sintáctico error | `resources/sintactico/conErrores/` | `TesterSintacticoDeCasosConErrores` | `ModuloPrincipalET2` | `sintErrorNN.java` |
| Modo pánico | `resources/sintactico/panico/` | `TesterSintacticoModoPanico` (asserts a mano, un `@Test` por archivo) | `ModuloPrincipalET2` | descriptivo |
| Semántico OK | `resources/semantico/sinErrores/` | `TesterSemanticoDeCasosSinErrores` | `ModuloPrincipalET3` | `semCorrectoNN.java` |
| Semántico error | `resources/semantico/conErrores/` | `TesterSemanticoDeCasosConErrores` | `ModuloPrincipalET3` | `semErrorNN.java` |
| Sem2 sentencias | `resources/semantico2/{sinErrores,conErrores}/` | `TesterSinErroresSem2` / `TesterConErroresSem2` | (sin conectar) | `semIICorrectoNN` / `semIIErrorNN` |
| Sem2 por logro | `resources/semantico2/logros/<logro>/{sinErrores,conErrores}/` | (ninguno todavía) | (sin conectar) | `semII<Logro>Correcto01` / `Error01` |
| Varios errores | `resources/semantico/multiError/` | (ningún tester todavía) | `ModuloPrincipalET3` | `semMultiError<Descripcion>.java` |

Numerar continuando el último número existente de la carpeta (`ls` antes de crear).

## Formato de un caso

Primera línea = etiqueta esperada precedida de `///`. El tester de errores verifica que esa etiqueta aparezca en la salida.

- Éxito: `///[SinErrores]`
- Léxico / sintáctico / semántico: `///[Error:<lexema>|<línea>]` (formato único de la cátedra; en el semántico el lexema es el nombre de la entidad que dispara el error, ver la tabla de lexemas en `Documentación/etapa3-semantico/casos-de-prueba.md`)

Después, comentarios `//` explicando **qué** se prueba y **por qué** el error cae en esa línea (sobre todo en casos límite: qué declaración se reporta en un ciclo, cuál de dos duplicados, etc.). Recién después, el código MiniJava. La línea de la etiqueta cuenta: la línea 1 es la del `///`.

Ejemplo:

```java
///[Error:A1|5]
// Ciclo directo, el caso mas chico posible: una clase que se extiende a si
// misma. El "ciclo" tiene un solo miembro, asi que es tambien el que cierra
// la cadena -- se reporta sobre su propia linea.
class A1 extends A1{}
```

## Códigos semánticos

Son categorías fijas internas (ver `Documentación/etapa3-semantico/casos-de-prueba.md`): eligen la descripción legible (`ErrorSemantico.getDescripcion()`) pero **no** van en la etiqueta. Nombrar el código del caso en sus comentarios `//` y en la tabla de `casos-de-prueba.md`. Los vigentes están en el código: `grep -rhoE '"ERR_[A-Z_]+"' Código/src | sort -u`. No inventar un código nuevo en un test sin agregarlo también en la implementación.

## Criterios para diseñar casos

- Un caso de error prueba **un** error; si interesa la interacción de varios, va a `multiError/`.
- Por cada caso con error, pensar el "gemelo" sano (`sinErrores`) que demuestra que no hay falso positivo (p. ej. herencia lineal A←B←C sin ciclo).
- Cubrir los bordes que pide cada logro en `Documentación/etapa3-semantico/analizador-semantico.md` ("Logros (Etapa 3)"): mínimo (1 elemento), repetición triple, orden de declaración (uso antes de declarar), genéricos, sealed/nonsealed/final, interfaces múltiples.
- La línea esperada sale de la regla documentada (p. ej. duplicados se reportan en la **segunda** declaración); si la regla no está clara, consultar el doc antes de fijar la etiqueta.
- Verificar el caso contra la implementación real antes de darlo por bueno; si falla, decidir si el bug es del caso o del compilador y decirlo.

## Correr los tests

Siempre desde `Código/` (los paths de las carpetas son relativos):

```bash
cd Código
./test.sh          # compila y corre todos los testers de Etapas 1-3 (léxico, sintáctico, semántico)
./test.sh sem2     # sólo Sem2; `./test.sh todo` corre ambos
```

Un único caso: `java -jar out/compilador.jar resources/semantico/conErrores/semError15.java` y comparar con su primera línea.

`./test.sh` ya incluye los testers de las etapas anteriores. Reportar los resultados tal cual (cuántos pasan / fallan y cuáles).
