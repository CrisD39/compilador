# Tipos raw aceptados en declaraciones

**Estado:** reemplazado por el ADR 0007 (`0007-sin-tipos-raw.md`). El
enunciado completo extiende §5.2 a la Etapa 3.

El enunciado (§5.2) prohíbe usar un tipo declarado genérico sin argumento
fuera de una herencia. Igual se decidió aceptar `Caja dato;` en los tipos de
atributos, parámetros y retornos, como en Java, porque la Etapa 3 ya lo
implementa y lo prueba así (Logro 5, `etapa3-semantico/casos-de-prueba.md`
§5.5). La desviación queda acotada a las declaraciones: crear un objeto raw
(`new Caja()`) sigue siendo error, como dice §5.2 (caso `semIIError24`).

## Consecuencias

- Los miembros vistos a través de un tipo raw **conservan el parámetro de
  tipo original**, igual que en la herencia sin argumento: `dato.contenido`
  tiene tipo `T`, no `Object`. Java los borraría a `Object`, pero el
  enunciado (§13) excluye explícitamente ese borrado y la cota implícita
  `Object`. Por eso, `String s = dato.contenido;` es un error.
- Si la cátedra exige §5.2 al pie de la letra, revertir esta decisión
  implica que el control de declaraciones rechace el tipo raw y que se
  ajusten los casos de Etapa 3 que hoy lo aceptan.
