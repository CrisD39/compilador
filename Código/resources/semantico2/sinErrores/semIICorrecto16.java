///[SinErrores]
// §11.8 -- Arreglos con tipo base parametrico y genericos instanciados en un contexto
// de instancia.

class Caja<T> {
    T contenido;

    void m1() {
        var elementos = new T[10];
        var cajas = new Caja<String>[10];
        var matriz = new Caja<T>[3][4];
        elementos[0] = contenido;
        matriz[0][1] = this;
    }
}

class Init{
    static void main()
    { }
}
