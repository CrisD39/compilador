///[Error:Lista|16]
// El tipo de la variable sigue las reglas de cualquier tipo escrito en un
// cuerpo: una clase generica sin argumento es un error
// (ERR_TIPO_SIN_ARGUMENTO sobre Lista).

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    Object o;

    void m1(Lista<Lista<String>> filas) {
        for (Lista fila : filas) {
            o = fila;
        }
    }
}

class Init{
    static void main()
    { }
}
