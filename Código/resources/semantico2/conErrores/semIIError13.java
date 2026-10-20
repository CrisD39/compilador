///[Error:m9|7]
// §10.2 -- Llamada a un metodo que no existe en la clase actual ni en sus ancestros.
// Se reporta 'm9' en la linea 7.

class A1 {
    void m1() {
        m9();
    }
}

class Init{
    static void main()
    { }
}
