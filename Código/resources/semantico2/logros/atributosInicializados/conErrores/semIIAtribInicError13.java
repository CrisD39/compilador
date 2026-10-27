///[Error:b|9]
///[Error:v|10]
// Solo el destino directo de un = se salva de la referencia adelantada. En
// (c = b), c es el destino y no se marca, pero b se lee. En (v[0] = 1) el
// destino es el componente, no v: v se lee para indexarlo
// (ERR_REFERENCIA_ADELANTADA, sobre b y sobre v).

class A1 {
    int a = (c = b);
    int d = (v[0] = 1);
    int b;
    int c;
    int[] v;
}

class Init{
    static void main()
    { }
}
