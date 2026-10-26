///[Error:x|11]
///[Error:x|16]
// ERR_SENTENCIA_INALCANZABLE: despues de un while (true) o de un for sin
// condicion no se llega nunca. Un error por metodo.

class A1 {
    int x;

    void m1() {
        while (true) { }
        x = 1;
    }

    void m2() {
        for (;;) { }
        x = 1;
    }
}

class Init{
    static void main()
    { }
}
