///[SinErrores]
// Un bucle cuya condicion es la constante true no termina normalmente, asi que
// un metodo int que termina en el no tiene falta de retorno. El for sin
// condicion cuenta como true, tenga o no inicializacion y actualizacion.

class A1 {
    int m1() {
        while (true) { }
    }

    int m2() {
        for (;;) { }
    }

    int m3() {
        for (var i = 0; ; i = i + 1) { }
    }
}

class Init{
    static void main()
    { }
}
