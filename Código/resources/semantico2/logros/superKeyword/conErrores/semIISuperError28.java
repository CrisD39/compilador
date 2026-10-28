///[Error:y|21]
///[Error:this|25]
///[Error:m|29]
// Contexto previo a la construccion (ERR_REFERENCIA_ANTES_DE_SUPER, sobre el
// token del uso): super(y) con y atributo propio (no heredado), super(this.y)
// y super(p.n() + m()), donde p.n() vale (miembro de otro objeto) pero m() es
// un metodo de instancia sin receptor.

class A1 {
    A1(int v) { }
}

class P3 {
    int n() { return 1; }
}

class B2 extends A1 {
    int y;

    B2() {
        super(y);
    }

    B2(int v) {
        super(this.y);
    }

    B2(P3 p, int v) {
        super(p.n() + m());
    }

    int m() { return 1; }
}

class Init{
    static void main()
    { }
}
