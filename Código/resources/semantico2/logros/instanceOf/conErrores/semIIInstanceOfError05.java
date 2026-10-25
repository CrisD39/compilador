///[Error:instanceof|11]
// Dos clases hermanas no tienen relacion: la prueba nunca puede dar true,
// y Java la rechaza (ERR_OPERADOR_INCOMPATIBLE, sobre instanceof).

class A1 {}
class B2 extends A1 {}
class C3 extends A1 {}

class D4 {
    void m1(B2 b) {
        var r = b instanceof C3;
    }
}

class Init{
    static void main()
    { }
}
