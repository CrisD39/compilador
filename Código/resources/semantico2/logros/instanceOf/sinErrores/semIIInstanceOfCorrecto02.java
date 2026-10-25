///[SinErrores]
// No hace falta que los tipos sean iguales ni que se baje: subir, el
// mismo tipo, null y Object a cualquier clase tambien valen, como en Java.

class A1 {
    void m1(B2 b, Object o) {
        var r1 = b instanceof A1;
        var r2 = b instanceof B2;
        var r3 = null instanceof A1;
        var r4 = o instanceof B2;
        var r5 = b instanceof Object;
        var r6 = o instanceof String;
    }
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
