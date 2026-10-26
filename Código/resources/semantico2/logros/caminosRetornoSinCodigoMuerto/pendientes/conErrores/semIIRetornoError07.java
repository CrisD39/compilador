///[Error:x|11]
// ERR_SENTENCIA_INALCANZABLE: la asignacion despues de un return es
// inalcanzable; se reporta sobre su primer token (el nombre asignado).

class A1 {
    int x;

    int m1() {
        x = 1;
        return 1;
        x = 2;
    }
}

class Init{
    static void main()
    { }
}
