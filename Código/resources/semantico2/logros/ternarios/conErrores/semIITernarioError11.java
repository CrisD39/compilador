///[Error:?|9]
// char no conforma con el tipo esperado int (sin promocion numerica, §9.1):
// ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?.

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
