///[SinErrores]
// §10.4 -- Un metodo estatico accede a miembros de instancia mediante un receptor
// explicito.

class Persona {
    int edad;

    int obtenerEdad() {
        return edad;
    }

    static int consultar(Persona persona) {
        persona.edad = 4;
        return persona.obtenerEdad();
    }
}

class Init{
    static void main()
    { }
}
