///[Error:?|9]
// char no conforma con int (sin promocion numerica, §9.1), asi que las ramas
// son incompatibles (ERR_TERNARIO_INCOMPATIBLE, sobre el ?).

class A1 {
    int i;

    void m1(boolean c) {
        i = c ? 'a' : 1;
    }
}

class Init{
    static void main()
    { }
}
