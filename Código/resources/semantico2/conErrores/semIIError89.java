///[Error:v|7]
// §12.3 -- Una llamada void no permite inferir un tipo para var.
// Se reporta 'v' en la linea 7.

class A1 {
    void m1() {
        var v = m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
