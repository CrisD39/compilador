///[SinErrores]
// Sin modificador o con public, el miembro es publico (Parte I 6.1): se
// usa desde otra clase y desde una subclase, atributos, metodos,
// estaticos y constructores.

class A1 {
    int x;
    public int y;

    A1() { }
    public A1(int v) { }

    int m1() { return x; }
    public static int m2() { return 2; }
}

class B2 extends A1 {
    void m3() {
        x = y + m1() + A1.m2();
    }
}

class C3 {
    void m4(B2 b) {
        var a = new A1();
        var otra = new A1(3);
        a.x = a.y + b.m1() + A1.m2() + b.x;
    }
}

class Init{
    static void main()
    { }
}
