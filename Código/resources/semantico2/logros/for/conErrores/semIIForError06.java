///[Error:=|9]
// La actualizacion es una asignacion, pero se chequea: un boolean no se
// asigna a la i int (ERR_ASIGNACION_INCOMPATIBLE sobre el =).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i = true) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
