///[Error:=|8]
// En una lista, el error va sobre el "=" del declarador que falla (spec
// 0009, D5): el segundo, en su propia linea.

class A1 {
    void m1() {
        int x = 1,
            y = true;
    }
}

class Init{
    static void main()
    { }
}
