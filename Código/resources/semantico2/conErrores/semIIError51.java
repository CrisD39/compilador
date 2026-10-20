///[Error:+|7]
// §11.2 -- No hay concatenacion de cadenas con +.
// Se reporta '+' en la linea 7.

class A1 {
    void m1() {
        var s = "a" + "b";
    }
}

class Init{
    static void main()
    { }
}
