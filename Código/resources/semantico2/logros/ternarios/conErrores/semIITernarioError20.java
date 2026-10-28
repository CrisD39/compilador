///[Error:?|13]
///[Error:noExiste|14]
// Orden: la condicion se chequea antes que las ramas. En la primera
// sentencia la condicion int gana a la rama "a" (ERR_CONDICION_NO_BOOLEAN,
// sobre el ?). En la segunda, el error de una rama sale en su propio token
// (ERR_VARIABLE_NO_DECLARADA, sobre noExiste), no en el ?.

class A1 {
    int i;
    int x;

    void m1(boolean c) {
        i = 1 ? x : "a";
        i = c ? noExiste : 1;
    }
}

class Init{
    static void main()
    { }
}
