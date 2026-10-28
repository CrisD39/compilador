///[Error:noExiste|13]
// Orden sin tipo esperado (D4, paso 3): las dos ramas se tipan antes de
// filtrar void. La rama izquierda es void, pero el error que sale es el de
// la derecha (ERR_VARIABLE_NO_DECLARADA, sobre noExiste), no el
// ERR_TERNARIO_RAMA_VOID sobre el ?.

class A1 {
    void v() {}

    void m1(boolean c) {
        var z = c ? v()
                :
                noExiste;
    }
}

class Init{
    static void main()
    { }
}
