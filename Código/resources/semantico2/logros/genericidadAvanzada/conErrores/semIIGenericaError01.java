///[Error:=|10]
// Caja<A1> no conforma con Caja<String> (invarianza, sin comodines).

class Caja<T> {
    T v;
}

class A1 {
    void m1() {
        Caja<String> c = new Caja<A1>();
    }
}

class Init{
    static void main()
    { }
}
