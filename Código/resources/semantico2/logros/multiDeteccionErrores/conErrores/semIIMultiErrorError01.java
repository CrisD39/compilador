///[Error:y|10]
// Dos errores en sentencias consecutivas: tiene que reportar ambos. Esta
// primera linea fija el 1ro; el 2do es [Error:z|11] (a1 = z, z no
// declarada).

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
