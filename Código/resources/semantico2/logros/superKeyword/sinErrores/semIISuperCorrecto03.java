///[SinErrores]
// Genericos: en B2 extends A1<String>, super se tipa como A1<String>, asi
// que super.dato y super.get() son String, y el constructor A1(T t) se ve
// como A1(String t) al llamarlo con super("hola").

class A1<T> {
    T dato;

    A1(T t) { dato = t; }

    T get() { return dato; }
}

class B2 extends A1<String> {
    String s;
    String t;

    B2() {
        super("hola");
    }

    void m1() {
        s = super.dato;
        t = super.get();
    }
}

class Init{
    static void main()
    { }
}
