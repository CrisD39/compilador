///[Error:var|7]
// §12.3 -- Una declaracion local no puede ser cuerpo directo de while.
// Se reporta 'var' en la linea 7.

class A1 {
    void m1(boolean p1) {
        while (p1) var x = 1;
    }
}

class Init{
    static void main()
    { }
}
