///[SinErrores]
// §12.2 -- Sentencias de llamada validas: void, con valor descartado, creacion de
// objeto, encadenada terminando en llamada.

class Persona {
    int edad;

    int obtenerEdad() {
        return edad;
    }

    Persona crearPersona() {
        return new Persona();
    }

    void m1(Persona persona) {
        persona.obtenerEdad();
        new Persona();
        crearPersona().obtenerEdad();
        m2();
    }

    void m2() {}
}

class Init{
    static void main()
    { }
}
