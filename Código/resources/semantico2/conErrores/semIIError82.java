///[Error:;|7]
// §12.2 -- Una sentencia de llamada no puede basarse en un primario parentizado.
// Se reporta ';' en la linea 7.

class A1 {
    void m1(A1 p1) {
        (p1).m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
