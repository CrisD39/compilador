///[Error:m3|15]
// §10.2 -- Llamada sobre un receptor de interfaz a un metodo que la interfaz no
// declara.
// Se reporta 'm3' en la linea 15.

interface I1 {
    void m2();
}

class A1 implements I1 {
    void m2() {}
    void m3() {}

    void m1(I1 p1) {
        p1.m3();
    }
}

class Init{
    static void main()
    { }
}
