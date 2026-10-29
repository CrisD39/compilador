///[SinErrores]
// La actualizacion puede ser un postfijo: i++ cuenta como asignacion porque,
// con el logro Postfijos, ya es una sentencia valida (ADR 0013). Antes de la
// ADR era semIIForError02. Tambien vale i-- con una local clasica.

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i++) {
            a1 = i;
        }
        for (int j = 10; j > 0; j--) {
            a1 = j;
        }
    }
}

class Init{
    static void main()
    { }
}
