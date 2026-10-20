///[Error:Caja|11]
// §5.2 -- Sin tipos raw: crear una entidad generica sin argumento es error.
// Se reporta 'Caja' en la linea 11.

class Caja<T> {
    T contenido;
}

class A1 {
    void m1() {
        var v = new Caja();
    }
}

class Init{
    static void main()
    { }
}
