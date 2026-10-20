///[Error:=|13]
// §9.3 -- Argumentos genericos invariantes: Caja<String> no conforma con Caja<Object>.
// Se reporta '=' en la linea 13.

class Caja<T> {
    T contenido;
}

class A1 {
    Caja<Object> a1;

    void m1(Caja<String> p1) {
        a1 = p1;
    }
}

class Init{
    static void main()
    { }
}
