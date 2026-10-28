---
titulo: Tipo esperado en asignación, inicializador y return
estado: ready-for-agent
spec: ../../0007-ternarios.md
---

# 02: Tipo esperado en asignación, inicializador y return

**Qué se construye:** donde hay un tipo esperado, el ternario se tipa
contra él, como en Java. En el valor de `=`, en el inicializador de un
atributo y en el `return` de un método no void, cada rama del ternario
tiene que conformar con el tipo esperado (el del destino, el del atributo
o el de retorno), y si una no conforma, da
`ERR_TERNARIO_RAMA_INCOMPATIBLE` sobre el `?`. Así valen
`a = c ? new B2() : new C2();`, `return c ? new B2() : new C2();` y
`A1 a = c ? new B2() : new C2();` con dos hermanas que extienden `A1`. Se
chequea la condición primero, y después cada rama completa, la izquierda y
después la derecha. El tipo esperado baja a través de un paréntesis sin
encadenado y de un ternario anidado, y el error va sobre el `?` del
ternario dueño de la rama. En `(c ? b : a).getN()` el ternario sigue sin
tipo esperado. Una vez validadas sus ramas, el ternario devuelve el tipo
esperado, así que el contexto sigue con su control de siempre y nunca da
un segundo error. En un método void o en un constructor, `return` sigue
sin tipo esperado. Los argumentos todavía se chequean sin tipo esperado.

**Bloqueado por:** 01.

**Decisiones:** de la spec, D2 (`chequearConTipoEsperado`, con su
implementación por defecto y las dos redefiniciones), D3 (los contextos
de asignación, inicializador y `return`; no los argumentos) y D4 (paso 2).
ADR 0011. Glosario: *tipo esperado*.

**Dónde:**
- `NodoExpresion`: `chequearConTipoEsperado(tabla, esperado)`, que por
  defecto devuelve `chequear(tabla)`, al lado de `chequearComoDestino`.
- `NodoExpTernaria`: lo redefine con el paso 2 de D4.
- `NodoExpParentizada`: lo delega a la expresión de adentro sólo si no
  tiene encadenado.
- `NodoExpAsignacion`, `Atributo.chequearInicializador` y `NodoReturn`
  (sólo con un tipo de retorno no void): usan `chequearConTipoEsperado` en
  lugar de `chequear`, sin cambiar su control de conformidad ni su código.
- `ERR_TERNARIO_RAMA_INCOMPATIBLE` en la tabla de códigos, con la
  descripción de la spec.
- Casos nuevos en `logros/ternarios/` (tabla de la spec).

**Criterios:**
- [ ] Casos de `logros/ternarios/` en verde: Correcto 02 y 06; Error 09,
      13 y 21. Siguen en verde los del ticket 01.
- [ ] `semIIAtribInicError15` sigue en verde con `[Error:?|7]`, ahora con
      `ERR_TERNARIO_RAMA_INCOMPATIBLE`.
- [ ] Un ternario sin tipo esperado (receptor, `==`, `var`, condiciones,
      índices) sigue con la regla del ticket 01.
- [ ] Ningún caso base de `semantico2/{sinErrores,conErrores}` ni de las
      Etapas 1–3 cambia de etiqueta.
- [ ] Siguen en verde los demás logros habilitados.
- [ ] `./test.sh` en verde.
