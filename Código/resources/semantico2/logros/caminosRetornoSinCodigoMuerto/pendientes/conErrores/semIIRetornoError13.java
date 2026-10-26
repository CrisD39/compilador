///[Error:x|11]
// La recuperacion tapa la falta de retorno: el cuerpo sale "recuperando", no
// vivo, asi que solo se reporta ERR_SENTENCIA_INALCANZABLE y no un
// ERR_FALTA_RETORNO en cascada sobre m1.

class A1 {
    int x;

    int m1() {
        return 1;
        x = 2;
    }
}

class Init{
    static void main()
    { }
}
