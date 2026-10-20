///[Error:[|9]
// §11.8 -- Cada indexacion quita una dimension: v[0] es int y no admite otra
// indexacion.
// Se reporta '[' en la linea 9.

class A1 {
    void m1() {
        var v = new int[10];
        var w = v[0][1];
    }
}

class Init{
    static void main()
    { }
}
