///[Error:x|6]
// Declaracion como cuerpo directo de un if (spec 0009, D9): el error va
// sobre el primer nombre, como antes.

class A1 {
    void m1(boolean c) { if (c) int x = 1; }
}

class Init{
    static void main()
    { }
}
