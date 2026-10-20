///[Error:+|9]
// §11.2 -- No hay promocion numerica: char + int es error.
// Se reporta '+' en la linea 9.

class A1 {
    int a1;

    void m1() {
        a1 = 'a' + 1;
    }
}

class Init{
    static void main()
    { }
}
