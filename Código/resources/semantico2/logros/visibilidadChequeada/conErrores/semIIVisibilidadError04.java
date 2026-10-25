///[Error:A1|11]
// new con un constructor private desde otra clase (ERR_ACCESO_PRIVADO,
// sobre el nombre de la clase, como los demas errores de new).

class A1 {
    private A1() { }
}

class B2 {
    void m1() {
        var a = new A1();
    }
}

class Init{
    static void main()
    { }
}
