///[SinErrores]
// El postfijo vale en la inicializacion y en la actualizacion sobre cualquier
// destino asignable int: una local ya declarada, un atributo con this, un
// atributo de otro objeto y un elemento de arreglo (ADR 0013).

class B1 {
    int b1;
}

class A1 {
    int a1;
    B1 b;
    int[] arr;

    void m1() {
        int i = 0;
        for (i++; i < 10; this.a1--) {
            a1 = i;
        }
        for (b.b1++; b.b1 < 10; arr[i]++) {
            i = b.b1;
        }
    }
}

class Init{
    static void main()
    { }
}
