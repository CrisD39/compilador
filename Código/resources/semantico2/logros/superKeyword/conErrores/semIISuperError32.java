///[Error:;|11]
// Despues del . de un acceso con super se espera el nombre del atributo o
// del metodo (error sintactico, sobre el ;).

class A1 {
    int x;
}

class B2 extends A1 {
    void m1() {
        super.;
    }
}

class Init{
    static void main()
    { }
}
