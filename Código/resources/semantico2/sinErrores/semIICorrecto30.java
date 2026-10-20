///[SinErrores]
// §12.5 -- return; en metodo void y en constructor; return e con subtipo del retorno
// declarado.

class A1 {
    public A1() {
        return;
    }

    void m1() {
        return;
    }

    A1 m2() {
        return new B2();
    }

    Object m3() {
        return "texto";
    }
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
