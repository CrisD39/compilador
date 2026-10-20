///[SinErrores]
// §9.5 -- null conforma con clases, interfaces, parametros de tipo y arreglos.

interface I1 {
    void m2();
}

class Caja<T> {
    T contenido;
    A1 a;
    I1 i;
    int[] arr;

    void m1() {
        contenido = null;
        a = null;
        i = null;
        arr = null;
    }
}

class A1 {}

class Init{
    static void main()
    { }
}
