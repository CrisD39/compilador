///[SinErrores]
// §11.5 / 11.9 -- Destinos asignables: locales, parametros, atributos encadenados tras llamada
// y componentes.

class Persona {
    int edad;
    Persona amigo;

    Persona crearPersona() {
        return new Persona();
    }

    void m1(int p1, Persona[] personas) {
        var x = 1;
        x = 2;
        p1 = x;
        edad = p1;
        crearPersona().edad = 3;
        amigo.amigo.edad = 4;
        personas[0] = amigo;
        personas[0].edad = 5;
    }
}

class Init{
    static void main()
    { }
}
