///[Error:p1|7]
// §12.3 -- Una local no puede llamarse igual que un parametro de la unidad.
// Se reporta 'p1' en la linea 7.

class A1 {
    void m1(int p1) {
        var p1 = 2;
    }
}

class Init{
    static void main()
    { }
}
