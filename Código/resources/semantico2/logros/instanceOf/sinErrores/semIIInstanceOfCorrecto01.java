///[SinErrores]
// instanceof con clase no generica que esta en la jerarquia del operando:
// bajar de A1 a su subclase B2.

class A1 {
    void m1() {
        var a = new A1();
        var b = a instanceof B2;
    }
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
