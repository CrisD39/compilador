///[Error:->|12]
// Una lambda en los argumentos de super(...) sigue sin soportarse
// (ERR_NO_SOPORTADO, sobre ->; spec 0006, D10).

class A1 {
    A1(Object o) { }
}

class B2 extends A1 {
    B2() {
        super(
            x -> x);
    }
}

class Init{
    static void main()
    { }
}
