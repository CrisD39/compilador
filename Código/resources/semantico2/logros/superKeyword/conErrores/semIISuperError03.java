///[Error:m|14]
// super.m() con m que solo viene de la interfaz implementada: una clase con
// implements no tiene extends (ADR 0005), asi que su superclase es Object,
// que no tiene m (ERR_METODO_NO_ENCONTRADO, sobre m).

interface I1 {
    void m();
}

class A1 implements I1 {
    void m() { }

    void m2() {
        super.m();
    }
}

class Init{
    static void main()
    { }
}
