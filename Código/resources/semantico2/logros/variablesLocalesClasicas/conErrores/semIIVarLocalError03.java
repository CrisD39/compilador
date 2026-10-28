///[Error:x|8]
// Local duplicada en la misma sentencia (spec 0009, D5): el segundo x
// choca con el primero, ya declarado.

class A1 {
    void m1() {
        int x,
            x;
    }
}

class Init{
    static void main()
    { }
}
