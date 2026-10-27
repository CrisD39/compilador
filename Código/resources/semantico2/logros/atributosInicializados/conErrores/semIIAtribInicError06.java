///[Error:+|7]
// Un error adentro del inicializador sale sobre su propio token
// (ERR_OPERADOR_INCOMPATIBLE, sobre el +) y es el unico error del atributo:
// no se suma un error de conformidad sobre el =.

class A1 {
    int a = 1 + true;
}

class Init{
    static void main()
    { }
}
