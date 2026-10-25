///[Error:j|9]
// La inicializacion es un var, pero su valor se chequea: j no existe
// (ERR_VAR_NO_DECLARADA sobre j).

class A1 {
    int a1;

    void m1() {
        for (var i = j; i < 10; i = i + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
