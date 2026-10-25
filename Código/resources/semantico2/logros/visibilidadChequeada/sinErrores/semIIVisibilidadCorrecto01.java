///[SinErrores]
// Acceso a un atributo publico desde otra clase.

class A1 {
    public int x;
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
