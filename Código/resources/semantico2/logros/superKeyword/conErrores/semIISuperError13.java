///[Error:super|10]
// super() en un metodo estatico: gana el error de ubicacion
// (ERR_SUPER_FUERA_DE_LUGAR, sobre super) y no el de contexto estatico,
// porque se controla antes.

class A1 { }

class B2 extends A1 {
    static void m1() {
        super();
    }
}

class Init{
    static void main()
    { }
}
