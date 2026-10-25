///[Error:instanceof|10]
// El tipo a la derecha de instanceof no puede ser generico
// (ERR_INSTANCEOF_GENERICO, sobre instanceof).

class Caja<T> {}

class A1 {
    void m1() {
        var a = new A1();
        var b = a instanceof Caja<A1>;
    }
}

class Init{
    static void main()
    { }
}
