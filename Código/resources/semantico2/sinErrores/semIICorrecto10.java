///[SinErrores]
// §9.2 -- Interfaz que extiende a otra: conforma con la interfaz ancestra
// (transitivo).

interface I1 {
    void m2();
}

interface I2 extends I1 {
    void m3();
}

class A1 implements I2 {
    I1 v;
    I2 w;

    void m2() {}
    void m3() {}

    void m1() {
        v = new A1();
        w = new A1();
        v = w;
    }
}

class Init{
    static void main()
    { }
}
