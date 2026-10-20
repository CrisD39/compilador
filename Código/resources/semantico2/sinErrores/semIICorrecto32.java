///[SinErrores]
// §12.3 -- var toma exactamente el tipo del inicializador, conservando argumento
// generico y dimensiones.

class Caja<T> {
    T contenido;
}

class A1 {
    Caja<String> b;
    int[][] d;
    char f;
    String h;

    void m1(Caja<String> p1) {
        var a = p1;
        b = a;
        var c = new int[2][2];
        d = c;
        var e = 'z';
        f = e;
        var g = "s";
        h = g;
    }
}

class Init{
    static void main()
    { }
}
