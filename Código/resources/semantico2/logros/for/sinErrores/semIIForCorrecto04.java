///[SinErrores]
// Las tres clausulas son opcionales, como en Java: una clausula vacia no es
// un componente mal puesto. El cuerpo puede ser una sentencia sin llaves.

class A1 {
    int a1;

    void m1() {
        for (;;) {
            a1 = 1;
        }
    }

    void m2() {
        for (var i = 0; ; i = i + 1)
            a1 = i;
        for (var i = 0; i < 3;) {
            i = i + 1;
        }
    }
}

class Init{
    static void main()
    { }
}
