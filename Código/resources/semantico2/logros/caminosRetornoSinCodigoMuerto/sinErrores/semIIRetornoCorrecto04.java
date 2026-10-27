///[SinErrores]
// Solo el literal true o false es una condicion constante (spec 0004, D6;
// ADR 0009). Una condicion con operadores no se evalua, aunque su valor se
// conozca: el bucle puede terminar y el return de despues es alcanzable
// (m1 a m5). En Java estos return serian inalcanzables. Con valor false
// pasa lo mismo: !true y 1 > 2 no son constantes, asi que el cuerpo del
// while es alcanzable (m8 y m9; en Java seria inalcanzable). Los parentesis,
// uno o varios, no cambian el literal: (true) y ((true)) si son constantes,
// y m6 y m7 no necesitan return.

class A1 {
    int m1() {
        while (!false) { }
        return 1;
    }

    int m2() {
        while (1 < 2) { }
        return 1;
    }

    int m3() {
        while (true && true) { }
        return 1;
    }

    int m4() {
        while ('a' == 'a') { }
        return 1;
    }

    int m5() {
        for (; "a" == "a"; ) { }
        return 1;
    }

    int m6() {
        while ((true)) { }
    }

    int m7() {
        for (; ((true)); ) { }
    }

    void m8() {
        var x = 0;
        while (!true) { x = 1; }
    }

    void m9() {
        var x = 0;
        while (1 > 2) { x = 1; }
    }
}

class Init{
    static void main()
    { }
}
