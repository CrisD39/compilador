///[SinErrores]
// super.m1() invoca a un metodo del ancestro.

class A1 {
    void m1() {}
}

class B2 extends A1 {
    void m2() {
        super.m1();
    }
}

class Init{
    static void main()
    { }
}
