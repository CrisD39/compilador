---
titulo: Descripciones de super(...) con detalle
estado: hecho
spec: ../../0006-super.md
---

# 08: Descripciones de `super(...)` con `detalle`

**Qué se construye:** el texto de `ERR_ARGUMENTO_INCOMPATIBLE`,
`ERR_CONSTRUCTOR_NO_ENCONTRADO` y `ERR_ACCESO_PRIVADO` para `super(...)`
deja de elegirse mirando si el lexema es `"super"`. Lo elige el campo
`detalle` de `ErrorSemantico`, que ya existe para las variantes que el
lexema solo no distingue. Quien lanza el error en la resolución del
constructor sabe si es un `new` o un `super(...)`, y lo pasa. Así, si otro
camino reporta uno de esos códigos sobre un token `super` por otro motivo,
no sale el texto de `super(...)`.

Solo cambia la descripción, nunca la etiqueta `[Error:...]`: ningún caso
cambia.

**Bloqueado por:** 07 (el dato tiene que llegar por la resolución del
constructor, cuya firma cambian 06 y 07).

**Origen:** `revision.md`, §2.6.

**Criterios:**
- [x] `ErrorSemantico` ya no decide ninguna descripción por el lexema;
      el método que lo hacía desaparece.
- [x] La excepción semántica puede llevar el `detalle` hasta el
      `ErrorSemantico` que se registra.
- [x] El comentario de `detalle` lista sus usos nuevos, y los de los tres
      códigos dicen de dónde sale el texto.
- [x] Las descripciones de `super(...)` son las mismas que hoy (se
      comprueba con el jar sobre los casos de `superKeyword` que dan esos
      tres códigos), y las de `new` también.
- [x] `./test.sh` en verde, sin tocar casos.
