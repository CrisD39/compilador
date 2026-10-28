///[Error:m|20]
///[Error:x|21]
///[Error:m|22]
///[Error:=|23]
// El encadenado de un acceso con super es el de siempre: super.m con m que
// solo es metodo (ERR_ATRIBUTO_NO_ENCONTRADO, sobre m), super.x() con x que
// solo es atributo (ERR_METODO_NO_ENCONTRADO, sobre x), super.m(1) sin
// match de aridad (ERR_METODO_NO_ENCONTRADO, sobre m) y asignar el resultado
// de un metodo void (ERR_ASIGNACION_INCOMPATIBLE, sobre =).

class A1 {
    int x;
    void m() { }
}

class B2 extends A1 {
    int i;

    void m1() {
        var a = super.m;
        super.x();
        super.m(1);
        i = super.m();
    }
}

class Init{
    static void main()
    { }
}
