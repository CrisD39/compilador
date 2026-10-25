///[Error:x|10]
// Dentro de la clase declarante, el receptor tambien tiene que ser de esa
// clase: en Java b.x con b de tipo B2 es error aunque el codigo este en A1,
// porque B2 no hereda el private (ERR_ACCESO_PRIVADO, sobre el nombre).

class A1 {
    private int x;

    void m1(B2 b) {
        b.x = 1;
    }
}

class B2 extends A1 { }

class Init{
    static void main()
    { }
}
