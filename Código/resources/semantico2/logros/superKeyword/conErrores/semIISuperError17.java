///[Error:this|15]
///[Error:x|19]
///[Error:x|23]
// Contexto previo a la construccion, en tres constructores
// (ERR_REFERENCIA_ANTES_DE_SUPER, sobre el token del uso): super(this),
// super(x) con x heredado y super((x)), entre parentesis.

class A1 {
    A1 x;
    A1(A1 a) { }
}

class B2 extends A1 {
    B2() {
        super(this);
    }

    B2(int v) {
        super(x);
    }

    B2(int v, int w) {
        super((x));
    }
}

class Init{
    static void main()
    { }
}
