///[SinErrores]
// Llamada al constructor de la superclase en lugar (primera sentencia):
// super() sin extends (Object tiene el constructor por defecto),
// super(1, true) eligiendo por aridad entre constructores sobrecargados,
// super() con una superclase que no declara constructores, y un
// constructor sin super(...) cuya superclase solo tiene constructores con
// parametros (MiniJava no agrega la llamada implicita, Parte I 6.3).

class C1 {
    C1() {
        super();
    }
}

class A2 {
    A2() { }
    A2(int a) { }
    A2(int a, boolean b) { }
}

class B3 extends A2 {
    B3() {
        super(1, true);
    }
}

class D4 { }

class E5 extends D4 {
    E5() {
        super();
    }
}

class F6 {
    F6(int v) { }
}

class G7 extends F6 {
    G7() { }
}

class Init{
    static void main()
    { }
}
