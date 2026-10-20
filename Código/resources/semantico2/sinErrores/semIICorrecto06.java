///[SinErrores]
// §10.1 -- Una local puede ocultar un atributo; this.nombre selecciona el atributo.

class A1 {
    int v;

    void m1() {
        var v = true;
        this.v = 3;
        v = false;
    }
}

class Init{
    static void main()
    { }
}
