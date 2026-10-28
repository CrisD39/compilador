///[Error:?|8]
// En un metodo int, el retorno es el tipo esperado del ternario: la rama
// "a" no conforma con int (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre el ?), y
// el return no suma un ERR_RETORNO_INCORRECTO.

class A1 {
    int m1(boolean c) {
        return c ? 1 : "a";
    }
}

class Init{
    static void main()
    { }
}
