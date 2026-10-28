///[Error:?|9]
// null con un int: sin boxing, la rama null no conforma con el tipo
// esperado int del destino (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?).

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
