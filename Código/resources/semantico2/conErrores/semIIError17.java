///[Error:m2|8]
// §10.2 -- En un metodo estatico, una llamada sin receptor solo puede seleccionar un
// metodo estatico.
// Se reporta 'm2' en la linea 8.

class A1 {
    static void m1() {
        m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
