///[Error:=|6]
// El inicializador se chequea antes que el choque (spec 0009, D5): p choca
// con el parametro, pero el error es el del "=".

class A1 {
    void m1(int p) { int p = true; }
}

class Init{
    static void main()
    { }
}
