///[Error:=|9]
// §9.3 -- Object no conforma con un T no instanciado.
// Se reporta '=' en la linea 9.

class Caja<T> {
    T contenido;

    void m1(Object p1) {
        contenido = p1;
    }
}

class Init{
    static void main()
    { }
}
