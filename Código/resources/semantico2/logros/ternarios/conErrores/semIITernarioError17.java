///[Error:return|7]
// Un return con valor en un metodo void sigue siendo el error de retorno de
// siempre (ERR_RETORNO_INCORRECTO, sobre return), aunque el valor sea un ternario.

class A1 {
    void m1(boolean c) {
        return c ? 1 : 2;
    }
}

class Init{
    static void main()
    { }
}
