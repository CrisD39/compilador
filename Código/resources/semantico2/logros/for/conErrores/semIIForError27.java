///[Error:MiniIterable|5]
// MiniIterable es predefinida, como Object, String y System (ADR 0004):
// redeclararla es ERR_CLASE_DUPLICADA sobre el nombre de la declaracion.

interface MiniIterable<T> {
    T next();
}

class Init{
    static void main()
    { }
}
