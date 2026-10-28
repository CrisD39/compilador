///[Error:?|7]
// null con un int sin tipo esperado: en un var tambien es
// ERR_TERNARIO_INCOMPATIBLE, sobre el ?.

class A1 {
    void m1(boolean c) {
        var v = c ? 1 : null;
    }
}

class Init{
    static void main()
    { }
}
