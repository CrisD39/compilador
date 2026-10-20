///[Error:[|7]
// §11.8 -- El tamano en la creacion debe ser int (no hay conversion char -> int).
// Se reporta '[' en la linea 7.

class A1 {
    void m1() {
        var v = new int['a'];
    }
}

class Init{
    static void main()
    { }
}
