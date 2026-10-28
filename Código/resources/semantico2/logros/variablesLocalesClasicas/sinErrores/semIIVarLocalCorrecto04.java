///[SinErrores]
// Tipos de referencia y genericos (spec 0009, D4): una clase generica
// instanciada, String con un segundo nombre sin inicializador y una clase
// inicializada con null.

class A1 {}

class Caja<T> {}

class B2 {
    void m1() {
        Caja<A1> c = new Caja<A1>();
        String s = "a", t;
        A1 a = null;
        t = s;
    }
}

class Init{
    static void main()
    { }
}
