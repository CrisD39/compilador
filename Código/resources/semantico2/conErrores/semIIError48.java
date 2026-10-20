///[Error:=|10]
// §9.5 -- Una llamada void no produce valor: no puede ser lado derecho de una
// asignacion.
// Se reporta '=' en la linea 10.

class A1 {
    int a1;

    void m1() {
        a1 = m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
