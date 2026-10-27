///[Error:super|13]
///[Error:super|19]
// Sin constructor de esa aridad en la superclase, en dos clases
// (ERR_CONSTRUCTOR_NO_ENCONTRADO, sobre super): super(1) con A1 que solo
// tiene A1(), y super(1) sin extends (Object solo tiene el sin parametros).

class A1 {
    A1() { }
}

class B2 extends A1 {
    B2() {
        super(1);
    }
}

class C3 {
    C3() {
        super(1);
    }
}

class Init{
    static void main()
    { }
}
