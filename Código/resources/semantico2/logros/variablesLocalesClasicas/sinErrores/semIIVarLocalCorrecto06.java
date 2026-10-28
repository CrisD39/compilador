///[SinErrores]
// Ocultamiento (spec 0009, D5): una local clasica puede llevar el nombre de
// un atributo, como var. En int b1 = b1 el inicializador no ve su propio
// nombre, asi que lee el atributo b1.

class A1 {
    int a1;
    int b1;

    int m1() {
        int a1 = 2;
        int b1 = b1;
        return a1 + b1;
    }
}

class Init{
    static void main()
    { }
}
