///[SinErrores]
// El acceso con super combinado con los otros logros habilitados: postfijos
// sobre super.x, instanceof sobre super.o, super.x en las tres partes de un
// for clasico, un for-each sobre super.lista (conforma con
// MiniIterable<String>), super.debugPrint(...) (metodo estatico de Object,
// llamado con super desde un metodo de instancia) y un metodo no void que
// retorna con super en todos los caminos.

class L0 implements MiniIterable<String> {
    void start() { }
    String next() { return "a"; }
    boolean hasNext() { return false; }
}

class A1 {
    int x;
    Object o;
    L0 lista;
    boolean f() { return true; }
}

class B2 extends A1 {
    String s;

    void m1() {
        super.x++;
        super.x--;
        if (super.o instanceof B2) {
            super.x = 1;
        }
        for (super.x = 0; super.x < 3; super.x = super.x + 1) {
            super.debugPrint(super.x);
        }
        for (String e : super.lista) {
            s = e;
        }
    }

    int m2() {
        if (super.f()) {
            return super.x;
        } else {
            return 0;
        }
    }
}

class Init{
    static void main()
    { }
}
