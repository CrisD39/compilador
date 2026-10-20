///[Error:=|9]
// §9.1 -- Sin conversion entre referencias y primitivos: String no conforma con int.
// Se reporta '=' en la linea 9.

class A1 {
    int a1;

    void m1() {
        a1 = "texto";
    }
}

class Init{
    static void main()
    { }
}
