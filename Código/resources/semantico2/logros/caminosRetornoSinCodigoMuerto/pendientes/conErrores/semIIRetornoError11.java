///[Error:return|8]
// ERR_SENTENCIA_INALCANZABLE: !false es una expresion constante que vale true,
// asi que el while no termina y el return de despues es inalcanzable.

class A1 {
    int m1() {
        while (!false) { }
        return 1;
    }
}

class Init{
    static void main()
    { }
}
