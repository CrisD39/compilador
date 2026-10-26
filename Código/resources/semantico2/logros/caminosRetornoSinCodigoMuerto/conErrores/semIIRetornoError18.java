///[Error:m1|8]
///[Error:return|9]
// ERR_FALTA_RETORNO sale antes que los errores del cuerpo (spec 0004, D4):
// esta sobre el nombre del metodo, que va antes en el programa. Despues,
// ERR_RETORNO_INCORRECTO porque true no conforma con int.

class A1 {
    int m1(boolean b) {
        if (b) return true;
    }
}

class Init{
    static void main()
    { }
}
