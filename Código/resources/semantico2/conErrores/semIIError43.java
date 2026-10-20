///[Error:=|9]
// §9.3 -- Dentro de un cuerpo chequeado parametricamente, String no conforma con T.
// Se reporta '=' en la linea 9.

class Caja<T> {
    T contenido;

    void m1() {
        contenido = "texto";
    }
}

class Init{
    static void main()
    { }
}
