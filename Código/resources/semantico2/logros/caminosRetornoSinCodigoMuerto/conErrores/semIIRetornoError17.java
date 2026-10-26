///[Error:{|23]
///[Error:;|28]
///[Error:this|33]
///[Error:A1|38]
///[Error:A1|43]
///[Error:x|48]
///[Error:;|48]
///[Error:if|53]
///[Error:while|58]
///[Error:for|63]
// ERR_SENTENCIA_INALCANZABLE sobre el primer token de cada tipo de sentencia
// (spec 0004, D7), una por metodo despues de un return.
// Bloque: {. Sentencia vacia: ;. Llamadas: this, la clase del new o de la
// llamada estatica, y el nombre dentro de los parentesis. if, while y for.
// (x).m1(); ademas es ERR_SENTENCIA_INVALIDA sobre ; (semIIError82): la
// inalcanzable igual se chequea, asi que sale despues, en la misma linea.

class A1 {
    static void estatico() { }

    void m1() {
        return;
        { }
    }

    void m2() {
        return;
        ;
    }

    void m3() {
        return;
        this.m1();
    }

    void m4() {
        return;
        new A1().m1();
    }

    void m5() {
        return;
        A1.estatico();
    }

    void m6(A1 x) {
        return;
        (x).m1();
    }

    void m7(boolean b) {
        return;
        if (b) { }
    }

    void m8(boolean b) {
        return;
        while (b) { }
    }

    void m9() {
        return;
        for (var i = 0; i < 1; i = i + 1) { }
    }
}

class Init{
    static void main()
    { }
}
