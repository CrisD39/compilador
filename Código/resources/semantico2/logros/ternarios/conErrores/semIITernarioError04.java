///[Error:?|12]
// Las dos ramas void tambien son error, como en Java
// (ERR_TERNARIO_RAMA_VOID, sobre el ?), y no ERR_VAR_SIN_TIPO sobre v: el
// ternario falla antes.

class A1 {
    void m() {}

    void n() {}

    void m1(boolean c) {
        var v = c ? m() : n();
    }
}

class Init{
    static void main()
    { }
}
