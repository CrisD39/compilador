///[Error:;|7]
// §12.2 -- Un acceso a componente no es sentencia.
// Se reporta ';' en la linea 7.

class A1 {
    void m1(A1[] p1) {
        p1[0];
    }
}

class Init{
    static void main()
    { }
}
