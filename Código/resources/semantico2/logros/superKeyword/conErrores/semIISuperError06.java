///[Error:=|15]
///[Error:=|26]
// Un acceso con super mal tipado da el mismo error que con cualquier
// receptor: i = super.nombre con nombre String, y en D4 extends C3<String>,
// j = super.dato con dato de tipo T, que se ve como String
// (ERR_ASIGNACION_INCOMPATIBLE, sobre =).

class A1 {
    String nombre;
}

class B2 extends A1 {
    int i;
    void m1() {
        i = super.nombre;
    }
}

class C3<T> {
    T dato;
}

class D4 extends C3<String> {
    int j;
    void m2() {
        j = super.dato;
    }
}

class Init{
    static void main()
    { }
}
