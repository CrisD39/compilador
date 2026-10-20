///[SinErrores]
// §6.3 -- Constructores por aridad y sin llamada implicita a super: B2() es valido
// aunque A1 solo tenga A1(int).

class A1 {
    int a1;

    public A1(int p1) {
        a1 = p1;
    }
}

class B2 extends A1 {
    public B2() {
        a1 = 0;
        return;
    }

    public B2(int p1, int p2) {
        a1 = p1 + p2;
    }
}

class C3 {
    A1 y;

    void m1() {
        var v = new A1(3);
        var w = new B2();
        var x = new B2(1, 2);
        y = new B2();
    }
}

class Init{
    static void main()
    { }
}
