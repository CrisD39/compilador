///[SinErrores]
// La marca del contexto previo a la construccion se apaga al terminar
// super(...): despues, el constructor usa el objeto actual normalmente
// (atributo propio y heredado por su nombre suelto, this, metodos de
// instancia sin receptor y super.m()). Ademas, en los argumentos valen una
// llamada estatica con receptor de la clase actual (B2.s()) y una llamada a
// un metodo de instancia de otro objeto (new P3().n()).

class A1 {
    int x;
    A1(int v) { }
    void m() { }
}

class P3 {
    int n() { return 2; }
}

class B2 extends A1 {
    int y;

    B2(int v) {
        super(v + B2.s() + new P3().n());
        y = x;
        this.y = y + 1;
        m();
        super.m();
        r();
    }

    B2() {
        super(new P3().n());
        var b = this;
        b.r();
    }

    void r() { }

    static int s() { return 3; }
}

class Init{
    static void main()
    { }
}
