///[Error:?|9]
// null con un int: sin boxing, ninguna rama conforma con la otra
// (ERR_TERNARIO_INCOMPATIBLE, sobre el ?), aunque el destino sea int.

class A1 {
    int i;

    void m1(boolean c) {
        i = c ? 1 : null;
    }
}

class Init{
    static void main()
    { }
}
