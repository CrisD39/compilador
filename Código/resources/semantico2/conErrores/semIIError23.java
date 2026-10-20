///[Error:I1|11]
// §11.7 -- No se instancian interfaces.
// Se reporta 'I1' en la linea 11.

interface I1 {
    void m2();
}

class A1 {
    void m1() {
        var v = new I1();
    }
}

class Init{
    static void main()
    { }
}
