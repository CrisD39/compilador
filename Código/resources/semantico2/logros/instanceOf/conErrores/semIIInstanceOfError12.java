///[Error:instanceof|8]
// Sin precedencia (politica de Etapa 2): a == a instanceof A1 es
// (a == a) instanceof A1, y un boolean no se puede probar
// (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

class A1 {
    void m1(A1 a) {
        var r = a == a instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
