///[Error:r1|7]
// No se admite la variable asociada a la prueba (pattern matching de
// Java 16): despues del tipo se espera el cierre (error sintactico, sobre r1).

class A1 {
    void m1(Object o) {
        var r = o instanceof A1 r1;
    }
}

class Init{
    static void main()
    { }
}
