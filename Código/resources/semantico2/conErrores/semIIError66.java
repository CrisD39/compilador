///[Error:=|9]
// §11.9 -- Una llamada por si sola no es asignable.
// Se reporta '=' en la linea 9.

class A1 {
    int a1;

    void m1() {
        m2() = 3;
    }

    int m2() {
        return a1;
    }
}

class Init{
    static void main()
    { }
}
