///[SinErrores]
// Ternario como argumento (spec 0007, D3): cada argumento tiene como tipo
// esperado su parametro formal, visto desde el receptor. B2 y C2 son
// hermanas que extienden A1, asi que valen contra un parametro A1 en una
// llamada de instancia, estatica y encadenada, en new D4(...) y en
// super(...). Con cs de tipo Caja<String>, el formal T de poner se ve como
// String y cada rama ("a" y null) se compara con String. Sin tipo esperado
// las hermanas serian error (ADR 0011).

class A1 {}

class B2 extends A1 {}

class C2 extends A1 {}

class Caja<T> {
    void poner(T t) { }
}

class D4 {
    D4(A1 a) { }

    void usar(A1 a) { }

    static void usarEstatico(A1 a) { }

    void m1(boolean c, D4 d, Caja<String> cs) {
        usar(c ? new B2() : new C2());
        D4.usarEstatico(c ? new C2() : new B2());
        d.usar(c ? new B2() : new C2());
        this.usar((c ? new B2() : new C2()));
        var n = new D4(c ? new B2() : new C2());
        cs.poner(c ? "a" : null);
    }
}

class E5 extends D4 {
    E5(boolean c) {
        super(c ? new B2() : new C2());
    }
}

class Init{
    static void main()
    { }
}
