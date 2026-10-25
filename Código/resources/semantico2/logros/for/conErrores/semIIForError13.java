///[Error:for|10]
// null conforma con cualquier referencia, pero no es un MiniIterable: no
// tiene un tipo de elemento que recorrer (ERR_FOREACH_NO_ITERABLE, sobre el
// for).

class A1 {
    String s;

    void m1() {
        for (String x : null) {
            s = x;
        }
    }
}

class Init{
    static void main()
    { }
}
