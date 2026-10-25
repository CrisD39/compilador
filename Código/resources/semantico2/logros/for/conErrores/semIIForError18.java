///[Error:x|18]
// La variable del for-each no se ve despues del for: su scope cubre solo el
// cuerpo (ERR_VAR_NO_DECLARADA sobre x).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;

    void m1(Lista<String> l) {
        for (String x : l) {
            s = x;
        }
        s = x;
    }
}

class Init{
    static void main()
    { }
}
