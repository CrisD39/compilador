///[SinErrores]
// Metodo generico invocado con un argumento; T se infiere como int.

class A1 {
    <T> T id(T x) {
        return x;
    }

    void m1() {
        var r = id(5);
    }
}

class Init{
    static void main()
    { }
}
