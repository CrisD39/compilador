---
titulo: AST completo
estado: pendiente
spec: ../../0003-chequeo-sentencias-base.md
---

# 02: AST completo

**Qué se construye:** el parser arma el AST de **todo** cuerpo que acepta,
incluida la sintaxis de logros, y lo engancha en la tabla de símbolos.
Todavía no se chequea nada: cada `chequear` lanza `ERR_NO_SOPORTADO` sobre
el token de su nodo.

**Bloqueado por:** 01.

**Decisiones:** `requerimientos.md`, "NODOS": criterios, mapeo de no
terminal a nodo, árbol de herencia y ⚠1–⚠13. De la spec: D8 (AST con
errores sintácticos) y D11 (tokens de "no soportado").

**Dónde:**
- Paquete `Model/semantico/ast/`: `NodoSentencia`, `NodoExpresion`,
  `NodoEncadenado`, `ValorArreglo` y todos los nodos del árbol de herencia,
  salvo los `[pendiente]` de gramática (instanceof, super, float).
- Contratos: `NodoSentencia.chequear(TablaSimbolos)`;
  `NodoExpresion.chequear(TablaSimbolos): Tipo`, `esAsignable()`,
  `esLlamable()`; `NodoEncadenado.chequear(TablaSimbolos, Tipo
  tipoIzquierdo): Tipo`.
- `AnalizadorSintacticoImpl`: cada método de cuerpos **devuelve** su nodo al
  contenedor inmediato (estrategia B, con un único punto de attach). Al
  cerrar el `<Bloque>` de un método o de un constructor, el `NodoBloque` va
  a `Metodo`/`Constructor`. El inicializador de `<RestoMiembro>` va a
  `Atributo`. Los caminos factorizados (`<SentIdClase>`, `<TrasIdClaseFor>`,
  `<TrasParenId>`, `<DecidirTrasCierre>`) producen los mismos nodos que el
  camino normal (⚠13).
- ⚠13: agregar las llamadas a `postfijoOpcional()` y `ternarioOpcional()` en
  la rama `.` de `<SentIdClase>` y de `<TrasIdClaseFor>`.
- `NodoBloque.chequear` ya hace lo definitivo: abrir el scope, try/catch por
  sentencia con `tabla.agregarError(e.comoError())` y cerrar en `finally`.
  `Clase.chequearSentencias` ya engancha el atributo inicializado con su
  propia captura.

**Criterios:**
- [ ] `./test.sh` (Etapas 1–3) en verde: construir el AST no cambia
      ningún resultado sintáctico ni de declaraciones, tampoco en modo
      pánico.
- [ ] Caso sintáctico nuevo en `resources/sintactico/sinErrores/` con
      `A1.m().x++;` y `A1.m().b ? 1 : 2;`, que hoy da error sintáctico.
- [ ] En ET4, un método con un `for` da `ERR_NO_SOPORTADO` sobre `for`. Un
      caso de cada fila de D11 da su lexema (verificado a mano).
- [ ] Ningún cuerpo parseado queda sin nodo: todo `<Sentencia>` aceptado
      termina en la lista de algún `NodoBloque`.
