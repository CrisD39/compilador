///[SinErrores]
// Lo que vale en los argumentos de super(...) (contexto previo a la
// construccion): un parametro con el nombre de un atributo heredado (el
// nombre resuelve al parametro), literales, A1.m(), un metodo estatico
// propio sin receptor, new de la clase actual y miembros de otro objeto
// (p.x, p.m()).

class A1 {
    int x;

    A1(int v) { }

    static int m() { return 1; }
}

class P3 {
    int x;

    int m() { return 2; }
}

class B2 extends A1 {
    B2(int x) {
        super(x);
    }

    B2() {
        super(5);
    }

    B2(int a, int b) {
        super(A1.m() + s());
    }

    B2(P3 p, int a, int b) {
        super(p.x + p.m());
    }

    B2(int a, int b, int c, int d) {
        super(new B2(a).x);
    }

    static int s() { return 3; }
}

class Init{
    static void main()
    { }
}
