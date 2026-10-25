///[Error:m1|11]
// Llamada a un metodo private desde otra clase (ERR_ACCESO_PRIVADO, sobre
// el nombre del metodo).

class A1 {
    private void m1() { }
}

class B2 {
    void m2(A1 a) {
        a.m1();
    }
}

class Init{
    static void main()
    { }
}
