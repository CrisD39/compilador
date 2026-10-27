///[Error:super|11]
// super() elige un constructor private de la superclase
// (ERR_ACCESO_PRIVADO, sobre super).

class A1 {
    private A1() { }
}

class B2 extends A1 {
    B2() {
        super();
    }
}

class Init{
    static void main()
    { }
}
