///[Error:super|9]
// super() en un metodo de instancia: solo puede ser la primera sentencia de
// un constructor (ERR_SUPER_FUERA_DE_LUGAR, sobre super).

class A1 { }

class B2 extends A1 {
    void m1() {
        super();
    }
}

class Init{
    static void main()
    { }
}
