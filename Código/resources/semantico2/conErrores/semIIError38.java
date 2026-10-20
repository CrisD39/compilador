///[Error:=|15]
// §9.2 -- Una interfaz no conforma con una clase que la implementa.
// Se reporta '=' en la linea 15.

interface I1 {
    void m2();
}

class A1 implements I1 {
    A1 a1;

    void m2() {}

    void m1(I1 p1) {
        a1 = p1;
    }
}

class Init{
    static void main()
    { }
}
