///[Error:m1|6]
// ERR_FALTA_RETORNO: el if no se trata como constante, ni con if (true), asi
// que el if sin else puede terminar normalmente (igual que en Java).

class A1 {
    int m1() {
        if (true) return 1;
    }
}

class Init{
    static void main()
    { }
}
