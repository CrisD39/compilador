///[Error:;|9]
// §12.2 -- Una asignacion parentizada no es sentencia de asignacion.
// Se reporta ';' en la linea 9.

class A1 {
    int a1;

    void m1() {
        (a1 = 3);
    }
}

class Init{
    static void main()
    { }
}
