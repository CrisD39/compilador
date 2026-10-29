///[Error:for|9]
// Solo cuenta la raiz de la actualizacion (ADR 0013): i++ + 1 es una suma, no
// un postfijo, como en Java (ERR_FOR_ACTUALIZACION, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i++ + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
