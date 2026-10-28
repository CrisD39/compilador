///[Error:?|7]
// Un ternario mal tipado en el inicializador da su propio error
// (ERR_TERNARIO_INCOMPATIBLE, sobre el ?: logro Ternarios, spec 0007), y el
// atributo no suma un segundo error de conformidad sobre el =.

class A1 {
    int a = true ? 1 : "x";
}

class Init{
    static void main()
    { }
}
