///[Error:l|16]
// La variable del for-each tampoco se ve en su propia expresion: se declara
// despues de chequearla, como en Java (ERR_VAR_NO_DECLARADA sobre la l de
// la expresion).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    Object o;

    void m1() {
        for (Lista<String> l : l) {
            o = l;
        }
    }
}

class Init{
    static void main()
    { }
}
