///[SinErrores]
// §6.2 -- Sobrecarga simple por aridad: m2/0, m2/1 y m2/2.

class A1 {
    int m2() {
        return 0;
    }

    int m2(int p1) {
        return p1;
    }

    boolean m2(int p1, boolean p2) {
        return p2;
    }

    void m1() {
        var a = m2() + m2(1);
        var b = m2(1, true);
    }
}

class Init{
    static void main()
    { }
}
