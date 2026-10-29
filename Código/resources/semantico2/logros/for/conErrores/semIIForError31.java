///[Error:++|10]
// El postfijo de la actualizacion se chequea como en cualquier sentencia
// (logro Postfijos): el operando tiene que ser int, y un boolean no lo es
// (ERR_OPERADOR_INCOMPATIBLE, sobre el ++).

class A1 {
    int a1;

    void m1(boolean b) {
        for (var i = 0; i < 10; b++) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
