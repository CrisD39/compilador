///[Error:instanceof|9]
// Tampoco una clase generica usada sin argumento (raw): el logro pide una
// clase o interfaz no generica (ERR_INSTANCEOF_GENERICO, sobre instanceof).

class Caja<T> {}

class A1 {
    void m1(Object o) {
        var b = o instanceof Caja;
    }
}

class Init{
    static void main()
    { }
}
