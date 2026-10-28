///[SinErrores]
// Contextos sin tipo esperado (spec 0007, D3): indice, condicion de if y de
// while, y var con genericos iguales. while (c ? true : true) es un bucle
// comun y no una condicion constante (ADR 0009, D5): la asignacion que le
// sigue es alcanzable.

class Caja<T> {}

class A1 {
    int[] v;
    int x;
    Caja<String> cs;

    void m1(boolean c) {
        x = v[c ? 0 : 1];
        v[c ? 1 : 0] = 2;
        if (c ? true : false) {
            x = 1;
        }
        while (c ? true : true) {
            x = 2;
        }
        x = 3;
        var k = c ? new Caja<String>() : new Caja<String>();
        cs = k;
    }
}

class Init{
    static void main()
    { }
}
