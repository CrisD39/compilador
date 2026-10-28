///[Error:super|14]
///[Error:super|25]
// Sin constructor de esa aridad (ERR_CONSTRUCTOR_NO_ENCONTRADO, sobre super):
// super() con A1 que solo declara A1(int), porque el constructor por defecto
// existe solo si la clase no declara ninguno, y super(1, 2) con C3 que
// tiene C3() y C3(int), sobrecargados.

class A1 {
    A1(int v) { }
}

class B2 extends A1 {
    B2() {
        super();
    }
}

class C3 {
    C3() { }
    C3(int v) { }
}

class D4 extends C3 {
    D4() {
        super(1, 2);
    }
}

class Init{
    static void main()
    { }
}
