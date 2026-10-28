///[Error:p|16]
///[Error:q|17]
// Private del abuelo alcanzado con super: la Etapa 3 copia los private a
// las subclases, pero C3 no es la clase declarante (ERR_ACCESO_PRIVADO, sobre
// el nombre del miembro), aunque la superclase directa B2 no lo declare.

class A1 {
    private int p;
    private void q() { }
}

class B2 extends A1 { }

class C3 extends B2 {
    void m1() {
        super.p = 1;
        super.q();
    }
}

class Init{
    static void main()
    { }
}
