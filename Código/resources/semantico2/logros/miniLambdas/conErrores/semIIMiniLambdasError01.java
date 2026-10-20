///[Error:this|8]
// No se permite usar this dentro de una Lambda.

class A1 {
    int a1;

    void m1() {
        var f = () -> this;
    }
}

class Init{
    static void main()
    { }
}
