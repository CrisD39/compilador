///[Error:this|7]
// §10.4 -- this no es valido en un metodo estatico.
// Se reporta 'this' en la linea 7.

class A1 {
    static void m1() {
        var v = this;
    }
}

class Init{
    static void main()
    { }
}
