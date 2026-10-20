///[Error:var|7]
// §12.3 -- Una declaracion local no puede ser cuerpo directo de if.
// Se reporta 'var' en la linea 7.

class A1 {
    void m1(boolean p1) {
        if (p1) var x = 1;
    }
}

class Init{
    static void main()
    { }
}
