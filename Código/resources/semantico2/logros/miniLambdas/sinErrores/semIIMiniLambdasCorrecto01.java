///[SinErrores]
// Lambda bien formada: parametro sin tipo, cuerpo de una unica expresion, sin
// captura ni this.

class A1 {
    int a1;

    void m1() {
        var f = x -> x + 1;
    }
}

class Init{
    static void main()
    { }
}
