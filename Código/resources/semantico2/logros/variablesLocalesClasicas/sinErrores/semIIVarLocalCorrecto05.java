///[SinErrores]
// Locales de arreglo (spec 0009, D2 y D4): de primitivo con inicializador,
// de clase sin inicializador, y del parametro de tipo T de la clase.

class A1 {}

class Caja<T> {
    void m1() {
        int[] a = new int[3];
        A1[] b;
        T[] t;
        a[0] = 1;
    }
}

class Init{
    static void main()
    { }
}
