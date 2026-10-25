///[SinErrores]
// Lo que conforma con MiniIterable a cualquier distancia se recorre: el tipo
// MiniIterable<String> mismo, una interfaz que la extiende (Coleccion<E>),
// una clase que implementa esa interfaz y una subclase de esa clase que fija
// el argumento (Nombres extends Bolsa<String>). La expresion puede ser
// cualquier expresion: una llamada o un new, no solo una variable.

interface Coleccion<E> extends MiniIterable<E> {
    int tamanio();
}

class Bolsa<E> implements Coleccion<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
    int tamanio() { return 0; }
}

class Nombres extends Bolsa<String> { }

class A1 {
    String s;

    void m1(MiniIterable<String> it, Coleccion<String> c, Nombres n) {
        for (String x : it) { s = x; }
        for (String x : c) { s = x; }
        for (String x : n) { s = x; }
        for (String x : crear()) { s = x; }
        for (String x : new Nombres()) { s = x; }
    }

    Bolsa<String> crear() {
        return null;
    }
}

class Init{
    static void main()
    { }
}
