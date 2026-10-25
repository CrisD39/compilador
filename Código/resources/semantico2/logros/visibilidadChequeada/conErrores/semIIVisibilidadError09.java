///[Error:x|16]
///[Error:m1|17]
///[Error:A1|18]
// Multi-deteccion: un error por sentencia, en el orden del programa
// (atributo leido en una expresion, metodo y constructor).

class A1 {
    private int x;
    private A1() { }
    private void m1() { }
    static A1 crear() { return new A1(); }
}

class B2 {
    void m2(A1 a) {
        var n = a.x + 1;
        a.m1();
        var otra = new A1();
    }
}

class Init{
    static void main()
    { }
}
