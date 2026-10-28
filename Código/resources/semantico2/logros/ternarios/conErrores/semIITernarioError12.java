///[Error:?|10]
// Un T no conforma con Object (regla de semIIError41-43), y un Object no
// conforma con T: las ramas son incompatibles (ERR_TERNARIO_INCOMPATIBLE,
// sobre el ?).

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
