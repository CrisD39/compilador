///[Error:if|12]
///[Error:z|14]
// ERR_SENTENCIA_INALCANZABLE adentro de una sentencia inalcanzable: el if es
// inalcanzable, y adentro de su rama se siguen buscando inalcanzables, como
// hace javac (z = 3 viene despues de otro return). No hay falta de retorno.

class A1 {
    int z;

    int m1(boolean b) {
        return 1;
        if (b) {
            return 2;
            z = 3;
        }
    }
}

class Init{
    static void main()
    { }
}
