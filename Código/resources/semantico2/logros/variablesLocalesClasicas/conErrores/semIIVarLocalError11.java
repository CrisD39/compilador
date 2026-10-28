///[Error:?|6]
// Rama de ternario incompatible con el tipo declarado (spec 0009, D5, spec
// 0007): el error va sobre el "?", sin un segundo error sobre el "=".

class A1 {
    void m1(boolean c) { int x = c ? 1 : true; }
}

class Init{
    static void main()
    { }
}
