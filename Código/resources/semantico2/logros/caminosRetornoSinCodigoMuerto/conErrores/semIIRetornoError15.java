///[Error:x|11]
///[Error:x|16]
// ERR_SENTENCIA_INALCANZABLE tambien en un constructor y en un metodo void:
// despues de return; no se llega nunca.

class A1 {
    int x;

    public A1() {
        return;
        x = 1;
    }

    void m1() {
        return;
        x = 1;
    }
}

class Init{
    static void main()
    { }
}
