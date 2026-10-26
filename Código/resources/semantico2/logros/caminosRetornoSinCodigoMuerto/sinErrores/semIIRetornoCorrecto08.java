///[SinErrores]
// Un bucle con return adentro puede no ejecutarse nunca, asi que el return de
// despues es alcanzable y cierra el camino que faltaba. Vale para el while con
// condicion no constante y para el for-each.

class Lista<E> implements MiniIterable<E> {
    void start() { }
    E next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    int m1(boolean b) {
        while (b) {
            return 1;
        }
        return 2;
    }

    int m2(Lista<String> l) {
        for (String s : l) {
            return 1;
        }
        return 2;
    }
}

class Init{
    static void main()
    { }
}
