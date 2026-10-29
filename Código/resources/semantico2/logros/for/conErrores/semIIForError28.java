///[Error:for|9]
// La actualizacion es una asignacion o un postfijo (ADR 0013): una llamada
// no lo es, aunque Java la acepte (ERR_FOR_ACTUALIZACION, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (a1 = 0; a1 < 10; m2()) {
            a1 = 2;
        }
    }

    void m2() { }
}

class Init{
    static void main()
    { }
}
