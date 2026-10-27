///[Error:=|7]
// Una llamada a un metodo void no produce valor: no puede inicializar un
// atributo, igual que no puede ser el lado derecho de una asignacion
// (ERR_ASIGNACION_INCOMPATIBLE, sobre el =).

class A1 {
    int a = nada();

    void nada() { }
}

class Init{
    static void main()
    { }
}
