///[Error:m1|13]
// ERR_FALTA_RETORNO: el for-each siempre puede terminar normalmente (la
// coleccion puede estar vacia), asi que un return solo adentro del cuerpo no
// alcanza.

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    int m1(Lista<String> l) {
        for (String s : l) {
            return 1;
        }
    }
}

class Init{
    static void main()
    { }
}
