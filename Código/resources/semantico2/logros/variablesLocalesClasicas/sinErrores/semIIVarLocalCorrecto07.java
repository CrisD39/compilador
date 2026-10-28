///[SinErrores]
// Sin asignacion definida (spec 0009, D7, ADR 0012): una local sin
// inicializador se lee y se asigna libremente. En Java, return x daria
// "might not have been initialized".

class A1 {
    int m1() {
        int x;
        return x;
    }

    void m2() {
        A1 a;
        a = new A1();
    }
}

class Init{
    static void main()
    { }
}
