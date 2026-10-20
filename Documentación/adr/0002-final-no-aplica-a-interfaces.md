# `final` no aplica a interfaces

El enunciado del Logro 1 (E3) admitía `final` en clases, métodos e
interfaces. Se decidió no admitirlo en interfaces, porque Java tampoco lo
permite: una interfaz existe para ser implementada, y declararla final
contradice ese propósito. Se quitó de la gramática, así que
`final interface X {}` da un error sintáctico (se esperaba `class`), sin un
chequeo semántico aparte.

## Consecuencias

- La exhaustividad de un tipo sellado es de tres vías en clases (cada subtipo
  permitido es final, sellado o no sellado) y de dos vías en interfaces
  (sellado o no sellado).
- Si la cátedra exige el enunciado del logro al pie de la letra, hay que
  volver a agregar `final` en `<Interfaz>` y definir qué significa una
  interfaz final, algo que Java no define.
