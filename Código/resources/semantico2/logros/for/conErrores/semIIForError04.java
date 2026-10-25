///[Error:for|9]
// Una local clasica (int i = 0) tampoco es un var: el logro pide
// asignacion o declaracion var (ERR_FOR_INICIALIZACION, sobre el for).

class A1 {
    int a1;

    void m1() {
        for (int i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
