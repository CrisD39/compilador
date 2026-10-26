///[Error:var|11]
///[Error:z|11]
// Viene de semIIError104 de la base (12.5): el codigo inalcanzable igual se
// chequea. Primero ERR_SENTENCIA_INALCANZABLE sobre var y despues, en la misma
// linea, ERR_VAR_NO_DECLARADA sobre z.
// Se reportan 'var' y 'z' en la linea 11.

class A1 {
    int m1() {
        return 1;
        var y = z;
    }
}

class Init{
    static void main()
    { }
}
