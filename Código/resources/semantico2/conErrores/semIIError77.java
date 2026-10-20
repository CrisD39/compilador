///[Error:;|14]
// §12.2 -- Si hay encadenado, el ultimo debe ser una llamada: crear().a1 termina en un
// atributo.
// Se reporta ';' en la linea 14.

class A1 {
    int a1;

    A1 crear() {
        return new A1();
    }

    void m1() {
        crear().a1;
    }
}

class Init{
    static void main()
    { }
}
