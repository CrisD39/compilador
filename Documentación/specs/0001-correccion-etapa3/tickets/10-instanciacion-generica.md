---
titulo: Instanciación genérica en la redefinición
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 10: Instanciación genérica en la redefinición (D5 por `extends`, D6)

**Qué se construye:** las signaturas heredadas de un tipo genérico se
comparan instanciadas con el argumento de la herencia, también en cadena.
Al heredar sin argumento, el parámetro de tipo se conserva (no pasa a
`Object`). Los parámetros de tipo de método se comparan por posición.

**Bloqueado por:** 03, 09.

**Historias:** 44, 45, 46, 47, 48, 49.

- [ ] `Tipo.sustituir` y el mapa `ancestro → argumento` compuesto en la
      consolidación. Los miembros copiados no se reescriben.
- [ ] Pasan `semCorrecto25`, `26` y `28`.
- [ ] Pasan `semError61` (el `T` de la hija no es el de `Base`),
      `semError79` (herencia sin argumento) y `semError80` (genérico contra
      no genérico).
- [ ] `semCorrecto20` sigue en verde.
- [ ] Ningún caso que pasaba deja de pasar.
