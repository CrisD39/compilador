///[Error:?|7]
// Un ternario en el inicializador sigue siendo un logro no implementado: da
// ERR_NO_SOPORTADO sobre el ? (spec 0003, D11), y el atributo no suma un
// segundo error (ni "no soportado" sobre su nombre ni de conformidad).

class A1 {
    int a = true ? 1 : 2;
}

class Init{
    static void main()
    { }
}
