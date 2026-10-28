///[SinErrores]
// null en un ternario (spec 0007, D4): sale de conformaCon, sin codigo
// especial. null con una clase, un arreglo o un T, de cualquiera de los dos
// lados, da esa referencia. null : null tiene el tipo null, que conforma
// con un A1. En un var, c ? null : new A1() es A1, no null.

class A1 {}

class Caja<T> {
    A1 a;
    int[] v;
    T t;

    void m1(boolean c, T otro) {
        a = c ? null : new A1();
        a = c ? new A1() : null;
        v = c ? null : new int[3];
        v = c ? new int[3] : null;
        t = c ? null : otro;
        t = c ? otro : null;
        a = c ? null : null;
        var w = c ? null : new A1();
        a = w;
    }
}

class Init{
    static void main()
    { }
}
