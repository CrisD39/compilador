///[Error:+|7]
// §11.2 -- Un resultado void no se usa como operando.
// Se reporta '+' en la linea 7.

class A1 {
    void m1() {
        var v = 1 + m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
