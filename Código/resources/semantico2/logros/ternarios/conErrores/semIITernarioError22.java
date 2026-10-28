///[Error:?|12]
// Orden en una llamada: los argumentos se tipan todos antes de compararse
// con sus formales. Con m2(String, int), el 1 no conforma con String, pero
// antes el ternario ya falla contra su tipo esperado int (la rama "a"), asi
// que el error informado es el del ? y no ERR_ARGUMENTO_INCOMPATIBLE sobre
// m2.

class A1 {
    void m2(String s, int i) { }

    void m1(boolean c, int x) {
        m2(1, c ? x : "a");
    }
}

class Init{
    static void main()
    { }
}
