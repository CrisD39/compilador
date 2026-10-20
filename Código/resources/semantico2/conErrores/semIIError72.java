///[Error:[|10]
// §11.8 -- Un arreglo con tipo estatico Object no permite indexacion.
// Se reporta '[' en la linea 10.

class A1 {
    Object o;

    void m1() {
        o = new int[3];
        var v = o[0];
    }
}

class Init{
    static void main()
    { }
}
