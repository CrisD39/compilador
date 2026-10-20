///[SinErrores]
// §10.3 -- Clases, metodos y constructores se resuelven aunque se declaren despues.

class A1 {
    void m1() {
        var v = new B2(5);
        var w = v.m2();
        m3();
    }

    void m3() {}
}

class B2 {
    public B2(int p1) {}

    int m2() {
        return 1;
    }
}

class Init{
    static void main()
    { }
}
