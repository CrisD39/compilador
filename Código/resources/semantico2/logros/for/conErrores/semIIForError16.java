///[Error:for|15]
// Lista<Lista<String>> recorre Lista<String>, no String: el argumento se
// compara entero (ERR_FOREACH_TIPO_INCOMPATIBLE, sobre el for).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;

    void m1(Lista<Lista<String>> filas) {
        for (String x : filas) {
            s = x;
        }
    }
}

class Init{
    static void main()
    { }
}
