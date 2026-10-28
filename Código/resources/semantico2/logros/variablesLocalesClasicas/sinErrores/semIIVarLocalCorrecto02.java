///[SinErrores]
// Declaradores mezclados (spec 0009, D2 y D5): cada nombre tiene su propio
// inicializador opcional, en cualquier orden, como en Java (JLS §14.4).
// Despues se usan todas, con el tipo declarado.

class A1 {
    int m1() {
        int x, y = 2, z;
        int a = 1, b = 2;
        x = y + a;
        z = b;
        return x + z;
    }
}

class Init{
    static void main()
    { }
}
