///[Error:super|17]
///[Error:super|27]
///[Error:this|37]
// Orden de los controles de super(...) (spec 0006, D8): aridad, visibilidad,
// argumentos y conformidad. Con this en los argumentos de los tres: si no
// hay constructor de esa aridad gana ERR_CONSTRUCTOR_NO_ENCONTRADO (sobre
// super), si el constructor es private gana ERR_ACCESO_PRIVADO (sobre super),
// y si existe y es visible, this da ERR_REFERENCIA_ANTES_DE_SUPER antes de
// mirar si conforma con el int del parametro.

class A1 {
    A1() { }
}

class B2 extends A1 {
    B2() {
        super(this);
    }
}

class C3 {
    private C3(D4 d) { }
}

class D4 extends C3 {
    D4() {
        super(this);
    }
}

class E5 {
    E5(int v) { }
}

class F6 extends E5 {
    F6() {
        super(this);
    }
}

class Init{
    static void main()
    { }
}
