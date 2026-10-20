///[Error:return|7]
// §12.5 -- Un return con expresion void es incorrecto incluso en un metodo void.
// Se reporta 'return' en la linea 7.

class A1 {
    void m1() {
        return m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
