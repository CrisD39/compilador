///[Error:A1|8]
// D11 -- El diamante tambien en la creacion de un arreglo (new A1<>[2]) es
// el logro Genericidad avanzada, todavia no soportado.
// Se reporta 'A1' en la linea 8.

class A1 {
    void m1() {
        var w = new A1<>[2];
    }
}

class Init{
    static void main()
    { }
}
