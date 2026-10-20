///[Error:T|8]
// §10.4 -- El parametro de tipo de la clase no puede usarse en el cuerpo de un metodo
// estatico.
// Se reporta 'T' en la linea 8.

class Caja<T> {
    static void m1() {
        var datos = new T[10];
    }
}

class Init{
    static void main()
    { }
}
