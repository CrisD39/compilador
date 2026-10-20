///[Error:?|8]
// Las ramas del ternario tienen tipos incompatibles (int y String).

class A1 {
    int a1;

    void m1() {
        a1 = true ? 1 : "hola";
    }
}

class Init{
    static void main()
    { }
}
