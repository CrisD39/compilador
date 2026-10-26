///[SinErrores]
// Los metodos void y los constructores no tienen que retornar un valor: el
// cuerpo puede estar vacio, no tener return, terminar en return; o tener un
// return; solo adentro de un if.

class A1 {
    int a1;

    public A1() { }

    void m1() { }

    void m2() {
        a1 = 1;
    }

    void m3() {
        a1 = 2;
        return;
    }

    void m4(boolean b) {
        if (b) return;
        a1 = 3;
    }
}

class A2 {
    int a2;

    public A2(boolean b) {
        if (b) {
            return;
        }
        a2 = 1;
        return;
    }
}

class Init{
    static void main()
    { }
}
