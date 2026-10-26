///[Error:m1|6]
// ERR_FALTA_RETORNO: un while con condicion no constante puede no ejecutarse
// nunca, asi que el return de adentro no cubre el camino con b falso.

class A1 {
    int m1(boolean b) {
        while (b) {
            return 1;
        }
    }
}

class Init{
    static void main()
    { }
}
