///[Error:=|9]
// §9.1 -- Sin conversion implicita char -> int.
// Se reporta '=' en la linea 9.

class A1 {
    int a1;

    void m1() {
        a1 = 'c';
    }
}

class Init{
    static void main()
    { }
}
