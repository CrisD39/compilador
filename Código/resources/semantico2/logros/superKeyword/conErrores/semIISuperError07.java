///[Error:(|13]
// super(...) es una sentencia y no produce un valor: dentro de una expresion
// super exige un primer eslabon .id (error sintactico, sobre el ( que sigue
// a super).

class A1 {
    A1(int v) { }
}

class B2 extends A1 {
    int x;
    void m1() {
        x = super(1);
    }
}

class Init{
    static void main()
    { }
}
