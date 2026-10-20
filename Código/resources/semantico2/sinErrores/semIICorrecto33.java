///[SinErrores]
// §5 -- Herencia sin argumento conserva T: el metodo heredado sigue retornando T (no
// Object).

class Base<T> {
    T obtener() {
        return null;
    }
}

class Derivada extends Base {
    void m1() {
        var v = obtener();
        v = null;
    }
}

class Init{
    static void main()
    { }
}
