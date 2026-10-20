///[Error:B2|16]
// §10.3 -- Si la clase declara un constructor no se agrega el por defecto; tampoco se
// buscan constructores en ancestros.
// Se reporta 'B2' en la linea 16.

class A1 {
    public A1() {}
}

class B2 extends A1 {
    public B2(int p1) {}
}

class C3 {
    void m1() {
        var v = new B2();
    }
}

class Init{
    static void main()
    { }
}
