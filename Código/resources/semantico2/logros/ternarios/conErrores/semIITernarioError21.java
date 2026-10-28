///[Error:?|16]
// Anidado con tipo esperado propagado: el A1 del destino baja por el
// parentesis al ternario de adentro, cuya rama 5 no conforma con A1. El
// error va sobre el ? del ternario de adentro (su propia linea), no sobre
// el de afuera.

class A1 {}

class B2 extends A1 {}

class D4 {
    A1 a;

    void m1(boolean c1, boolean c2) {
        a = c1 ? (c2
                    ? new B2() : 5) : null;
    }
}

class Init{
    static void main()
    { }
}
