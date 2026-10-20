///[Error:m3|7]
// §9.5 -- Una llamada void no puede ser argumento actual.
// Se reporta 'm3' en la linea 7.

class A1 {
    void m1() {
        m3(m2());
    }

    void m2() {}
    void m3(int p1) {}
}

class Init{
    static void main()
    { }
}
