///[SinErrores]
// Acceso con super: selecciona el miembro como se ve desde la superclase y
// el encadenado es el de siempre. super.x leido y asignado, super.v[0],
// super.get().y, super.get().n() como sentencia, miembros que la superclase
// hereda del abuelo, un metodo redefinido llamado con super (la version del
// padre), super.mEstatico() desde un metodo de instancia y super.toString()
// en una clase sin extends (su superclase es Object).

class C0 {
    int y;
    void n() { }
}

class A0 {
    int deAbuelo;
    void saludar() { }
}

class A1 extends A0 {
    int x;
    int[] v;

    C0 get() { return new C0(); }
    int m1() { return x; }
    static int mEstatico() { return 1; }
}

class B2 extends A1 {
    int m1() {
        return super.m1() + 1;
    }

    void m2() {
        super.x = 3;
        var a = super.x + super.v[0];
        super.v[0] = a;
        var b = super.get().y;
        super.get().n();
        super.deAbuelo = super.mEstatico() + b;
        super.saludar();
    }
}

class D3 {
    String m3() {
        return super.toString();
    }
}

class Init{
    static void main()
    { }
}
