///[Error:for|9]
// A diferencia de Java, un arreglo no se recorre: no conforma con
// MiniIterable (ERR_FOREACH_NO_ITERABLE, sobre el for).

class A1 {
    int a1;

    void m1(int[] numeros) {
        for (int x : numeros) {
            a1 = x;
        }
    }
}

class Init{
    static void main()
    { }
}
