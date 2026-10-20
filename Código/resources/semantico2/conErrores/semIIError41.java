///[Error:return|9]
// §9.3 -- Un T no instanciado no conforma con Object (sin cota implicita).
// Se reporta 'return' en la linea 9.

class Caja<T> {
    T contenido;

    Object m1() {
        return contenido;
    }
}

class Init{
    static void main()
    { }
}
