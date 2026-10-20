///[Error:;|7]
// §12.2 -- Una llamada parentizada no es sentencia de llamada.
// Se reporta ';' en la linea 7.

class A1 {
    void m1() {
        (m2());
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
