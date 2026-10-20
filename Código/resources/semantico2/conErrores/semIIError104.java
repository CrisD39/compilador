///[Error:z|9]
// §12.5 -- El codigo inalcanzable igual se chequea: variable no declarada despues de un
// return.
// Se reporta 'z' en la linea 9.

class A1 {
    int m1() {
        return 1;
        var y = z;
    }
}

class Init{
    static void main()
    { }
}
