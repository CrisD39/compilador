///[Error:secreto|13]
///[Error:privM|14]
// Miembros private de la superclase, en dos sentencias: la clase actual no
// es la declarante (ERR_ACCESO_PRIVADO, sobre el nombre del miembro).

class A1 {
    private int secreto;
    private void privM() { }
}

class B2 extends A1 {
    void m1() {
        var a = super.secreto;
        super.privM();
    }
}

class Init{
    static void main()
    { }
}
