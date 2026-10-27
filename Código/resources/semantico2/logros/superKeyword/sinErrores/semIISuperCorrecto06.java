///[SinErrores]
// Contextos de instancia: super.b en el inicializador de un atributo (no es
// referencia adelantada: no es un nombre suelto y b lo declara A1), y
// accesos con super en un constructor, despues de super() y en otro sin
// super(...). Fuera de los argumentos de super(...) el objeto actual se usa
// normalmente.

class A1 {
    int b = 1;

    void m() { }
}

class B2 extends A1 {
    int a = super.b;

    B2() {
        super();
        super.m();
        super.b = a;
    }

    B2(int v) {
        super.b = v;
        super.m();
    }
}

class Init{
    static void main()
    { }
}
