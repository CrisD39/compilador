///[Error:m1|11]
// Llamada estatica a un metodo private desde otra clase
// (ERR_ACCESO_PRIVADO, sobre el nombre del metodo).

class A1 {
    private static int m1() { return 1; }
}

class B2 {
    void m2() {
        var n = A1.m1();
    }
}

class Init{
    static void main()
    { }
}
