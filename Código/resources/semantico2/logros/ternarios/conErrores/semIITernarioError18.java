///[Error:v|7]
// c ? null : null tiene el tipo null, asi que en un var falla igual que
// var v = null; (ERR_VAR_SIN_TIPO, sobre v).

class A1 {
    void m1(boolean c) {
        var v = c ? null : null;
    }
}

class Init{
    static void main()
    { }
}
