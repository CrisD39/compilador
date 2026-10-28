///[Error:?|10]
// Un T no conforma con Object (regla de semIIError41-43): con el Object del
// destino como tipo esperado, la rama t no conforma
// (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?).

class Caja<T> {
    Object o;

    void m1(boolean c, T t) {
        o = c ? t : new Object();
    }
}

class Init{
    static void main()
    { }
}
