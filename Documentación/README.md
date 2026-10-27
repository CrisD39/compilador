# Documentación

Orden de lectura sugerido: `enunciados/` (qué pide la cátedra) → carpeta de la etapa que se esté trabajando → `testers/` para correr y ampliar pruebas.

| Carpeta | Contenido |
|---|---|
| `enunciados/` | PDFs de la cátedra: pautas de las etapas, aspectos léxicos, reglas de sintaxis y semántica de MiniJava. |
| `etapa1-lexico/` | `informe-analizador-lexico.md`: requisitos REQ-*, tokens, arquitectura, autómata y casos de prueba. |
| `etapa2-sintactico/` | `analizador-sintactico.md` (gramática, paso a LL(1), FIRST/FOLLOW, modo pánico), `sintaxis-minijava.md` (gramática de la cátedra), `logros.md` (logros apuntados). |
| `etapa3-semantico/` | `analizador-semantico.md` (tabla de símbolos, pasadas, logros, chequeo de sentencias), `casos-de-prueba.md` (propuesta de casos, formato `[Error:<lexema>|<línea>]` y categorías internas `ERR_*`), `diagramas/` (UML de la tabla de símbolos), `plan-correccion-etapa3.md` (pasos para dejar el chequeo de declaraciones conforme a la Parte I del enunciado). |
| `etapa4-semantico2/` | `requerimientos.md` (logros de la etapa, decisiones de diseño del chequeo de sentencias, nodos del AST y estado de la implementación), `casos-de-prueba.md` (índice de los 263 casos base, 92 sin errores y 171 con errores: los 10 de referencia de la cátedra en una nota y el resto en tablas; y convención de lexema con sus códigos `ERR_*`). |
| `testers/` | Guías de uso de los testers JUnit: `guia-testers-lexico.md` y `guia-testers-sem2.md`. |
| `GLOSARIO.md` | Vocabulario del análisis semántico: tipos declarados, fases, miembros, herencia y genericidad. Ante dos palabras para lo mismo, usar la del glosario. |
| `specs/` | Tracker local: una spec por archivo (`NNNN-<nombre>.md`, frontmatter `estado: ready-for-agent` / `hecho`) y, si se trocea, sus tickets en `NNNN-<nombre>/tickets/`. Specs: `0001-correccion-etapa3` y `0002-correccion-etapa3-enunciado-completo` (control de declaraciones), `0003-chequeo-sentencias-base` (chequeo de sentencias base de la Etapa 4, con *Multi-detección* y *Postfijos*), `0004-caminos-de-retorno` (logro *Todos los caminos llevan al retorno sin código muerto*), `0005-atributos-inicializados` (logro *Atributos inicializados*). |
| `adr/` | Decisiones de diseño difíciles de revertir o que se apartan del enunciado, una por archivo (`0001-…md`). Las de la Etapa 4 son la 0007 (sin tipos raw), la 0008 (el logro de caminos de retorno reemplaza a §12.5) y la 0009 (sólo los literales `true`/`false` son condiciones constantes; no se evalúan expresiones). |
