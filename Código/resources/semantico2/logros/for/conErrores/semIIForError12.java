///[Error:for|8]
// Un primitivo no se recorre (ERR_FOREACH_NO_ITERABLE, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (int x : 10) {
            a1 = x;
        }
    }
}

class Init{
    static void main()
    { }
}
