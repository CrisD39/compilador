///[Error:A1|11]
// §11.7 -- El argumento del constructor no conforma con su parametro formal.
// Se reporta 'A1' en la linea 11.

class A1 {
    public A1(int p1) {}
}

class B2 {
    void m1() {
        var v = new A1(true);
    }
}

class Init{
    static void main()
    { }
}
