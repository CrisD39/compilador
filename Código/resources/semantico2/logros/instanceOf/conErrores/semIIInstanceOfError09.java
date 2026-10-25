///[Error:Zeta|7]
// El tipo de destino tiene que estar declarado (ERR_TIPO_NO_DECLARADO de
// la Etapa 3, sobre el nombre de la clase).

class A1 {
    void m1(A1 a) {
        var r = a instanceof Zeta;
    }
}

class Init{
    static void main()
    { }
}
