///[Error:=|12]
// §9.2 -- Un supertipo no conforma con el subtipo (la direccion inversa no se
// permite).
// Se reporta '=' en la linea 12.

class A1 {}

class B2 extends A1 {
    B2 b2;

    void m1() {
        b2 = new A1();
    }
}

class Init{
    static void main()
    { }
}
