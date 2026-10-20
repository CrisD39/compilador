///[Error:=|7]
// §11.9 -- Crear un arreglo no designa un destino asignable.
// Se reporta '=' en la linea 7.

class A1 {
    void m1() {
        new int[3] = null;
    }
}

class Init{
    static void main()
    { }
}
