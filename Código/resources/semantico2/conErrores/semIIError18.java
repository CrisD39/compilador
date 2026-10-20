///[Error:m2|12]
// §10.2 -- Clase.m() exige que m sea estatico: un metodo de instancia no se invoca con
// el nombre de la clase.
// Se reporta 'm2' en la linea 12.

class A1 {
    void m2() {}
}

class B2 {
    void m1() {
        A1.m2();
    }
}

class Init{
    static void main()
    { }
}
