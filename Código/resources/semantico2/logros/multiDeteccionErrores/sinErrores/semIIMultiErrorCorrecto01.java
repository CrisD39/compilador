///[SinErrores]
// Sin errores: el control de recuperacion no debe inventar ninguno.

class A1 {
    int a1;

    void m1() {
        a1 = 1;
        a1 = 2;
    }
}

class Init{
    static void main()
    { }
}
