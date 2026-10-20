///[Error:a1|9]
// §10.1 -- En un metodo estatico no se permite el acceso implicito a un atributo.
// Se reporta 'a1' en la linea 9.

class A1 {
    int a1;

    static void m1() {
        a1 = 3;
    }
}

class Init{
    static void main()
    { }
}
