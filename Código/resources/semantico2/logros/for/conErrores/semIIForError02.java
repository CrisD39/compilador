///[Error:for|9]
// La actualizacion tiene que ser una asignacion: i++ no lo es, aunque Java lo
// acepte (ERR_FOR_ACTUALIZACION, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i++) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
