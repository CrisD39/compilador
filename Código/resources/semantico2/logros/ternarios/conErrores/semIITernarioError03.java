///[Error:?|11]
// Una rama void no produce valor (ERR_TERNARIO_RAMA_VOID, sobre el ?), aunque
// la otra sea int como el destino.

class A1 {
    int i;

    void m() {}

    void m1(boolean c) {
        i = c ? m() : 1;
    }
}

class Init{
    static void main()
    { }
}
