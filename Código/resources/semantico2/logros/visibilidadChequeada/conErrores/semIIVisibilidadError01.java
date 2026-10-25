///[Error:x|12]
// Acceso a un atributo private desde otra clase (ERR_ACCESO_PRIVADO,
// sobre el nombre del atributo).

class A1 {
    private int x;
}

class B2 {
    void m1() {
        var a = new A1();
        a.x = 1;
    }
}

class Init{
    static void main()
    { }
}
