///[Error:x|11]
// Acceso a un atributo private desde otra clase.

class A1 {
    private int x;
}

class B2 {
    void m1() {
        A1 a = new A1();
        a.x = 1;
    }
}

class Init{
    static void main()
    { }
}
