///[Error:;|14]
///[Error:=|15]
// El ternario no es una sentencia valida (ERR_SENTENCIA_INVALIDA, sobre el
// ;) ni un destino asignable (ERR_DESTINO_NO_ASIGNABLE, sobre el =).
// Las ramas son int: con ramas void, c ? m() : n(); daria primero
// ERR_TERNARIO_RAMA_VOID, porque la expresion se tipa antes de mirar si es
// una sentencia.

class A1 {
    int a;
    int b;

    void m1(boolean c) {
        c ? a : b;
        (c ? a : b) = 3;
    }
}

class Init{
    static void main()
    { }
}
