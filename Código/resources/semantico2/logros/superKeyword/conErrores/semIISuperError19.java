///[Error:super|12]
// Sentencia inalcanzable que empieza con super: se marca sobre super
// (ERR_SENTENCIA_INALCANZABLE). Despues se chequea y es valida.

class A1 {
    void m() { }
}

class B2 extends A1 {
    void m1() {
        return;
        super.m();
    }
}

class Init{
    static void main()
    { }
}
