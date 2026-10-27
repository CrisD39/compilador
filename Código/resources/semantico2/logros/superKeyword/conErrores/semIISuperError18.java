///[Error:m|21]
///[Error:super|25]
///[Error:this|29]
// Contexto previo a la construccion, en tres constructores
// (ERR_REFERENCIA_ANTES_DE_SUPER, sobre el token del uso): super(m()) con m
// de instancia y sin receptor, super(super.x), y this anidado en
// super(new C3(this).y).

class A1 {
    int x;
    A1(int v) { }
}

class C3 {
    int y;
    C3(B2 b) { }
}

class B2 extends A1 {
    B2() {
        super(m());
    }

    B2(int v) {
        super(super.x);
    }

    B2(int v, int w) {
        super(new C3(this).y);
    }

    int m() { return 1; }
}

class Init{
    static void main()
    { }
}
