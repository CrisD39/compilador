# El logro de caminos de retorno reemplaza a §12.5 de la base

**Estado:** aceptado, sin implementar (grill del 2026-10-26).

El enunciado completo (§12.5 y §13) dice que MiniJava no exige retorno en
todos los caminos y que no rechaza código inalcanzable. El logro *Todos los
caminos llevan al retorno sin código muerto* exige exactamente lo contrario,
"al igual que en Java". Se decide que el logro esté **siempre activo** en la
Etapa 4 (`ModuloPrincipalET4`, el main del jar), sin ninguna opción para
apagarlo. Así se hizo con los demás logros: el jar implementa los logros que
se intentan, y la diferencia con la base se documenta en el informe.

De §12.5 se mantiene que una sentencia inalcanzable se chequea igual
(resolución de nombres, tipos y contexto): ahora es un error, pero ese
error no le ahorra el resto de los chequeos.

## Opciones consideradas

- **Activarlo con una opción de línea de comandos.** Las dos semánticas
  podrían convivir, pero quien corrige tendría que saber que tiene que
  activarla, y ningún otro logro funciona así.

## Consecuencias

- Los casos base que se apoyaban en la regla vieja dejan de valer:
  `semIICorrecto31`, `semIICorrecto72` (el CP-SII-38 de la cátedra) y
  `semIIError104`. Además `logros/for/semIIForCorrecto04` tiene un `for`
  después de un `for (…; ; …)` infinito. Si la cátedra corre su tester base
  con CP-SII-38, ese caso va a fallar, y el informe tiene que decirlo.
