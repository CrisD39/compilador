///[SinErrores]
// Condiciones que no son constantes: el bucle puede terminar, asi que el
// return de despues es alcanzable. Solo el literal true o false es una
// condicion constante (spec 0004, D6; ADR 0009), y null == null y
// 1 / 0 == 0 tienen operadores (javac tampoco las trata como constantes).
// El if nunca se trata como constante, ni con if (true): el return 2 es
// alcanzable.

class A1 {
    int m1(boolean b) {
        while (b) { }
        return 1;
    }

    int m2() {
        while (null == null) { }
        return 1;
    }

    int m3() {
        while (1 / 0 == 0) { }
        return 1;
    }

    int m4() {
        if (true) return 1;
        return 2;
    }
}

class Init{
    static void main()
    { }
}
