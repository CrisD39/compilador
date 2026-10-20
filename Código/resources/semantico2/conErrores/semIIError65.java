///[Error:=|7]
// §11.9 -- this por si solo no es asignable.
// Se reporta '=' en la linea 7.

class A1 {
    void m1() {
        this = new A1();
    }
}

class Init{
    static void main()
    { }
}
