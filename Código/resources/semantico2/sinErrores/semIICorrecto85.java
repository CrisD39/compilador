// CP-SII-51: una llamada que produce valor es correcta como sentencia, incluso encadenada
class Persona {
    int edad;

    int obtenerEdad() {
        return edad;
    }

    static Persona crear() {
        return new Persona();
    }
}

class Uso {
    static int constante() {
        return 9;
    }

    void m1() {
        Persona.crear().obtenerEdad();
        this.constante();
        Uso.constante();
    }
}