///[SinErrores]
// La variable del for vive en un scope propio: dos for seguidos pueden
// declarar la misma i, y despues del for se puede volver a declarar i.
// Los for anidados usan nombres distintos y el de adentro ve la i de afuera.

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
        for (var i = 5; i > 0; i = i - 1) {
            for (var j = 0; j < i; j = j + 1) {
                a1 = i + j;
            }
        }
        var i = 3;
        a1 = i;
    }
}

class Init{
    static void main()
    { }
}
