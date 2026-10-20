///[SinErrores]
// Genericos anidados y diamante: el argumento de tipo se infiere del lado
// izquierdo.

class Caja<T> {
    T v;
}

class A1 {
    void m1() {
        Caja<Caja<String>> c = new Caja<>();
    }
}

class Init{
    static void main()
    { }
}
