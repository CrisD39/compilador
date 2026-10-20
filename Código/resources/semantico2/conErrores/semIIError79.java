///[Error:;|7]
// §12.2 -- Crear un arreglo no es llamar a un constructor: no es sentencia.
// Se reporta ';' en la linea 7.

class A1 {
    void m1() {
        new int[3];
    }
}

class Init{
    static void main()
    { }
}
