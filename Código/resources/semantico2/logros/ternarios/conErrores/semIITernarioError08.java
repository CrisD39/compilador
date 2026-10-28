///[Error:?|15]
// Dos clases hermanas como operando de ==: tampoco hay tipo esperado
// (ERR_TERNARIO_INCOMPATIBLE, sobre el ?, y no sobre ==).

class A1 {}

class B2 extends A1 {}

class C2 extends A1 {}

class D4 {
    boolean b;

    void m1(boolean c) {
        b = (c ? new B2() : new C2()) == null;
    }
}

class Init{
    static void main()
    { }
}
