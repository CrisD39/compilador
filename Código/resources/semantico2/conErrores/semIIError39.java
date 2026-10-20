///[Error:=|11]
// §9.2 -- Clases no relacionadas por herencia no conforman entre si.
// Se reporta '=' en la linea 11.

class A1 {}

class B2 {
    A1 a1;

    void m1() {
        a1 = new B2();
    }
}

class Init{
    static void main()
    { }
}
