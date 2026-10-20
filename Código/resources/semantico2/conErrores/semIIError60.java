///[Error:==|9]
// §11.2 -- == entre clases no relacionadas: ninguna conforma con la otra.
// Se reporta '==' en la linea 9.

class A1 {}

class B2 {
    void m1(A1 p1) {
        var v = p1 == this;
    }
}

class Init{
    static void main()
    { }
}
