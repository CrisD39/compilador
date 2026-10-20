///[Error:m2|7]
// §10.2 -- La resolucion es por (nombre, aridad): existe m2/1 pero no m2/2.
// Se reporta 'm2' en la linea 7.

class A1 {
    void m1() {
        m2(1, 2);
    }

    void m2(int p1) {}
}

class Init{
    static void main()
    { }
}
