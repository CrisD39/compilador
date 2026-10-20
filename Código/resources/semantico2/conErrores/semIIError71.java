///[Error:[|7]
// §11.8 -- Solo se indexa una expresion de tipo arreglo.
// Se reporta '[' en la linea 7.

class A1 {
    void m1(int p1) {
        var v = p1[0];
    }
}

class Init{
    static void main()
    { }
}
