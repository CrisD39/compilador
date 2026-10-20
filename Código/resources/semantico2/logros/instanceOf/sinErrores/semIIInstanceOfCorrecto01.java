///[SinErrores]
// instanceof con clase no generica que esta en la jerarquia del operando.

class A1 {
    void m1() {
        A1 a = new A1();
        boolean b = a instanceof B2;
    }
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
