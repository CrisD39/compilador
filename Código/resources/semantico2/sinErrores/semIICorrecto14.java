///[SinErrores]
// §5 / 9.3 -- Dentro de la clase generica, T conforma consigo mismo y el cuerpo se chequea
// parametricamente.

class Caja<T> {
    T contenido;

    T obtener() {
        return contenido;
    }

    void copiar(Caja<T> otra) {
        contenido = otra.obtener();
        var v = this;
        v.contenido = contenido;
    }
}

class Init{
    static void main()
    { }
}
