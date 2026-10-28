///[SinErrores]
// Herencia sin argumento de una clase generica (ADR 0007): el
// metodo heredado m(T t) conserva su T (spec 0001, D5), y null conforma con
// ella (gemelo de semIIError173).
class A1<T> {
    void m(T t) { }
}
class B2 extends A1 {
    void p() { m(null); }
}

class Init{
    static void main()
    { }
}
