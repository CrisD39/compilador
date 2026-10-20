///[Error:noExiste|10]
// super.noExiste(): el ancestro no tiene ese metodo.

class A1 {
    void m1() {}
}

class B2 extends A1 {
    void m2() {
        super.noExiste();
    }
}

class Init{
    static void main()
    { }
}
