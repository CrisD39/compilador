///[Error:?|18]
// Dos clases hermanas como receptor: sin tipo esperado no se busca un
// supertipo comun (ADR 0011), asi que ninguna rama conforma con la otra
// (ERR_TERNARIO_INCOMPATIBLE, sobre el ?). En Java compila.

class A1 {
    int getN() { return 1; }
}

class B2 extends A1 {}

class C2 extends A1 {}

class D4 {
    int i;

    void m1(boolean c) {
        i = (c ? new B2() : new C2()).getN();
    }
}

class Init{
    static void main()
    { }
}
