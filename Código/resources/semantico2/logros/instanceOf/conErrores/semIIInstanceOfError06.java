///[Error:instanceof|10]
// String es final (ADR 0004): no puede tener una subclase que implemente
// I1, asi que no se puede probar contra ella (ERR_OPERADOR_INCOMPATIBLE,
// sobre instanceof).

interface I1 {}

class A1 {
    void m1(String s) {
        var r = s instanceof I1;
    }
}

class Init{
    static void main()
    { }
}
