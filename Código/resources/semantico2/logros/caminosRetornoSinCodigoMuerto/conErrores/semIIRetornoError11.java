///[Error:m1|7]
// ERR_FALTA_RETORNO: !false no es una condicion constante (spec 0004, D6;
// ADR 0009), asi que el while puede terminar y m1 llega al final sin
// retornar. En Java !false es constante y este metodo compila.

class A1 {
    int m1() {
        while (!false) { }
    }
}

class Init{
    static void main()
    { }
}
