///[Error:=|9]
// El resultado es boolean: no se puede asignar a un int
// (ERR_ASIGNACION_INCOMPATIBLE, sobre =).

class A1 {
    int n;

    void m1(A1 a) {
        n = a instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
