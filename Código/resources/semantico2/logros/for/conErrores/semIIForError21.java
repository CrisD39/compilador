///[Error:x|17]
// En for-each anidados, el de adentro no puede repetir la variable del de
// afuera, que sigue visible en su cuerpo (ERR_LOCAL_DUPLICADA sobre la x
// de adentro).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;

    void m1(Lista<String> l) {
        for (String x : l) {
            for (String x : l) {
                s = x;
            }
        }
    }
}

class Init{
    static void main()
    { }
}
