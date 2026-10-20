///[Error:m2|8]
// §10.2 -- Con la aridad elegida no se busca otra variante por tipos: m2/1 recibe int y
// se pasa boolean, aunque no haya otro m2/1.
// Se reporta 'm2' en la linea 8.

class A1 {
    void m1() {
        m2(true);
    }

    void m2(int p1) {}
    void m2(int p1, int p2) {}
}

class Init{
    static void main()
    { }
}
