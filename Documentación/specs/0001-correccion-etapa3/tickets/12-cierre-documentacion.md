---
titulo: Cierre de documentación
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 12: Cierre de documentación

**Qué se construye:** la documentación refleja lo implementado.

**Bloqueado por:** 02, 03, 04, 05, 06, 07, 08, 09, 10, 11.

**Historias:** 71.

- [x] La sección "Decisiones: tipos predefinidos, clave y redefinición" de
      `analizador-semantico.md` queda marcada como implementada.
- [x] La §5.6 de `casos-de-prueba.md` pasa de "implementación pendiente" a
      hecha, con la columna "Hoy" actualizada.
- [x] Quedan documentados `ERR_HERENCIA_TIPO_INCORRECTO`,
      `ERR_CONSTRUCTOR_NOMBRE_INVALIDO`, `ERR_REDEFINICION_INCOMPATIBLE` y
      `ERR_REDEFINE_ESTATICO`, y el retiro de `ERR_INTERFAZ_NO_DECLARADA`.
- [x] Se corrige la etiqueta vieja `[ErrorSem:<código>|<línea>]` del
      `README.md`.
- [x] Todos los testers de Etapas 1–3 en verde. La spec pasa a
      `estado: hecho`.
      Además: `diagrama_tabla_simbolos.puml` actualizado a la API actual
      (los `.png` no se regeneraron: no hay PlantUML en el entorno), el
      plan de corrección marcado como implementado, y se borró
      `Tipo.contieneParametro()`, que había quedado sin usos. 263 tests en
      verde.
