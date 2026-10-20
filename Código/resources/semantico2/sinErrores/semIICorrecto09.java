///[SinErrores]
// §9.2 -- Subtipo -> supertipo, clase -> interfaz que implementa, y todo tipo clase ->
// Object.

interface I1 {
    void m2();
}

class A1 implements I1 {
    void m2() {}
}

class B2 extends A1 {
    A1 a;
    I1 i;
    Object o;

    void m1() {
        a = new B2();
        i = new B2();
        o = new A1();
        i = a;
    }
}

class Init{
    static void main()
    { }
}
