///[Error:;|7]
// Una prueba de tipo no es una sentencia valida por si sola
// (ERR_SENTENCIA_INVALIDA, sobre ;).

class A1 {
    void m1(A1 a) {
        a instanceof A1;
    }
}

class Init{
    static void main()
    { }
}
