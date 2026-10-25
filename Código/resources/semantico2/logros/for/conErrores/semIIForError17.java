///[Error:for|10]
// Un parametro de tipo no conforma con Object (no tiene cotas, spec 0003
// D9): MiniIterable<T> se recorre como T, no como Object
// (ERR_FOREACH_TIPO_INCOMPATIBLE, sobre el for).

class Caja<T> {
    Object o;

    void llenar(MiniIterable<T> it) {
        for (Object x : it) {
            o = x;
        }
    }
}

class Init{
    static void main()
    { }
}
