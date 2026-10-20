///[Error:<|7]
// §11.2 -- Los relacionales exigen int, int: dos char no se comparan con <.
// Se reporta '<' en la linea 7.

class A1 {
    void m1(char p1, char p2) {
        var v = p1 < p2;
    }
}

class Init{
    static void main()
    { }
}
