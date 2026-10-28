///[SinErrores]
// Izquierda a derecha (spec 0009, D5): el inicializador de y ve a x, que se
// declaro antes en la misma sentencia.

class A1 {
    int m1() {
        int x = 1, y = x;
        return y;
    }
}

class Init{
    static void main()
    { }
}
