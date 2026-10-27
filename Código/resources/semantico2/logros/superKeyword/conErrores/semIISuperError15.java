///[Error:super|14]
///[Error:super|24]
// Argumento que no conforma, en dos clases (ERR_ARGUMENTO_INCOMPATIBLE,
// sobre super, que hace de nombre de la clase de new): super(true) con
// A1(int), y super(5) en D4 extends C3<String>, donde C3(T t) se ve como
// C3(String t).

class A1 {
    A1(int v) { }
}

class B2 extends A1 {
    B2() {
        super(true);
    }
}

class C3<T> {
    C3(T t) { }
}

class D4 extends C3<String> {
    D4() {
        super(5);
    }
}

class Init{
    static void main()
    { }
}
