///[Error:;|9]
// super suelto no es un valor: como sentencia, despues de super se espera
// ( o . (error sintactico, sobre el ;).

class A1 { }

class B2 extends A1 {
    void m1() {
        super;
    }
}

class Init{
    static void main()
    { }
}
