///[SinErrores]
// Interfaces, como en Java: una clase no final y una interfaz siempre
// pueden tener un subtipo comun (una subclase que la implemente), aunque
// hoy no exista. Dos interfaces tambien. Vale en las dos direcciones.

interface I1 {}
interface I2 {}

class A1 {}

class B2 implements I1 {}

class C3 {
    void m1(A1 a, I1 i, I2 j, B2 b) {
        var r1 = a instanceof I1;
        var r2 = i instanceof A1;
        var r3 = i instanceof I2;
        var r4 = j instanceof B2;
        var r5 = b instanceof I1;
        var r6 = i instanceof B2;
    }
}

class Init{
    static void main()
    { }
}
