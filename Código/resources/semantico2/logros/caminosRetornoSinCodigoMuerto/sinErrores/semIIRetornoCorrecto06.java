///[SinErrores]
// if (false) no es un error: el if no se trata como constante (JLS 14.22), asi
// que su rama no es inalcanzable.

class A1 {
    int a1;

    void m1() {
        if (false) {
            a1 = 1;
        }
    }

    int m2() {
        if (false) {
            a1 = 2;
        }
        return a1;
    }
}

class Init{
    static void main()
    { }
}
