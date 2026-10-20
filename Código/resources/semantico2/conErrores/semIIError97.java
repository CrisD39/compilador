///[Error:=|12]
// §12.4 -- Se chequean todas las ramas aunque no se ejecuten: error dentro del else.
// Se reporta '=' en la linea 12.

class A1 {
    int a1;

    void m1() {
        if (true) {
            a1 = 1;
        } else {
            a1 = false;
        }
    }
}

class Init{
    static void main()
    { }
}
