///[Error:for|10]
// Un postfijo parentizado no es un postfijo (ADR 0013): (i++) en la
// inicializacion es una expresion parentizada, como en Java
// (ERR_FOR_INICIALIZACION, sobre el for).

class A1 {
    int a1;

    void m1(int i) {
        for ((i++); i < 10; i++) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
