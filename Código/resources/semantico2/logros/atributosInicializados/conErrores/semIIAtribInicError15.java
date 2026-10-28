///[Error:?|7]
// Un ternario mal tipado en el inicializador da su propio error: "x" no
// conforma con el tipo esperado int (ERR_TERNARIO_RAMA_INCOMPATIBLE, sobre
// el ?: spec 0007), sin un segundo error de conformidad sobre el =.

class A1 {
    int a = true ? 1 : "x";
}

class Init{
    static void main()
    { }
}
