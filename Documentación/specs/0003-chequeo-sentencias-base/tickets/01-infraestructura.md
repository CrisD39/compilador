---
titulo: Infraestructura
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 01: Infraestructura

**Qué se construye:** el andamiaje de la tercera pasada, sin chequeos
todavía. Al terminar, el árbol compila, el jar corre ET4 y
`chequearSentencias()` recorre las unidades propias sin hacer nada en
ellas, porque todavía no hay AST.

**Bloqueado por:** —.

**Decisiones:** D2, D3, D4, D6, D7 de la spec. La convención de casos es la
de "Testing Decisions".

**Dónde:**
- Borrar el WIP (D2): `Model/semantico/Nodo*.java`, `Alcance.java`, los
  stubs de scope de `TablaSimbolos`, `Metodo.variablesLocales` y
  `Controller/AnalizadorSemanticoHandler2{,Impl}.java`.
- `Controller/AnalizadorSemantico2Handler{,Impl}.java`: el pipeline de ET3
  y, si no hubo errores léxicos, sintácticos ni de declaraciones,
  `tablaSimbolos.chequearSentencias()`. Para saber si hubo errores léxicos
  hace falta que el listener lo informe, o que el handler los cuente.
- `View/ModuloPrincipalET4.java`: copia del formato de salida de ET3, con
  el handler nuevo.
- `build.sh`: el main del jar pasa a `View.ModuloPrincipalET4`.
- `src/test/java/Tester{Sin,Con}ErroresSem2.java`: el campo `init` pasa a
  `ModuloPrincipalET4`.
- `Model/semantico/ExcepcionSemantica.java` (token + código, con
  `comoError()`).
- `Model/semantico/Variable.java` (interfaz), implementada por `Parametro`
  y `Atributo`, y `VariableLocal`.
- `TablaSimbolos`: la pila `Deque<Map<String, VariableLocal>>`,
  `abrirBloque()`/`cerrarBloque()`, `declararLocal(...)`, `buscarVariable(nombre)` (D6),
  `constructorActual`, `atributoActual`, `setUnidadActual(...)` y
  `chequearSentencias()` (D4).
- `Metodo.miClase` se setea en `agregarMetodoActualAClaseActual()` (o
  donde se registre el método). `Atributo` y `Constructor` guardan su
  clase declarante.
- `Clase.chequearSentencias(tabla)`: recorre los atributos con inicializador,
  los constructores y los métodos **propios**. Por ahora, los cuerpos son
  `null` y se saltean.
- `src/test/java/TesterLogrosSem2.java`: parametrizado sobre los logros
  habilitados (`multiDeteccionErrores`, `postfijos`). Exige todas las
  etiquetas `///[...]` iniciales de cada caso con error, y `[SinErrores]` en
  los demás. Se agrega a `test.sh` dentro del grupo `sem2`.
- `logros/multiDeteccionErrores/conErrores/semIIMultiErrorError01.java`:
  la línea 2 pasa a `///[Error:z|11]`, sin mover las demás.

**Criterios:**
- [ ] `./build.sh` compila, y `java -jar out/compilador.jar` usa ET4.
- [ ] `./test.sh` (Etapas 1–3) en verde.
- [ ] `./test.sh sem2` corre los tres testers de Sem2. Pueden fallar casos,
      pero no puede fallar la compilación.
- [ ] Un programa con un error de declaraciones da exactamente los mismos
      errores en ET3 y en ET4.
- [ ] No queda ninguna referencia a `Alcance` ni a los stubs de scope.
