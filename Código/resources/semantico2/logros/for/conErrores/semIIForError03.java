///[Error:for|9]
// La inicializacion tiene que ser una asignacion o un var: una llamada no
// lo es (ERR_FOR_INICIALIZACION, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (m2(); a1 < 10; a1 = a1 + 1) {
            a1 = 2;
        }
    }

    void m2() { }
}

class Init{
    static void main()
    { }
}
