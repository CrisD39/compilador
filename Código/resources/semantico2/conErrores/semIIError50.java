///[Error:+|9]
// §11.2 -- + binario exige int, int.
// Se reporta '+' en la linea 9.

class A1 {
    int a1;

    void m1() {
        a1 = 1 + true;
    }
}

class Init{
    static void main()
    { }
}
