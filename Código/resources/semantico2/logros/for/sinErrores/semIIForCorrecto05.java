///[SinErrores]
// for-each minimo: una clase no generica que implementa MiniIterable<Persona>
// se recorre con una variable Persona, que se usa en el cuerpo. MiniIterable
// es predefinida: se implementa sin declararla.

class Persona {
    int edad;
}

class Grupo implements MiniIterable<Persona> {
    void start() { }
    Persona next() { return null; }
    boolean hasNext() { return false; }
}

class A1 {
    int a1;

    void m1(Grupo g) {
        for (Persona p : g) {
            a1 = p.edad;
        }
    }
}

class Init{
    static void main()
    { }
}
