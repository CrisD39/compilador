///[SinErrores]
// Solo el literal true o false es una condicion constante (spec 0004, D6;
// ADR 0009). Una condicion con operadores no se evalua, aunque su valor se
// conozca: el bucle puede terminar y el return de despues es alcanzable.
// En Java estos return serian inalcanzables. El parentesis no cambia el
// literal: (true) si es constante y m6 no necesita return.

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
}

class Init{
    static void main()
    { }
}
