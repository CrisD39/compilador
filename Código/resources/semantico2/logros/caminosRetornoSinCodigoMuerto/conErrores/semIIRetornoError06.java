///[Error:m1|6]
// ERR_FALTA_RETORNO: if/else anidados en los que una rama interna (c falso)
// no retorna; el if/else de afuera sale vivo por esa rama.

class A1 {
    int m1(boolean b, boolean c) {
        if (b) {
            if (c) return 1;
        } else {
            return 2;
        }
    }
}

class Init{
    static void main()
    { }
}
