///[Error:instanceof|9]
// Una llamada void no produce valor: no se puede probar
// (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

class A1 {
    void nada() { }

    void m1() {
        var b = nada() instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
