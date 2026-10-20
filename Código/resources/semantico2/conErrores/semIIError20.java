///[Error:toString|10]
// §10.2 -- Un receptor de tipo T no instanciado no garantiza miembros de Object:
// contenido.toString() es incorrecto.
// Se reporta 'toString' en la linea 10.

class Caja<T> {
    T contenido;

    void m1() {
        var s = contenido.toString();
    }
}

class Init{
    static void main()
    { }
}
