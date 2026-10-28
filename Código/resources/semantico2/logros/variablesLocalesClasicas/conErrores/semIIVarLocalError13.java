///[Error:int|8]
// Local clasica inalcanzable (spec 0009, D9, spec 0004): el error va sobre
// su tipo, y la declaracion se chequea sin otro error.

class A1 {
    void m1() {
        return;
        int x = 1;
    }
}

class Init{
    static void main()
    { }
}
