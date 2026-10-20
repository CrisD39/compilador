///[Error:=|9]
// §11.4 -- Una expresion parentizada no es destino asignable: (x) = 3.
// Se reporta '=' en la linea 9.

class A1 {
    int a1;

    void m1() {
        (a1) = 3;
    }
}

class Init{
    static void main()
    { }
}
