///[SinErrores]
// Ternarios anidados (spec 0007): a derecha y a izquierda, con condicion
// compuesta (el ?: tiene la menor precedencia), y con el tipo esperado
// propagado a traves de un parentesis sin encadenado y del ternario de
// adentro: en a = c1 ? (c2 ? new B2() : new C2()) : null; las hermanas
// conforman con A1, el tipo del destino.

class A1 {}

class B2 extends A1 {}

class C2 extends A1 {}

class D4 {
    int i;
    A1 a;

    void m1(boolean c1, boolean c2, int x, boolean b) {
        i = c1 ? 1 : c2 ? 2 : 3;
        i = (c1 ? c2 : b) ? 1 : 2;
        i = x > 1 && b ? 1 : 2;
        a = c1 ? (c2 ? new B2() : new C2()) : null;
        a = c1 ? null : c2 ? new B2() : new C2();
        a = (c1 ? (c2 ? new B2() : new C2()) : new A1());
    }

    A1 m2(boolean c1, boolean c2) {
        return c1 ? new C2() : (c2 ? null : new B2());
    }
}

class Init{
    static void main()
    { }
}
