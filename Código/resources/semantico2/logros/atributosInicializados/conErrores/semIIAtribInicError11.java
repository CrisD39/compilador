///[Error:b|7]
// El postfijo lee su operando: b++ con b posterior es una referencia
// adelantada, igual que en javac (ERR_REFERENCIA_ADELANTADA, sobre b). No es
// el destino directo de un =, asi que no se salva.

class A1 {
    int a = b++;
    int b;
}

class Init{
    static void main()
    { }
}
