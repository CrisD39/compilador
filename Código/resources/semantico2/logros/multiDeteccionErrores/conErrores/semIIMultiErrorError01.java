///[Error:y|10]
///[Error:z|11]
// Dos errores en sentencias consecutivas: tiene que reportar ambos
// (a1 = y y a1 = z, ninguna de las dos variables esta declarada).

class A1 {
    int a1;

    void m1() {
        a1 = y;
        a1 = z;
    }
}

class Init{
    static void main()
    { }
}
