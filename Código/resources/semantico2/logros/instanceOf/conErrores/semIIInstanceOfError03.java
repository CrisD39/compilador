///[Error:instanceof|8]
// El operando tiene que ser una referencia: un int no se puede probar
// (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

class A1 {
    void m1() {
        var n = 5;
        var b = n instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
