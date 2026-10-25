///[SinErrores]
// El elemento puede ser un parametro de tipo: dentro de Lista<E> se recorre
// this como E, y dentro de Caja<T> un MiniIterable<T> se recorre como T.
// (Como Object no: T no conforma con Object, spec 0003 D9; ver
// semIIForError17.)

class Lista<E> implements MiniIterable<E> {
    E ultimo;

    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }

    void recorrer() {
        for (E x : this) {
            ultimo = x;
        }
    }
}

class Caja<T> {
    T contenido;

    void llenar(MiniIterable<T> it) {
        for (T x : it) {
            contenido = x;
        }
    }
}

class Init{
    static void main()
    { }
}
