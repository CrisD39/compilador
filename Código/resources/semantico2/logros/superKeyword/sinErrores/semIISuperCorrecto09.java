///[SinErrores]
// Cadena generica: en B2<T> extends A1<T>, super se tipa como A1<T>, asi que
// super.dato y super.get() son T y super(t) elige A1(T t) con un T. En
// C3 extends B2<String>, super.dato y super.get() (que B2 hereda de A1)
// y super.m1() (que declara B2) son String, y super("hola") elige
// B2(T t) visto como B2(String t).

class A1<T> {
    T dato;
    A1(T t) { dato = t; }
    T get() { return dato; }
}

class B2<T> extends A1<T> {
    B2(T t) {
        super(t);
    }

    T m1() {
        var a = super.dato;
        dato = a;
        return super.get();
    }
}

class C3 extends B2<String> {
    String s;

    C3() {
        super("hola");
    }

    void m2() {
        s = super.dato;
        s = super.get();
        s = super.m1();
    }
}

class Init{
    static void main()
    { }
}
