///[Error:?|12]
///[Error:?|13]
// Multi-deteccion: dos sentencias con ternarios mal tipados dan dos
// errores, en orden (ERR_TERNARIO_INCOMPATIBLE y ERR_TERNARIO_RAMA_VOID).

class A1 {
    int i;

    void m() {}

    void m1(boolean c) {
        i = c ? 1 : "a";
        i = c ? 2 : m();
    }
}

class Init{
    static void main()
    { }
}
