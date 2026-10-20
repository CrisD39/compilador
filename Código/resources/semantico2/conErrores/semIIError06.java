///[Error:x|8]
// §10.1 -- Una local solo es visible despues de su declaracion: x se usa una linea
// antes de declararse.
// Se reporta 'x' en la linea 8.

class A1 {
    void m1() {
        var y = x;
        var x = 1;
    }
}

class Init{
    static void main()
    { }
}
