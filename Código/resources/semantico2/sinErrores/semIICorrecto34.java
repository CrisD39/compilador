///[SinErrores]
// §11.3 -- this tiene tipo de la clase actual con su parametro: dentro de Caja<T> es
// Caja<T>.

class Caja<T> {
    T contenido;

    Caja<T> yo() {
        return this;
    }

    Caja<T> c;

    void m1() {
        c = this;
        c.contenido = yo().contenido;
    }
}

class Init{
    static void main()
    { }
}
