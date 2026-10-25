///[SinErrores]
// La variable del for-each vive en un scope propio que cubre solo el cuerpo:
// dos for-each seguidos pueden usar el mismo nombre y despues se puede volver
// a declarar. Puede ocultar un atributo (como cualquier local). Los anidados
// usan nombres distintos y el de adentro ve la variable del de afuera; un
// for-each tambien puede ir dentro de un for clasico.

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    String s;
    int a1;

    void m1(Lista<String> l1, Lista<Lista<String>> l2) {
        for (String x : l1) { s = x; }
        for (String x : l1) { s = x; }
        var x = 3;
        a1 = x;
        for (String s : l1) {
            s = "oculta el atributo";
        }
        for (Lista<String> fila : l2) {
            for (String celda : fila) {
                s = celda;
            }
        }
        for (var i = 0; i < 3; i = i + 1) {
            for (String y : l1) {
                a1 = i;
            }
        }
    }
}

class Init{
    static void main()
    { }
}
