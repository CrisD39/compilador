///[Error:var|8]
// §12.3 -- Una declaracion local no puede ser cuerpo directo de else.
// Se reporta 'var' en la linea 8.

class A1 {
    void m1(boolean p1) {
        if (p1) { }
        else var x = 1;
    }
}

class Init{
    static void main()
    { }
}
