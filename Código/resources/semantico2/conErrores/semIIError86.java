///[Error:v|9]
// §12.3 -- Una local no puede ocultar a una local todavia visible de un contenedor.
// Se reporta 'v' en la linea 9.

class A1 {
    void m1() {
        var v = 1;
        {
            var v = 2;
        }
    }
}

class Init{
    static void main()
    { }
}
