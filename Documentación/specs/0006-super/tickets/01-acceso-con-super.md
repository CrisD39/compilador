---
titulo: Acceso con super
estado: ready-for-agent
spec: ../../0006-super.md
---

# 01: Acceso con super

**Qué se construye:** `super` deja de ser un identificador y pasa a ser
palabra clave en todas las etapas. En un método de instancia, un
constructor o el inicializador de un atributo se pueden escribir
`super.x` y `super.m(...)`, como operando o al principio de una sentencia
(`super.x = 3;`, `super.m().n();`), con el encadenado normal detrás
(`super.get().y`, `super.v[0]`). El acceso con super selecciona el miembro
como se ve desde la superclase: el tipo del `extends` tal como se escribió
(con su argumento de tipo), o `Object` si no hay `extends`. De ahí en
adelante se chequea igual que si `super` fuera una variable de ese tipo,
con la sustitución genérica, los miembros que la superclase hereda, la
visibilidad, la asignabilidad y los métodos estáticos. Un miembro que sólo
declara la clase actual o una interfaz implementada da el error de
miembro inexistente sobre su nombre. En un método estático, el acceso con
super da `ERR_SUPER_EN_ESTATICO` sobre `super`. `super` suelto,
`super[0]` y `super(...)` dentro de una expresión son errores sintácticos
sobre el token que sigue a `super`. Una sentencia inalcanzable que empieza
con `super` se marca sobre `super`. En este ticket todavía no existe la
llamada al constructor de la superclase como sentencia.

**Bloqueado por:** ninguno (puede empezar ya).

**Decisiones:** de la spec, D1 (alcance), D2 (léxico), D3 (forma de
expresión y rama `.` de la sentencia), D4 (`NodoSuper`), D5 (tipo de la
superclase), D6 (acceso con super, salvo el paso 2, que es del ticket 03),
D10 (sentencia inalcanzable sobre `super`) y D13 (contrato de salida).
Glosario: *superclase*, *acceso con super*.

**Dónde:**
- Antes de tocar código: escribir el ADR propuesto en Further Notes de la
  spec (`super(...)` es una sentencia y va primera, JDK 21 y no JDK 25),
  con el número siguiente a los ADR existentes.
- Léxico: `PR_SUPER` en la tabla de palabras clave, junto a `this`.
- Parser: `super` en los FIRST de `<Operando>`, `<Expresion>` y
  `<Sentencia>`. En `<Operando>`, `super` exige un primer eslabón
  `. idMetVar` o `. idMetVar <ArgsActuales>` y después sigue el
  `<ReferenciaResto>` de siempre. En `<Sentencia>`, `super` se intercepta
  antes de la rama de expresión, como `idClase`, y si sigue `.` arma el
  acceso y continúa con la misma cola de expresión que usa la rama de
  `idClase`. Las producciones van en comentarios sobre los métodos, como el
  resto del parser.
- `NodoSuper` (un `NodoPrimario`, con el token `super`): contexto estático,
  después `chequearEncadenado` con el tipo de la superclase.
- `Clase` expone el tipo de la superclase (D5).
- `ERR_SUPER_EN_ESTATICO` en la tabla de códigos, con su descripción.
- `TesterLogrosSem2`: agregar `superKeyword` a `LOGROS_HABILITADOS`.
- Casos nuevos en `logros/superKeyword/` (tabla de la spec).

**Criterios:**
- [ ] ADR escrito, con el número siguiente a los existentes.
- [ ] Casos de `logros/superKeyword/` en verde: Correcto 01 (existe) y 02;
      Error 01 (existe), 02–08, 19 y 20.
- [ ] `class B2 extends A1 { void m2() { super.m1(); } }` (con `m1` en
      `A1`) da `[SinErrores]` con el jar.
- [ ] Ningún caso base de `semantico2/{sinErrores,conErrores}` ni de las
      Etapas 1–3 cambia de etiqueta.
- [ ] Siguen en verde los demás logros habilitados.
- [ ] `./test.sh` en verde.
