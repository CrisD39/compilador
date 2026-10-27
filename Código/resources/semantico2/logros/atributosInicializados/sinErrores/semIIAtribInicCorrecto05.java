///[SinErrores]
// La regla de la referencia adelantada es solo de los inicializadores: el
// constructor y los metodos usan ultimo, declarado al final de la clase.

class A1 {
    A1() {
        ultimo = 1;
    }

    int m1() {
        return ultimo + 1;
    }

    void m2() {
        ultimo = m1();
    }

    int ultimo = 7;
}

class Init{
    static void main()
    { }
}
