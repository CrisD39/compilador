///[Error:this|10]
// §10.4 -- La restriccion de contexto estatico tambien aplica dentro de parentesis y
// subexpresiones.
// Se reporta 'this' en la linea 10.

class A1 {
    int a1;

    static void m1() {
        var v = (this).a1;
    }
}

class Init{
    static void main()
    { }
}
