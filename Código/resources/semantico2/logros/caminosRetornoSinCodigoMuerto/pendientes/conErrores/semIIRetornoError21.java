///[Error:x|14]
///[Error:=|18]
///[Error:m2|21]
// Multi-deteccion entre unidades: los errores de flujo salen junto con los de
// tipos y en orden de programa. ERR_SENTENCIA_INALCANZABLE en el constructor,
// ERR_ASIGNACION_INCOMPATIBLE en m1 y ERR_FALTA_RETORNO en m2.

class A1 {
    int x;
    boolean b;

    public A1() {
        return;
        x = 1;
    }

    void m1() {
        x = b;
    }

    int m2() {
        x = 2;
    }
}

class Init{
    static void main()
    { }
}
