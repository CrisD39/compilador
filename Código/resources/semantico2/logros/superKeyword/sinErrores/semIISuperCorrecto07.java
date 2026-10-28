///[SinErrores]
// Herencia sin argumento de una clase generica (excepcion del ADR 0007): el
// parametro T de A1(T t) y de m(T t) se conserva (spec 0001, D5), y null
// conforma con el: super(null) y super.m(null) no dan error (gemelo de
// semIISuperError21).

class A1<T> {
    A1(T t) { }
    void m(T t) { }
}
class B2 extends A1 {
    B2() { super(null); }
    void p() { super.m(null); }
}

class Init{
    static void main()
    { }
}
