///[Error:super|13]
///[Error:super|14]
// Acceso con super en un metodo estatico, en dos sentencias: como this, no
// se puede usar desde un contexto estatico (ERR_SUPER_EN_ESTATICO, sobre super).

class A1 {
    int x;
    void m() { }
}

class B2 extends A1 {
    static void m2() {
        super.x = 1;
        super.m();
    }
}

class Init{
    static void main()
    { }
}
