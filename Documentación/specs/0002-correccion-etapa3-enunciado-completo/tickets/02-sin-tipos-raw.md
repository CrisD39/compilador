---
titulo: Sin tipos raw
estado: hecho
spec: ../../0002-correccion-etapa3-enunciado-completo.md
---

# 02: Sin tipos raw (ADR 0007)

**Qué se construye:** usar una entidad genérica sin argumento fuera de la
cláusula de herencia o implementación da `ERR_TIPO_SIN_ARGUMENTO` sobre el
nombre del tipo. Cubre atributos, parámetros, retornos, tipos base de
arreglos y argumentos genéricos (también el argumento de una herencia).
Heredar sin argumento sigue siendo válido. Hoy todos esos usos dan
`[SinErrores]`.

**Bloqueado por:** 01.

**Historias:** 1–10.

**Dónde:** la validación de `Tipo` recibe si admite una cabeza sin
argumento. Sólo la usan así los supertipos de `Clase` e `Interfaz`; la
recursión en el argumento nunca la admite.

- [x] Pasan `semError98`–`104` (atributo, retorno, parámetro de método,
      parámetro de constructor, arreglo, argumento genérico, argumento de
      la herencia). Hoy fallan todos.
- [x] Pasa `semCorrecto38` (herencia sin argumento).
- [x] `semCorrecto22` sin `Lista cruda;` sigue pasando.
- [x] `ERR_TIPO_SIN_ARGUMENTO` tiene descripción en `ErrorSemantico`.
- [x] Todos los testers de Etapas 1–3 en verde.
