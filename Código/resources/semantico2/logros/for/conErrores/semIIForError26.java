///[Error:for|13]
///[Error:=|14]
///[Error:for|15]
// Multi-deteccion: un for-each con error corta solo esa sentencia, y el
// chequeo sigue con la siguiente (ERR_FOREACH_NO_ITERABLE en el primero,
// ERR_ASIGNACION_INCOMPATIBLE en el medio, ERR_FOREACH_TIPO_INCOMPATIBLE en
// el ultimo).

class A1 {
    int a1;

    void m1(String s, MiniIterable<String> it) {
        for (String x : s) { a1 = 1; }
        a1 = s;
        for (int x : it) { a1 = x; }
    }
}

class Init{
    static void main()
    { }
}
