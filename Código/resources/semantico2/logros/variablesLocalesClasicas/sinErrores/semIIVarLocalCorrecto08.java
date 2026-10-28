///[SinErrores]
// Ternario con tipo esperado (spec 0009, D5, ADR 0011): B2 y C3 son
// hermanas que extienden A1, y cada rama conforma con el tipo declarado A1.

class A1 {}

class B2 extends A1 {}

class C3 extends A1 {}

class D4 {
    void m1(boolean c) {
        A1 a = c ? new B2() : new C3();
    }
}

class Init{
    static void main()
    { }
}
