///[Error:x|15]
// En una cadena se controla cada eslabon con su receptor: a.b es publico,
// pero el x de B2 es private (ERR_ACCESO_PRIVADO, sobre el nombre).

class B2 {
    private int x;
}

class A1 {
    public B2 b;
}

class C3 {
    void m1(A1 a) {
        a.b.x = 1;
    }
}

class Init{
    static void main()
    { }
}
