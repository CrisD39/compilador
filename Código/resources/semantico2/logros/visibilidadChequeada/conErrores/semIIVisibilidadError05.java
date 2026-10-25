///[Error:x|11]
// Un atributo private no se hereda, como en Java: la subclase no lo puede
// usar implicito (ERR_ACCESO_PRIVADO, sobre el nombre).

class A1 {
    private int x;
}

class B2 extends A1 {
    void m1() {
        x = 1;
    }
}

class Init{
    static void main()
    { }
}
