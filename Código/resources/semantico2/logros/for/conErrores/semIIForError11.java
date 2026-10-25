///[Error:for|10]
// Una clase que no implementa MiniIterable no se recorre, aunque tenga los
// mismos metodos (start/next/hasNext): la conformidad es nominal
// (ERR_FOREACH_NO_ITERABLE, sobre el for).

class A1 {
    String s;

    void m1(Parecida p) {
        for (String x : p) {
            s = x;
        }
    }
}

class Parecida {
    void start() { }
    String next() { return null; }
    boolean hasNext() { return false; }
}

class Init{
    static void main()
    { }
}
