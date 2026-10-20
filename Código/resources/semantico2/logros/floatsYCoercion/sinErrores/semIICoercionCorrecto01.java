///[SinErrores]
// Un float acepta un int (coercion int->float) y un int acepta un char
// (char->int).

class A1 {
    void m1() {
        var f = 1.5;
        f = 2;
        int i = 'a';
    }
}

class Init{
    static void main()
    { }
}
