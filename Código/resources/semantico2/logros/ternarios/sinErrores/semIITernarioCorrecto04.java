///[SinErrores]
// Ternario sin tipo esperado (spec 0007, D4 paso 3): tiene el tipo de la
// rama con la que la otra conforma. B2 conforma con A1, asi que en los dos
// ordenes el ternario es A1, y vale como receptor, en == y en un var (que
// despues se asigna a un A1). Lo mismo con K3 y la interfaz I1 que
// implementa: el ternario es I1.

interface I1 {
    int getI();
}

class A1 {
    int getN() { return 1; }
}

class B2 extends A1 {}

class K3 implements I1 {
    int getI() { return 2; }
}

class D4 {
    int x;
    boolean b;
    A1 a;

    void m1(boolean c, A1 a1, B2 b2, I1 i, K3 k) {
        x = (c ? b2 : a1).getN();
        x = (c ? a1 : b2).getN();
        b = (c ? b2 : a1) == a1;
        b = a1 == (c ? a1 : b2);
        var v = c ? new B2() : new A1();
        a = v;
        var w = c ? new A1() : new B2();
        a = w;
        x = (c ? k : i).getI();
        x = (c ? i : k).getI();
    }
}

class Init{
    static void main()
    { }
}
