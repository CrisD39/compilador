///[SinErrores]
// La inicializacion puede ser una local clasica, como en Java (spec 0009,
// D8): int i vive en el scope propio del for, como el var.

class A1 {
    int a1;

    void m1() {
        for (int i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
