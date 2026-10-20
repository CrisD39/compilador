///[Error:A1|12]
// §10.3 -- Se selecciona el constructor por aridad: A1 solo tiene constructor de 1
// parametro.
// Se reporta 'A1' en la linea 12.

class A1 {
    public A1(int p1) {}
}

class B2 {
    void m1() {
        var v = new A1();
    }
}

class Init{
    static void main()
    { }
}
