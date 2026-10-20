---
titulo: Clave (nombre, aridad)
estado: ready-for-agent
spec: ../../0001-correccion-etapa3.md
---

# 06: Clave (nombre, aridad) (D4)

**Qué se construye:** la sobrecarga es sólo por aridad. Dos métodos con el
mismo nombre y la misma cantidad de parámetros son duplicados, aunque
difieran los tipos o el retorno. Lo mismo para constructores y para los
métodos de una interfaz.

**Bloqueado por:** 01.

**Historias:** 28, 29, 30, 31, 32, 33, 69.

- [ ] `getFirma()` pasa a `getClave()` en métodos, constructores y
      atributos, y los miembros se indexan por clave.
- [ ] Pasan `semCorrecto27` y `semError56`, `76`, `77`, `85`.
- [ ] Las descripciones de método y constructor duplicados hablan de "la
      misma cantidad de parámetros".
- [ ] `semCorrecto14` y `semCorrecto15` pasan a ser errores por la clave:
      se mueven a `conErrores/` con la etiqueta corregida (sin tocar el
      código del caso) y se reporta el cambio.
- [ ] Ningún otro caso que pasaba deja de pasar; si alguno cambia por la
      regla, se corrige su etiqueta y se reporta.
