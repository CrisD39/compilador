///[Error:super|16]
///[Error:super|21]
///[Error:super|21]
// Fuera de lugar (ERR_SUPER_FUERA_DE_LUGAR, sobre super): super(a) despues
// de una declaracion de local, y super(v) despues de un return. El segundo
// es ademas inalcanzable (ERR_SENTENCIA_INALCANZABLE, sobre super) y,
// como toda sentencia inalcanzable, despues se chequea (spec 0006, D10).

class A1 {
    A1(int v) { }
}

class B2 extends A1 {
    B2() {
        var a = 1;
        super(a);
    }

    B2(int v) {
        return;
        super(v);
    }
}

class Init{
    static void main()
    { }
}
