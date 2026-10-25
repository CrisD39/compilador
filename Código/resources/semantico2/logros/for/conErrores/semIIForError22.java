///[Error:var|16]
// Una declaracion local no puede ser el cuerpo directo del for-each, igual
// que en if/while/for (ERR_DECLARACION_COMO_CUERPO sobre el var).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;

    void m1(Lista<String> l) {
        for (String x : l)
            var y = x;
    }
}

class Init{
    static void main()
    { }
}
