///[Error:?|12]
// Rama incompatible en new: con D4(int), el tipo esperado del argumento es
// int y la rama "a" no conforma (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el
// ?), sin un ERR_ARGUMENTO_INCOMPATIBLE sobre D4.

class D4 {
    D4(int i) { }
}

class A1 {
    void m1(boolean c) {
        var d = new D4(c ? 1 : "a");
    }
}

class Init{
    static void main()
    { }
}
