///[Error:m2|8]
// §10.2 -- El argumento no conforma con el formal: se pasa un A1 donde se espera B2
// (subtipo invertido).
// Se reporta 'm2' en la linea 8.

class A1 {
    void m1() {
        m2(new A1());
    }

    void m2(B2 p1) {}
}

class B2 extends A1 {}

class Init{
    static void main()
    { }
}
