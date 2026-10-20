///[SinErrores]
// §11.8 -- Arreglo de una interfaz: crea referencias, no instancias de la interfaz; el
// encadenado continua tras indexar.

interface I1 {
    void m2();
}

class A1 {
    int edad;

    void m1() {
        var figuras = new I1[5];
        var personas = new A1[2];
        personas[0] = new A1();
        personas[0].edad = 3;
        figuras[0].m2();
    }
}

class Init{
    static void main()
    { }
}
