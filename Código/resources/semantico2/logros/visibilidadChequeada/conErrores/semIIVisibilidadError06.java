///[Error:m1|11]
// Tampoco un metodo private: la subclase no lo puede llamar implicito
// (ERR_ACCESO_PRIVADO, sobre el nombre).

class A1 {
    private int m1() { return 1; }
}

class B2 extends A1 {
    void m2() {
        var n = m1();
    }
}

class Init{
    static void main()
    { }
}
