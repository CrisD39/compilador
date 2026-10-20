///[SinErrores]
// §11.2 -- == entre char, entre referencias conformantes en alguna direccion, con null
// y entre String.

class A1 {}

class B2 extends A1 {
    void m1(char c1, char c2, A1 p1, B2 p2, String s) {
        var v1 = c1 == c2;
        var v2 = p1 == p2;
        var v3 = p2 != p1;
        var v4 = p1 == null;
        var v5 = s == "hola";
        var v6 = new int[2] != null;
    }
}

class Init{
    static void main()
    { }
}
