///[Error:b2|19]
// §10.1 -- e.x busca x en el tipo ESTATICO de e (A1), no en la clase dinamica: el
// atributo existe solo en B2.
// Se reporta 'b2' en la linea 19.

class A1 {
    int a1;
}

class B2 extends A1 {
    int b2;
}

class C3 {
    A1 p;

    void m1() {
        p = new B2();
        p.b2 = 4;
    }
}

class Init{
    static void main()
    { }
}
