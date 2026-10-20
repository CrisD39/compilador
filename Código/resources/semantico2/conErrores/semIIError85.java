///[Error:v|8]
// §12.3 -- Una local no puede repetirse en el mismo bloque.
// Se reporta 'v' en la linea 8.

class A1 {
    void m1() {
        var v = 1;
        var v = 2;
    }
}

class Init{
    static void main()
    { }
}
