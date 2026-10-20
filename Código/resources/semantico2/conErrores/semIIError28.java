///[Error:Caja|16]
// §11.7 -- El argumento del constructor se chequea con la sustitucion generica: en
// Caja<String>, T es String.
// Se reporta 'Caja' en la linea 16.

class Caja<T> {
    T contenido;

    public Caja(T p1) {
        contenido = p1;
    }
}

class A1 {
    void m1() {
        var v = new Caja<String>(5);
    }
}

class Init{
    static void main()
    { }
}
