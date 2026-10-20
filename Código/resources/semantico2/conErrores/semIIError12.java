///[Error:inexistente|9]
// §10.1 -- Acceso a atributo inexistente en una clase existente.
// Se reporta 'inexistente' en la linea 9.

class A1 {
    int a1;

    void m1(A1 p1) {
        p1.inexistente = 2;
    }
}

class Init{
    static void main()
    { }
}
