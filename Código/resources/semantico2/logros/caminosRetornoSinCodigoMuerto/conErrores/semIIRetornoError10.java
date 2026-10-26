///[Error:{|12]
///[Error:x|19]
// ERR_SENTENCIA_INALCANZABLE: el cuerpo de un bucle cuya condicion es la
// constante false es inalcanzable (igual que en Java). Se reporta sobre el
// primer token del cuerpo: la llave del bloque, o el nombre asignado si el
// cuerpo es una sola sentencia.

class A1 {
    int x;

    void m1() {
        while (false) {
            x = 1;
        }
    }

    void m2() {
        for (var i = 0; false; i = i + 1)
            x = i;
    }
}

class Init{
    static void main()
    { }
}
