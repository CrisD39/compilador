///[Error:x|15]
// La variable del for-each sigue la regla de ocultamiento: no puede repetir
// el nombre de un parametro (ERR_LOCAL_DUPLICADA sobre x).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;

    void m1(Lista<String> l, String x) {
        for (String x : l) {
            s = x;
        }
    }
}

class Init{
    static void main()
    { }
}
