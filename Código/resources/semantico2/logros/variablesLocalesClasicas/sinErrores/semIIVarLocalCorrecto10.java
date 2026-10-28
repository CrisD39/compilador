///[SinErrores]
// Local clasica en la inicializacion del for (spec 0009, D8), de tipo
// primitivo y de referencia. Cada una vive en el scope propio de su for:
// despues de cerrarlo, el mismo nombre se puede volver a declarar.

class A1 {
    int a1;

    void m1() {
        for (int i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
        for (A1 a = null; a == null; a = new A1()) {
            a1 = a.a1;
        }
        int i = 1;
        A1 a = new A1();
    }
}

class Init{
    static void main()
    { }
}
