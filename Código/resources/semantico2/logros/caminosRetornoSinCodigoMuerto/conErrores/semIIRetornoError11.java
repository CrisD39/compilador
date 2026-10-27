///[Error:m1|11]
///[Error:{|17]
// Solo el literal true o false es una condicion constante (spec 0004, D6;
// ADR 0009), solo o entre cualquier cantidad de parentesis.
// m1, ERR_FALTA_RETORNO: !false no es constante, asi que el while puede
// terminar y m1 llega al final sin retornar. En Java !false es constante y
// m1 compila. m2, ERR_SENTENCIA_INALCANZABLE: ((false)) es el literal false,
// asi que el cuerpo del while es inalcanzable y se reporta sobre su '{'.

class A1 {
    int m1() {
        while (!false) { }
    }

    void m2() {
        var x = 0;
        while ((false)) { x = 1; }
    }
}

class Init{
    static void main()
    { }
}
