///[Error:campo|7]
// §10.1 -- Un primitivo no permite seleccionar atributos por punto.
// Se reporta 'campo' en la linea 7.

class A1 {
    void m1(int p1) {
        var v = p1.campo;
    }
}

class Init{
    static void main()
    { }
}
