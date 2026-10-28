///[Error:?|12]
// Invarianza de los argumentos genericos (como en la asignacion): con tipo
// esperado Caja<Object>, la rama Caja<String> no conforma
// (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?).

class Caja<T> {}

class A1 {
    Caja<Object> co;

    void m1(boolean c) {
        co = c ? new Caja<String>() : null;
    }
}

class Init{
    static void main()
    { }
}
