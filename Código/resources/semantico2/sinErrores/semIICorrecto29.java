///[SinErrores]
// §12.4 -- Condiciones boolean en if/else/while, asignacion boolean como condicion,
// cuerpos con bloque y sentencia vacia.

class A1 {
    boolean b1;

    void m1(int p1) {
        if (p1 > 0) {
            var x = 1;
        } else {
            var x = 2;
        }
        while (b1 = false) ;
        if (b1) p1 = 2;
        else p1 = 3;
        ;
    }
}

class Init{
    static void main()
    { }
}
