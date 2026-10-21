---
titulo: Contratos de interfaz e interfaz contra Object
estado: hecho
spec: ../../0001-correccion-etapa3.md
---

# 11: Contratos de interfaz e interfaz contra `Object`

**Qué se construye:** una clase cumple un método de su interfaz con un
método de instancia, propio o heredado, de la misma clave y la misma
signatura instanciada. Los conflictos entre interfaces comparan la
signatura completa, y los métodos de una interfaz se comparan con los de
instancia de `Object`.

**Bloqueado por:** 10.

**Historias:** 55, 56, 57, 58, 59, 60, 61, 62, 63, 64.

- [x] Pasan `semCorrecto30` (`toString` heredado cumple el contrato) y
      `semCorrecto31` (`implements Proveedor<String>`).
- [x] Clave presente con otra signatura: `ERR_REDEFINICION_INCOMPATIBLE`
      sobre el método de la clase. Pasan `semError64` y `semError71`
      (D5 por `implements`).
- [x] Un estático no cumple el contrato: pasa `semError83`.
- [x] `ERR_METODO_RETORNO_INCOMPATIBLE` compara la signatura completa, con
      la descripción "se hereda con signaturas incompatibles": pasa
      `semError62`.
- [x] Interfaz contra `Object`: pasa `semError63`, y `semCorrecto32`
      (`debugPrint` en una interfaz) sigue en verde.
- [x] Ningún caso que pasaba deja de pasar.
      `semError37` cambió de resultado por la regla del contrato (error
      sobre el método de la clase, como `semError64`): se corrigió su
      etiqueta a `[Error:getValor|10]`, no el código (ver
      casos-de-prueba.md). Casos nuevos: `semCorrecto34`, `semError88`,
      `semError89`.
