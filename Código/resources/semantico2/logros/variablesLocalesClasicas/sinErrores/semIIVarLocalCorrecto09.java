///[SinErrores]
// Bloques disjuntos (spec 0009, D5): la primera x ya no es visible cuando se
// declara la segunda, asi que no chocan.

class A1 {
    void m1() {
        { int x = 1; }
        { boolean x = true; }
    }
}

class Init{
    static void main()
    { }
}
