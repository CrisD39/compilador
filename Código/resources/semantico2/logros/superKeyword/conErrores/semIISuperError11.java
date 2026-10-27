///[Error:super|12]
///[Error:super|17]
// Fuera de lugar en dos constructores (ERR_SUPER_FUERA_DE_LUGAR, sobre
// super): despues de una sentencia vacia (que sea primera es literal), y
// como segunda llamada (la primera esta en lugar; se marca la segunda).

class A1 { }

class B2 extends A1 {
    B2() {
        ;
        super();
    }

    B2(int v) {
        super();
        super();
    }
}

class Init{
    static void main()
    { }
}
