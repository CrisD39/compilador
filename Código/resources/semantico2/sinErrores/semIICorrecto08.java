///[SinErrores]
// §10.1 -- Atributos heredados se resuelven sin receptor; e.x busca en el tipo estatico
// de e.

class A1 {
    int a1;
}

class B2 extends A1 {
    int b2;

    void m1(B2 p1) {
        a1 = 1;
        p1.a1 = 2;
        p1.b2 = a1;
    }
}

class Init{
    static void main()
    { }
}
