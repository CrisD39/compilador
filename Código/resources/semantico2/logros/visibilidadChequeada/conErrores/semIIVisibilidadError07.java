///[Error:x|11]
// Ni con this desde la subclase: el receptor es un B2
// (ERR_ACCESO_PRIVADO, sobre el nombre).

class A1 {
    private int x;
}

class B2 extends A1 {
    void m1() {
        this.x = 1;
    }
}

class Init{
    static void main()
    { }
}
