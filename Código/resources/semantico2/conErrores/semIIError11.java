///[Error:dato|10]
// §10.1 -- Un parametro de tipo no instanciado (T) no permite seleccionar atributos por
// punto.
// Se reporta 'dato' en la linea 10.

class Caja<T> {
    T contenido;

    void m1() {
        var v = contenido.dato;
    }
}

class Init{
    static void main()
    { }
}
