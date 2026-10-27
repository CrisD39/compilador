///[Error:z|7]
// Una variable no declarada en un inicializador da el mismo error que en un
// metodo (ERR_VAR_NO_DECLARADA, sobre el nombre): un inicializador no tiene
// locales ni parametros, y z no es un atributo de A1 ni de sus ancestros.

class A1 {
    int a = z;
}

class Init{
    static void main()
    { }
}
