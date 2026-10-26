///[Error:return|11]
// ERR_SENTENCIA_INALCANZABLE: un if/else que retorna en las dos ramas no
// termina normalmente, asi que el return de despues es inalcanzable.

class A1 {
    int m1(boolean b) {
        if (b)
            return 1;
        else
            return 2;
        return 3;
    }
}

class Init{
    static void main()
    { }
}
