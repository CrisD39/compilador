///[SinErrores]
// §10.2 -- Un estatico se invoca con Clase.m() o con una expresion receptora; un
// estatico llama a otro sin receptor.

class Persona {
    static int consultar(int p1) {
        return p1;
    }

    static int doble(int p1) {
        return consultar(p1) + consultar(p1);
    }

    void m1(Persona persona) {
        var a = Persona.consultar(1);
        var b = persona.consultar(2);
    }
}

class Init{
    static void main()
    { }
}
