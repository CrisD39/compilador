///[Error:instanceof|9]
// El tipo a la derecha de instanceof no puede ser generico.

class Caja<T> {}

class A1 {
    void m1() {
        A1 a = new A1();
        boolean b = a instanceof Caja<A1>;
    }
}

class Init{
    static void main()
    { }
}
