///[SinErrores]
// Lo que probaba el segundo for de m2 en semIIForCorrecto04: clausulas
// opcionales del for. Cada for va en su propio metodo, porque despues de un
// for sin condicion (infinito) el codigo es inalcanzable.

class A1 {
    int a1;

    void m1() {
        for (var i = 0; ; i = i + 1)
            a1 = i;
    }

    void m2() {
        for (var i = 0; i < 3;) {
            i = i + 1;
        }
    }
}

class Init{
    static void main()
    { }
}
