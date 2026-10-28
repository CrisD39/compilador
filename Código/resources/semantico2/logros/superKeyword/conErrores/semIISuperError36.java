///[Error:this|16]
///[Error:super|17]
///[Error:super|21]
///[Error:noExiste|22]
// Multi-deteccion con super(...): en un constructor, la primera llamada esta
// en lugar aunque sus argumentos fallen (ERR_REFERENCIA_ANTES_DE_SUPER, sobre
// this) y la segunda queda fuera de lugar; en un metodo, super() fuera de
// lugar y despues super.noExiste() (ERR_METODO_NO_ENCONTRADO).

class A1 {
    A1(A1 a) { }
}

class B2 extends A1 {
    B2() {
        super(this);
        super(null);
    }

    void m1() {
        super();
        super.noExiste();
    }
}

class Init{
    static void main()
    { }
}
