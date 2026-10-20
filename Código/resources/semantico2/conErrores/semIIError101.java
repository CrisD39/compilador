///[Error:return|7]
// §12.5 -- El retorno de subtipo invertido: se declara B2 y se devuelve A1.
// Se reporta 'return' en la linea 7.

class A1 {
    B2 m1() {
        return new A1();
    }
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
