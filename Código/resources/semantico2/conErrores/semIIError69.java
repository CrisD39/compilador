///[Error:[|8]
// §11.8 -- El indice debe ser int.
// Se reporta '[' en la linea 8.

class A1 {
    void m1() {
        var v = new int[10];
        var w = v[true];
    }
}

class Init{
    static void main()
    { }
}
