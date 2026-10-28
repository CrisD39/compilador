///[SinErrores]
// Ternario con tipo esperado (spec 0007, D3 y D4 paso 2): B2 y C2 son
// hermanas que extienden A1, y cada rama conforma con el tipo esperado A1.
// Valen como valor de "=" sobre un atributo, como return de un metodo que
// retorna A1 y como inicializador de un atributo A1. Sin tipo esperado
// serian error (ADR 0011); en Java compilan en los tres contextos.

class A1 {}

class B2 extends A1 {}

class C2 extends A1 {}

class D4 {
    boolean c;
    A1 i = c ? new B2() : new C2();
    A1 a;

    void m1(boolean c) {
        a = c ? new B2() : new C2();
        a = (c ? new C2() : new B2());
    }

    A1 m2(boolean c) {
        return c ? new B2() : new C2();
    }
}

class Init{
    static void main()
    { }
}
