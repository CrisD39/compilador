///[SinErrores]
// Inicializacion con una asignacion a una local ya declarada, y a un
// atributo; la actualizacion tambien puede asignar un atributo.

class A1 {
    int a1;

    void m1() {
        var i = 0;
        for (i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
        for (a1 = 0; a1 < 10; a1 = a1 + 2) {
            i = a1;
        }
    }
}

class Init{
    static void main()
    { }
}
