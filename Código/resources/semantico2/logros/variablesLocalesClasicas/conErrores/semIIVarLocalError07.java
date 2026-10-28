///[Error:Caja|8]
// Generico sin argumentos (spec 0009, D4, ADR 0007): el error va sobre el
// token del tipo.

class Caja<T> {}

class A1 {
    void m1() { Caja c; }
}

class Init{
    static void main()
    { }
}
