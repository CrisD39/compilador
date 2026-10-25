///[Error:=|16]
// El cuerpo se chequea con la variable declarada con el tipo escrito: x es
// String y no se asigna a un int (ERR_ASIGNACION_INCOMPATIBLE sobre el =).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    int a1;

    void m1(Lista<String> l) {
        for (String x : l) {
            a1 = x;
        }
    }
}

class Init{
    static void main()
    { }
}
