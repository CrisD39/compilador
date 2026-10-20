///[Error:x|8]
// §12.3 -- El inicializador se chequea antes de incorporar la local: var x = x no se ve
// a si misma.
// Se reporta 'x' en la linea 8.

class A1 {
    void m1() {
        var x = x;
    }
}

class Init{
    static void main()
    { }
}
