///[SinErrores]
// Expresiones constantes que valen true (JLS 15.29), no solo el literal: cada
// metodo int termina en un bucle infinito y no tiene falta de retorno.
// 1 + 2 * 3 == 9 se pliega sin precedencia entre binarios, como evalua
// MiniJava: ((1 + 2) * 3) == 9. 999999999 * 3 desborda a negativo, como un
// int de 32 bits (un literal tiene como mucho 9 digitos, REQ-AL-09).

class A1 {
    int m1() {
        while (!false) { }
    }

    int m2() {
        while ((true)) { }
    }

    int m3() {
        while (1 < 2) { }
    }

    int m4() {
        while (true && !false) { }
    }

    int m5() {
        while ('a' == 'a') { }
    }

    int m6() {
        while ("a" == "a") { }
    }

    int m7() {
        while (1 + 2 * 3 == 9) { }
    }

    int m8() {
        for (; 999999999 * 3 < 0; ) { }
    }
}

class Init{
    static void main()
    { }
}
